/**
 * 
 */
package com.szas.android.SZASApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.szas.sync.ContentObserver;
import com.szas.sync.Tuple;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.local.LocalDAO;
import com.szas.sync.local.LocalTuple;
import com.szas.sync.remote.RemoteTuple;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncContext;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.SQLException;
import android.net.Uri;

/**
 * @author Pawel Szafer email pszafer@gmail.com
 * 
 *         no comments because based on ContentProvider
 */

public class SQLLocalDAO implements LocalDAO<Tuple>{ 
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

private static class DBContentProvider extends ContentProvider {

	public static final String AUTHORITY = "com.szas.android.SZASApplication";
	private static final String DBNAME = "szas";
	private static final int DBVERSION = 2;
	private static final String DBTABLE = "szas_table";
	private static final String DBMIME = "vnd.android.cursor.dir/stroringdata.szas";

	private static final String LOGTAG = "SZAS_ANDROID_PROJECT_DB";
	private static final UriMatcher sUriMatcher;
	private static final int DB_TABLE_ID = 1;
	private static final int DB_TABLE_ITEM_ID = 2;
	private static final String appName = "SZASApplication";

	public static final Uri CONTENT_URI = Uri.parse("content://" + appName
			+ "/" + DBTABLE);
	public static final String DBCOL_ID = "_id";
	public static final String DBCOL_syncTimestamp = "syncTimestamp";
	public static final String DBCOL_status = "status";
	public static final String DBCOL_form = "form";

	private DatabaseHelper databaseHelper;
	private static HashMap<String, String> szasProjectionMap;

	private static final String DBCREATE = "create table " + DBTABLE + " ("
			+ DBCOL_ID + " TEXT NOT NULL primary key," + DBCOL_syncTimestamp
			+ " TEXT not null," + // --sqlite nie ma long'ow
			DBCOL_status + " INTEGER not null," + // --inserting/updating/deleting/synced
			DBCOL_form + " TEXT not null )";
	
	private SyncContext syncContext = null;

	@Override
	public int delete(Uri arg0, String where, String[] whereArgs) {
		SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(arg0)) {
		case DB_TABLE_ID:
			count = sqLiteDatabase.delete(DBTABLE, where, whereArgs);
			break;
		case DB_TABLE_ITEM_ID:
			long itemId = Long.parseLong(arg0.getLastPathSegment());
			count = sqLiteDatabase.delete(DBTABLE, DBCOL_ID + " = ? ",
					new String[] { String.valueOf(itemId) });
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
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
		long rowID = sqLiteDatabase.insert(DBTABLE, null, contentValues);
		if (rowID > 0) {
			Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}
		throw new SQLException("Failed to insert ROW into " + arg0);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case DB_TABLE_ID:
			sqLiteQueryBuilder.setTables(DBTABLE);
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
			count = sqLiteDatabase.update(DBTABLE, values, where, whereArgs);
			break;
		case DB_TABLE_ITEM_ID:
			long itemId = Long.parseLong(uri.getLastPathSegment());
			count = sqLiteDatabase.update(DBTABLE, values, DBCOL_ID + " = ? ",
					new String[] { String.valueOf(itemId) });
			break;
		default:
			throw new IllegalArgumentException("Unkown URI");
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	/**
	 * Projection map to create Urimatcher
	 */
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(appName, DBTABLE, DB_TABLE_ID);
		sUriMatcher.addURI(appName, DBTABLE + "/#", DB_TABLE_ITEM_ID);

		szasProjectionMap = new HashMap<String, String>();

		szasProjectionMap.put(DBCOL_ID, DBCOL_ID);
		szasProjectionMap.put(DBCOL_syncTimestamp, DBCOL_syncTimestamp);
		szasProjectionMap.put(DBCOL_status, DBCOL_status);
		szasProjectionMap.put(DBCOL_form, DBCOL_form);
	}

	/**
	 * Class to create and update sqlite database
	 * 
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DBNAME, null, DBVERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DBCREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DBTABLE);
			onCreate(db);
		}
	}

}

/* (non-Javadoc)
 * @see com.szas.sync.UniversalDAO#getAll()
 */
@Override
public Collection<Tuple> getAll() {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see com.szas.sync.UniversalDAO#getById(long)
 */
@Override
public Tuple getById(long id) {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see com.szas.sync.UniversalDAO#insert(com.szas.sync.Tuple)
 */
@Override
public void insert(Tuple element) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see com.szas.sync.UniversalDAO#delete(com.szas.sync.Tuple)
 */
@Override
public void delete(Tuple element) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see com.szas.sync.UniversalDAO#update(com.szas.sync.Tuple)
 */
@Override
public void update(Tuple element) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see com.szas.sync.ContentObserverProvider#addContentObserver(com.szas.sync.ContentObserver)
 */
@Override
public void addContentObserver(ContentObserver contentObserver) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see com.szas.sync.ContentObserverProvider#removeContentObserver(com.szas.sync.ContentObserver)
 */
@Override
public boolean removeContentObserver(ContentObserver contentObserver) {
	// TODO Auto-generated method stub
	return false;
}

/* (non-Javadoc)
 * @see com.szas.sync.local.LocalDAO#getElementsToSync()
 */
@Override
public ArrayList<LocalTuple<Tuple>> getElementsToSync() {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see com.szas.sync.local.LocalDAO#getUnknownElementsToSync()
 */
@Override
public ArrayList<Object> getUnknownElementsToSync() {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see com.szas.sync.local.LocalDAO#getLastTimestamp()
 */
@Override
public long getLastTimestamp() {
	// TODO Auto-generated method stub
	return 0;
}

/* (non-Javadoc)
 * @see com.szas.sync.local.LocalDAO#setLastTimestamp(long)
 */
@Override
public void setLastTimestamp(long lastTimestamp) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see com.szas.sync.local.LocalDAO#setSyncedElements(java.util.ArrayList)
 */
@Override
public void setSyncedElements(ArrayList<RemoteTuple<Tuple>> syncedElements) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see com.szas.sync.local.LocalDAO#setSyncedUnknownElements(java.util.ArrayList)
 */
@Override
public void setSyncedUnknownElements(ArrayList<Object> syncedElements)
		throws WrongObjectThrowable {
	// TODO Auto-generated method stub
	
}
}
