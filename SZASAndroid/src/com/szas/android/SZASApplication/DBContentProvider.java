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
	public static String authority = "SZASApplication1";
	/**
	 * Database name
	 */
	private static final String DBNAME = "szas";

	/**
	 * Database version
	 */
	private static final int DBVERSION = 2;

	/**
	 * Mime address
	 */
	private static final String DBMIME = "vnd.android.cursor.dir/stroringdata.szas";

	/**
	 * Log tag XXX commented because not used
	 */
	// private static final String LOGTAG = "SZAS_ANDROID_PROJECT_DB";

	private UriMatcher syncedUriMatcher;
	private UriMatcher inProgressUriMatcher;
	private UriMatcher notSyncedUriMatcher;

	private static final int DB_TABLE_ID = 1; // not sure if need to be
												// different in each table

	private static final int DB_TABLE_ITEM_ID = 2; // like above

	private static DatabaseContentHelper databaseContentHelper;

	private HashMap<String, String> syncedProjectionMap;
	private HashMap<String, String> inProgressProjectionMap;
	private HashMap<String, String> notSyncedProjectionMap;

	/**
	 * Every content provider (SQLLocalDAO) extends DBContentProvider with
	 * another data, but structure of tables are the same
	 */
	public DBContentProvider() {
		createProjectionMap();
	}

	/**
	 * Projection map to create Urimatcher
	 */
	private void createProjectionMap() {
		syncedProjectionMap = DatabaseContentHelper.projectionMapSyncedElements;
		syncedUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		syncedUriMatcher.addURI(authority,
				DatabaseContentHelper.tableNameSyncedElements, DB_TABLE_ID);
		syncedUriMatcher.addURI(authority,
				DatabaseContentHelper.tableNameSyncedElements + "/#", DB_TABLE_ITEM_ID);

		inProgressProjectionMap = DatabaseContentHelper.projectionMapInProgressSyncingNotSyncedElements;
		inProgressUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		inProgressUriMatcher.addURI(authority,
				DatabaseContentHelper.tableNameInProgressSyncingElements, DB_TABLE_ID);
		inProgressUriMatcher.addURI(authority,
				DatabaseContentHelper.tableNameInProgressSyncingElements + "/#", DB_TABLE_ITEM_ID);

		notSyncedProjectionMap = DatabaseContentHelper.projectionMapInProgressSyncingNotSyncedElements;
		notSyncedUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		notSyncedUriMatcher.addURI(authority,
				DatabaseContentHelper.tableNameNotSyncedElements, DB_TABLE_ID);
		notSyncedUriMatcher.addURI(authority,
				DatabaseContentHelper.tableNameNotSyncedElements + "/#", DB_TABLE_ITEM_ID);
	}

	@Override
	public int delete(Uri arg0, String where, String[] whereArgs) {
		Contener contener = getUriMatcherAndSqliteDatabase(arg0, true);
		if (contener == null)
			return -1;
		UriMatcher sUriMatcher = contener.getsUriMatcher();
		SQLiteDatabase sqLiteDatabase = contener.getSqLiteDatabase();
		int count;
		switch (sUriMatcher.match(arg0)) {
		case DB_TABLE_ID:
			count = sqLiteDatabase.delete(contener.getTableName(), where,
					whereArgs);
			break;
		case DB_TABLE_ITEM_ID:
			long itemId = Long.parseLong(arg0.getLastPathSegment());
			count = sqLiteDatabase.delete(contener.getTableName(),
					DatabaseContentHelper.DBCOL_ID + " = ? ",
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
		UriMatcher uriMatcher = getUriMatcher(arg0);
		if (uriMatcher == null)
			return null;
		switch (uriMatcher.match(arg0)) {
		case DB_TABLE_ID:
			return DBMIME;
		default:
			throw new IllegalArgumentException("Wrong/Unknown URI");
		}
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		Contener contener = getUriMatcherAndSqliteDatabase(arg0, true);
		if (contener == null)
			return null;
		UriMatcher sUriMatcher = contener.getsUriMatcher();
		SQLiteDatabase sqLiteDatabase = contener.getSqLiteDatabase();
		String tableName = contener.getTableName();
		Uri contentUri = contener.getContentUri();
		if (sUriMatcher.match(arg0) != DB_TABLE_ID)
			throw new IllegalArgumentException("Unknown URI" + arg0);
		ContentValues contentValues;
		if (arg1 != null)
			contentValues = new ContentValues(arg1);
		else
			contentValues = new ContentValues();
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
		databaseContentHelper = new DatabaseContentHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Contener contener = getUriMatcherAndSqliteDatabase(uri, false);
		if (contener == null)
			return null;
		SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
		switch (contener.getsUriMatcher().match(uri)) {
		case DB_TABLE_ID:
			sqLiteQueryBuilder.setTables(contener.getTableName());
			sqLiteQueryBuilder.setProjectionMap(contener.getProjectionMap());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		Cursor cursor = sqLiteQueryBuilder.query(contener.getSqLiteDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		Contener contener = getUriMatcherAndSqliteDatabase(uri, true);
		if (contener == null)
			return -1;
		UriMatcher sUriMatcher = contener.getsUriMatcher();
		SQLiteDatabase sqLiteDatabase = contener.getSqLiteDatabase();
		String tableName = contener.getTableName();
		int count;
		switch (sUriMatcher.match(uri)) {
		case DB_TABLE_ID:
			count = sqLiteDatabase.update(tableName, values, where, whereArgs);
			break;
		case DB_TABLE_ITEM_ID:
			long itemId = Long.parseLong(uri.getLastPathSegment());
			count = sqLiteDatabase.update(tableName, values,
					DatabaseContentHelper.DBCOL_ID + " = ? ",
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
	public static void moveFromOneTableToAnother(String insertTable,
			String fromTable, boolean cleanInsertTable) {
		SQLiteDatabase sqLiteDatabase = databaseContentHelper.getWritableDatabase();
		DatabaseContentHelper.moveFromOneTableToAnother(sqLiteDatabase
				, insertTable,
				fromTable, cleanInsertTable);
	}

	/**
	 * Clean table
	 * 
	 * @param dbTable
	 *            table name
	 */
	public static void cleanTable(String dbTable) {
		DatabaseContentHelper.cleanTable(
				databaseContentHelper.getWritableDatabase(), dbTable);
	}

	/**
	 * 
	 * @param arg0
	 * @param isDBWritable
	 *            if set to true returns getWritableDatabase otherwise
	 *            getReadableDatabase
	 * @return
	 */
	private Contener getUriMatcherAndSqliteDatabase(Uri arg0,
			boolean isDBWritable) {
		Contener contener = null;
		String str = arg0.getLastPathSegment();
		if (str.equals(DatabaseContentHelper.tableNameSyncedElements)) {
			// if isDBWritable is true then return writableDB
			contener = new Contener();
			contener.setSqLiteDatabase(isDBWritable ? databaseContentHelper
					.getWritableDatabase() : databaseContentHelper
					.getReadableDatabase());
			contener.setsUriMatcher(syncedUriMatcher);
			contener.setTableName(DatabaseContentHelper.tableNameSyncedElements);
			contener.setContentUri(DatabaseContentHelper.contentUriSyncedElements);
			contener.setProjectionMap(syncedProjectionMap);
		} else if (str.equals(DatabaseContentHelper.tableNameInProgressSyncingElements)) {
			contener = new Contener();
			contener.setSqLiteDatabase(isDBWritable ? databaseContentHelper
					.getWritableDatabase() : databaseContentHelper
					.getReadableDatabase());
			contener.setsUriMatcher(inProgressUriMatcher);
			contener.setTableName(DatabaseContentHelper.tableNameInProgressSyncingElements);
			contener.setContentUri(DatabaseContentHelper.contentUriInProgressSyncingElements);
			contener.setProjectionMap(inProgressProjectionMap);
		} else if (str.equals(DatabaseContentHelper.tableNameNotSyncedElements)) {
			contener = new Contener();
			contener.setSqLiteDatabase(isDBWritable ? databaseContentHelper
					.getWritableDatabase() : databaseContentHelper
					.getReadableDatabase());
			contener.setsUriMatcher(notSyncedUriMatcher);
			contener.setTableName(DatabaseContentHelper.tableNameNotSyncedElements);
			contener.setContentUri(DatabaseContentHelper.contentUriNotSyncedElements);
			contener.setProjectionMap(notSyncedProjectionMap);
		}
		return contener;
	}

	/**
	 * 
	 * @param arg0
	 * @param isDBWritable
	 *            if set to true returns getWritableDatabase otherwise
	 *            getReadableDatabase
	 * @return
	 */
	private UriMatcher getUriMatcher(Uri arg0) {
		String str = arg0.getLastPathSegment();
		if (str.equals(DatabaseContentHelper.tableNameSyncedElements)) {
			return syncedUriMatcher;
		} else if (str.equals(DatabaseContentHelper.tableNameInProgressSyncingElements)) {
			return inProgressUriMatcher;
		} else if (str.equals(DatabaseContentHelper.tableNameNotSyncedElements)) {
			return notSyncedUriMatcher;
		}
		return null;
	}

	/**
	 * Elements
	 * 
	 */
	public static class DatabaseContentHelper extends SQLiteOpenHelper {

		/**
		 * 1 - synced, elements
		 * 2 - inProgress, SyncingElements
		 * 3 - ElementsToSync, notsynced
		 */
		/**
		 * @param context
		 * @param name
		 * @param version
		 */
		public DatabaseContentHelper(Context context) {
			super(context, DBNAME, null, DBVERSION);
		}

		/**
		 * Name of table
		 */
		public static final String tableNameSyncedElements = "szas_table1";
		public static final String tableNameInProgressSyncingElements = "szas_table2";
		public static final String tableNameNotSyncedElements = "szas_table3";
		
		/**
		 * Table URI
		 */
		public static final Uri contentUriSyncedElements = Uri.parse("content://" + authority
				+ "/" + tableNameSyncedElements);
		public static final Uri contentUriInProgressSyncingElements = Uri.parse("content://" + authority
				+ "/" + tableNameInProgressSyncingElements);
		public static final Uri contentUriNotSyncedElements = Uri.parse("content://" + authority
				+ "/" + tableNameNotSyncedElements);
		/**
		 * Columns of table
		 */
		public static final String DBCOL_ID = "_id";
		public static final String DBCOL_status = "status";
		public static final String DBCOL_type = "type";
		public static final String DBCOL_T = "T";

		/**
		 * Column indexes
		 */
		public static final int DBCOL_ID_INDEX = 0;
		public static final int DBCOL_T_INDEX = 1;
		public static final int DBCOL_type_INDEX = 2;
		public static final int DBCOL_status_INDEX = 3;
		

		/**
		 * String to create table
		 */
		private static final String createTableStringSyncedElements = "create table " + tableNameSyncedElements
				+ " (" + DBCOL_ID + " TEXT NOT NULL primary key," 
				+DBCOL_type + " TEXT not null,"
				+ DBCOL_T
				+ " TEXT not null )";
		private static final String createTableStringProgressSyncingElements = "create table " + tableNameInProgressSyncingElements
		+ " (" + DBCOL_ID + " TEXT NOT NULL primary key,"
		+ DBCOL_status + " INTEGER not null," + // --inserting/updating/deleting/synced
		DBCOL_type + " TEXT not null,"+
		DBCOL_T + " TEXT not null )";
		private static final String createTableStringNotSyncedElements = "create table " + tableNameNotSyncedElements
		+ " (" + DBCOL_ID + " TEXT NOT NULL primary key,"
		+ DBCOL_status + " INTEGER not null," + // --inserting/updating/deleting/synced
		DBCOL_type + " TEXT not null,"+
		DBCOL_T + " TEXT not null )";
		
		/**
		 * Hashmap of table
		 */
		public static HashMap<String, String> projectionMapSyncedElements = null;

		static {
			projectionMapSyncedElements = new HashMap<String, String>();
			projectionMapSyncedElements.put(DBCOL_ID, DBCOL_ID);
			projectionMapSyncedElements.put(DBCOL_T, DBCOL_T);
			projectionMapSyncedElements.put(DBCOL_type, DBCOL_type);
		}
		
		/**
		 * Hashmap of table
		 */
		public static HashMap<String, String> projectionMapInProgressSyncingNotSyncedElements = null;
		static {
			projectionMapInProgressSyncingNotSyncedElements = new HashMap<String, String>();
			projectionMapInProgressSyncingNotSyncedElements.put(DBCOL_ID, DBCOL_ID);
			projectionMapInProgressSyncingNotSyncedElements.put(DBCOL_status, DBCOL_status);
			projectionMapInProgressSyncingNotSyncedElements.put(DBCOL_T, DBCOL_T);
			projectionMapInProgressSyncingNotSyncedElements.put(DBCOL_type, DBCOL_type);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
		 * .sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(createTableStringSyncedElements);
			db.execSQL(createTableStringProgressSyncingElements);
			db.execSQL(createTableStringNotSyncedElements);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database
		 * .sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + tableNameSyncedElements);
			db.execSQL("DROP TABLE IF EXISTS " + tableNameInProgressSyncingElements);
			db.execSQL("DROP TABLE IF EXISTS " + tableNameNotSyncedElements);
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
		public static void moveFromOneTableToAnother(SQLiteDatabase db,
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
		public static void cleanTable(SQLiteDatabase db, String dbTable) {
			db.execSQL("DELETE FROM " + dbTable);
		}
	}

}
