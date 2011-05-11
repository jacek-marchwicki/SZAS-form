/**
 * 
 */
package com.szas.android.SZASApplication;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * @author pszafer@gmail.com
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class DBContentProvider extends ContentProvider {

	/**
	 * Authority name for ContentResolver
	 */
	//public static final String AUTHORITY = "SZASApplication";
	/**
	 * Database name
	 */
	private static final String DBNAME = "szas";

	/**
	 * Database version
	 */
	private static final int DBVERSION = 3;

	/**
	 * Mime address
	 */
	private static final String DBMIME = "vnd.android.cursor.dir/stroringdata.szas";

	/**
	 * Log tag XXX commented because not used
	 */
	// private static final String LOGTAG = "SZAS_ANDROID_PROJECT_DB";

	private UriMatcher sUriMatcher;

	private static final int DB_TABLE_ID = 1; // not sure if need to be
												// different in each table

	private static final int DB_TABLE_ITEM_ID = 2; // like above

	private String databaseColumnId = null;

	private DatabaseHelper databaseHelper;
	private HashMap<String, String> szasProjectionMap;

	private String createTableString = null;
	private String tableName = null;
	public Uri contentUri = null;

	/**
	 * Every content provider (SQLLocalDAO) extends DBContentProvider with
	 * another data, but structure of tables are the same
	 */
	public DBContentProvider(String authority, String DBTABLE, String DBCREATE, String DBCOL_ID,
			Uri ContentUri, HashMap<String, String> projectionMap) {
		this.createTableString = DBCREATE;
		this.tableName = DBTABLE;
		this.contentUri = ContentUri;
		this.databaseColumnId = DBCOL_ID;
		createProjectionMap(authority, projectionMap);
	}
	
	static {
		
	}

	/**
	 * Projection map to create Urimatcher
	 */
	private void createProjectionMap(String authority, HashMap<String, String> projectionMap) {
		szasProjectionMap = projectionMap;
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(authority, tableName, DB_TABLE_ID);
		sUriMatcher.addURI(authority, tableName + "/#", DB_TABLE_ITEM_ID);
	}

	@Override
	public int delete(Uri arg0, String where, String[] whereArgs) {
		SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(arg0)) {
		case DB_TABLE_ID:
			count = sqLiteDatabase.delete(tableName, where, whereArgs);
			break;
		case DB_TABLE_ITEM_ID:
			long itemId = Long.parseLong(arg0.getLastPathSegment());
			count = sqLiteDatabase.delete(tableName, databaseColumnId + " = ? ",
					new String[] { String.valueOf(itemId) });
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + arg0.getPath());
		}
		getContext().getContentResolver().notifyChange(arg0, null);
		return count;
	}

	@Override
	public String getType(Uri arg0) {
		switch (sUriMatcher.match(arg0)) {
		case DB_TABLE_ID:
			return DBMIME;
		default:
			throw new IllegalArgumentException("Wrong/Unknown URI");
		}
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		if (sUriMatcher.match(arg0) != DB_TABLE_ID)
			throw new IllegalArgumentException("Unknown URI" + arg0);
		ContentValues contentValues;
		if (arg1 != null)
			contentValues = new ContentValues(arg1);
		else
			contentValues = new ContentValues();
		SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
		long rowID = sqLiteDatabase.insert(tableName, null, contentValues);
		if (rowID > 0) {
			Uri noteUri = ContentUris.withAppendedId(contentUri, rowID);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}
		throw new SQLException("Failed to insert ROW into " + arg0);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext(),createTableString,tableName);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case DB_TABLE_ID:
			sqLiteQueryBuilder.setTables(tableName);
			sqLiteQueryBuilder.setProjectionMap(szasProjectionMap);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
		Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, projection,
				selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case DB_TABLE_ID:
			count = sqLiteDatabase.update(tableName, values, where, whereArgs);
			break;
		case DB_TABLE_ITEM_ID:
			long itemId = Long.parseLong(uri.getLastPathSegment());
			count = sqLiteDatabase.update(tableName, values, databaseColumnId + " = ? ",
					new String[] { String.valueOf(itemId) });
			break;
		default:
			throw new IllegalArgumentException("Unkown URI");
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/**
	 * Move fromTable to insertTable
	 * 
	 * @param insertTable
	 * @param fromTable
	 * @param cleanInsertTable
	 *            set true if you want clean insert table first
	 */
	public void moveFromOneTableToAnother(String insertTable,
			String fromTable, boolean cleanInsertTable) {
		databaseHelper.moveFromOneTableToAnother(
				databaseHelper.getWritableDatabase(), insertTable, fromTable,
				cleanInsertTable);
	}

	/**
	 * Clean table
	 * 
	 * @param dbTable
	 *            table name
	 */
	public void cleanTable(String dbTable) {
		databaseHelper
				.cleanTable(databaseHelper.getWritableDatabase(), dbTable);
	}

	/**
	 * Class to create and update sqlite database
	 * 
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private final String createTableString;
		private final String tableName;

		public DatabaseHelper(Context context, String createTableString, String tableName) {
			super(context, DBNAME, null, DBVERSION);
			this.createTableString = createTableString;
			this.tableName = tableName;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(createTableString);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
			onCreate(db);
		}

		/**
		 * Move content from fromTable to insetrtTable
		 * 
		 * @param db
		 *            database
		 * @param insertTable
		 *            table to insert content
		 * @param fromTable
		 *            source of content
		 * @param cleanInsertTable
		 *            clean insert table first
		 */
		public void moveFromOneTableToAnother(SQLiteDatabase db,
				String insertTable, String fromTable, boolean cleanInsertTable) {
			if (cleanInsertTable)
				db.execSQL("DELETE FROM " + insertTable);
			db.execSQL("INSERT INTO " + insertTable + " SELECT * FROM "
					+ fromTable);
		}

		/**
		 * Clean table
		 * 
		 * @param db
		 *            database
		 * @param dbTable
		 *            table name
		 */
		public void cleanTable(SQLiteDatabase db, String dbTable) {
			db.execSQL("DELETE FROM " + dbTable);
		}
	}
}
