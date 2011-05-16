/**
 * 
 */
package com.szas.android.SZASApplication;

import java.util.HashMap;

import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author pszafer@gmail.com
 *
 */
public class Contener{
	private UriMatcher sUriMatcher;
	private SQLiteDatabase sqLiteDatabase;
	private String tableName;
	private Uri contentUri;
	private HashMap<String, String> projectionMap;
	
	/**
	 * @param sUriMatcher the sUriMatcher to set
	 */
	public void setsUriMatcher(UriMatcher sUriMatcher) {
		this.sUriMatcher = sUriMatcher;
	}

	/**
	 * @return the sUriMatcher
	 */
	public UriMatcher getsUriMatcher() {
		return sUriMatcher;
	}

	/**
	 * @param sqLiteDatabase the sqLiteDatabase to set
	 */
	public void setSqLiteDatabase(SQLiteDatabase sqLiteDatabase) {
		this.sqLiteDatabase = sqLiteDatabase;
	}

	/**
	 * @return the sqLiteDatabase
	 */
	public SQLiteDatabase getSqLiteDatabase() {
		return sqLiteDatabase;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param contentUri the contentUri to set
	 */
	public void setContentUri(Uri contentUri) {
		this.contentUri = contentUri;
	}

	/**
	 * @return the contentUri
	 */
	public Uri getContentUri() {
		return contentUri;
	}

	/**
	 * @param projectionMap the projectionMap to set
	 */
	public void setProjectionMap(HashMap<String, String> projectionMap) {
		this.projectionMap = projectionMap;
	}

	/**
	 * @return the projectionMap
	 */
	public HashMap<String, String> getProjectionMap() {
		return projectionMap;
	}
}
