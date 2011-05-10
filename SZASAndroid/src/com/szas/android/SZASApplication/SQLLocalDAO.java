/**
 * 
 */
package com.szas.android.SZASApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.szas.sync.DAOObserver;
import com.szas.sync.Tuple;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.local.LocalDAO;
import com.szas.sync.local.LocalTuple;
import com.szas.sync.remote.RemoteTuple;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author pszafer@gmail.com
 * 
 */

public class SQLLocalDAO<T extends Tuple> implements LocalDAO<T>{ 

	ContentResolver contentResolver = null;
	Context context = null;
	
	Collection<DAOObserver> daoObservers = new ArrayList<DAOObserver>();
	
	/**
	 * 
	 */
	public SQLLocalDAO(Context context, ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
		this.context = context;
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Elements
	 *
	 */
	public static class SyncedContentProvider extends DBContentProvider{

		/**
		 * Name of table
		 */
		private static final String DBTABLE1 = "szas_table1";
		
		/**
		 * Table URI
		 */
		public static final Uri ContentUri = Uri.parse("content://" + AUTHORITY+ "/" + DBTABLE1);
		
		/**
		 * Columns of table
		 */
		public static final String DBCOL_ID = "_id";
		public static final String DBCOL_T = "T";
		
		/**
		 * Column indexes
		 */
		public static final int DBCOL_ID_INDEX = 0;
		public static final int DBCOL_T_INDEX = 1;
		
		/**
		 * String to create table
		 */
		private static final String DBCREATE1 = "create table " + DBTABLE1 + " ("
		+ DBCOL_ID + " TEXT NOT NULL primary key,"+
		DBCOL_T + " TEXT not null )";
		
		/**
		 * Hashmap of table
		 */
		private static HashMap<String, String> projectionMap = null;
		static{
			projectionMap = new HashMap<String, String>();
			projectionMap.put(DBCOL_ID, DBCOL_ID);
			projectionMap.put(DBCOL_T, DBCOL_T);
		}
		
		public SyncedContentProvider() {
			super(DBTABLE1, DBCREATE1, DBCOL_ID,  ContentUri, projectionMap);
		}
	}

	/**
	 * SyncingElements
	 *
	 */
	public static class InProgressContentProvider extends DBContentProvider{

		/**
		 * Name of table
		 */
		private static final String DBTABLE2 = "szas_table2";
		
		/**
		 * Table URI
		 */
		public static final Uri ContentUri = Uri.parse("content://" + AUTHORITY+ "/" + DBTABLE2);
		
		/**
		 * Columns of table
		 */
		public static final String DBCOL_ID = "_id";
		public static final String DBCOL_status = "status";
		public static final String DBCOL_T = "T";
		
		/**
		 * Column indexes
		 */
		public static final int DBCOL_ID_INDEX = 0;
		public static final int DBCOL_status_INDEX = 1;
		public static final int DBCOL_T_INDEX = 2;
		
		/**
		 * String to create table
		 */
		private static final String DBCREATE2 = "create table " + DBTABLE2 + " ("
		+ DBCOL_ID + " TEXT NOT NULL primary key,"+
		DBCOL_status + " INTEGER not null," + // --inserting/updating/deleting/synced
		DBCOL_T + " TEXT not null )";
		
		/**
		 * Hashmap of table
		 */
		private static HashMap<String, String> projectionMap = null;
		static{
			projectionMap = new HashMap<String, String>();
			projectionMap.put(DBCOL_ID, DBCOL_ID);
			projectionMap.put(DBCOL_status, DBCOL_status);
			projectionMap.put(DBCOL_T, DBCOL_T);
		}
		
		public InProgressContentProvider() {
			super(DBTABLE2, DBCREATE2, DBCOL_ID, ContentUri, projectionMap);
		}
	}
	
	/**
	 * ElementsToSync
	 *
	 */
	public static class NotSyncedContentProvider extends DBContentProvider{

		/**
		 * Name of table
		 */
		private static final String DBTABLE3 = "szas_table3";
		
		/**
		 * Table URI
		 */
		public static final Uri ContentUri = Uri.parse("content://" + AUTHORITY+ "/" + DBTABLE3);
		/**
		 * Columns of table
		 */
		public static final String DBCOL_ID = "_id";
		//public static final String DBCOL_syncTimestamp = "syncTimestamp";
		public static final String DBCOL_status = "status";
		public static final String DBCOL_T = "T";
		
		/**
		 * Column indexes
		 */
		public static final int DBCOL_ID_INDEX = 0;
		public static final int DBCOL_status_INDEX = 1;
		public static final int DBCOL_T_INDEX = 2;
		
		
		private static final String DBCREATE3 = "create table " + DBTABLE3 + " ("
		+ DBCOL_ID + " TEXT NOT NULL primary key,"+
		DBCOL_status + " INTEGER not null," + // --inserting/updating/deleting/synced
		DBCOL_T + " TEXT not null )";
		
		/**
		 * Hashmap of table
		 */
		private static HashMap<String, String> projectionMap = null;
		static{
			projectionMap = new HashMap<String, String>();
			projectionMap.put(DBCOL_ID, DBCOL_ID);
			projectionMap.put(DBCOL_status, DBCOL_status);
			projectionMap.put(DBCOL_T, DBCOL_T);
		}
		
		public NotSyncedContentProvider() {
			super(DBTABLE3, DBCREATE3, DBCOL_ID, ContentUri,  projectionMap);
		}

	}
	
	/* (non-Javadoc)
	 * @see com.szas.sync.UniversalDAO#getAll()
	 */
	@Override
	public Collection<T> getAll() {
		HashMap<Long, T> allElements = new HashMap<Long, T>();
		Cursor c1 = contentResolver.query(SyncedContentProvider.ContentUri, 
				new String[] {SyncedContentProvider.DBCOL_ID, 
				SyncedContentProvider.DBCOL_T}, 
				null, null, null);
		Cursor c2 = contentResolver.query(InProgressContentProvider.ContentUri, 
				new String[] {InProgressContentProvider.DBCOL_ID, 
					InProgressContentProvider.DBCOL_status,
					InProgressContentProvider.DBCOL_T}, 
				null, null, null);
		Cursor c3 = contentResolver.query(NotSyncedContentProvider.ContentUri, 
				new String[] {NotSyncedContentProvider.DBCOL_ID, 
				NotSyncedContentProvider.DBCOL_status, 
				NotSyncedContentProvider.DBCOL_T}, 
				null, null, null);
		try{
		if(c1.getCount() > 0)
		{
			c1.moveToFirst();
			do{
				allElements.put(c1.getLong(SyncedContentProvider.DBCOL_ID_INDEX), 
						new JSONDeserializer<T>().deserialize(c1.getString(SyncedContentProvider.DBCOL_T_INDEX)));
			}while (c1.moveToNext());
		}
		
		if(c2.getCount() > 0)
		{
			c2.moveToFirst();
			do{
				long objId = c2.getLong(InProgressContentProvider.DBCOL_ID_INDEX);
				allElements.remove(objId);
				LocalTuple.Status status = LocalTuple.Status.values()[c2.getInt(InProgressContentProvider.DBCOL_status_INDEX)];
				if(!status.equals(LocalTuple.Status.DELETING))
					allElements.put(objId,
						new JSONDeserializer<T>().deserialize(c2.getString(InProgressContentProvider.DBCOL_T_INDEX)));
			}while (c2.moveToNext());
		}
		
		if(c3.getCount() > 0)
		{
			c3.moveToFirst();
			do{
				long objId = c3.getLong(NotSyncedContentProvider.DBCOL_ID_INDEX);
				allElements.remove(objId);
				LocalTuple.Status status = LocalTuple.Status.values()[c3.getInt(NotSyncedContentProvider.DBCOL_status_INDEX)];
				if(!status.equals(LocalTuple.Status.DELETING))
					allElements.put(objId, 
						new JSONDeserializer<T>().deserialize(c3.getString(NotSyncedContentProvider.DBCOL_T_INDEX)));
			}while (c3.moveToNext());
		}
		
		}
		finally {
			c1.close();
			c2.close();
			c3.close();
		}
		return allElements.values();
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.UniversalDAO#getById(long)
	 */
	@Override
	public T getById(long id) {
		//notsynced
		
		Cursor c1 = contentResolver.query(NotSyncedContentProvider.ContentUri, new String[] { NotSyncedContentProvider.DBCOL_status, 
				NotSyncedContentProvider.DBCOL_T}, NotSyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		Cursor c2 = contentResolver.query(InProgressContentProvider.ContentUri, new String[] { InProgressContentProvider.DBCOL_status, 
				InProgressContentProvider.DBCOL_T}, InProgressContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		Cursor c3 = contentResolver.query(SyncedContentProvider.ContentUri, new String[] { 
				SyncedContentProvider.DBCOL_T}, SyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		try{
		if(c1.getCount()>0){
			c1.moveToFirst();
			LocalTuple.Status status = LocalTuple.Status.values()[c1.getInt(NotSyncedContentProvider.DBCOL_status_INDEX)];
			if(status.equals(LocalTuple.Status.DELETING))
				return null;
			return new JSONDeserializer<T>().deserialize(c1.getString(NotSyncedContentProvider.DBCOL_T_INDEX));
		}
		
		//inprogress
		if(c2.getCount()>0){
			c2.moveToFirst();
			LocalTuple.Status status = LocalTuple.Status.values()[c2.getInt(InProgressContentProvider.DBCOL_status_INDEX)];
			if(status.equals(LocalTuple.Status.DELETING))
				return null;
			return new JSONDeserializer<T>().deserialize(c2.getString(InProgressContentProvider.DBCOL_T_INDEX));
		}
			
		//synced
		if(c3.getCount()>0){
			c3.moveToFirst();
			return new JSONDeserializer<T>().deserialize(c3.getString(SyncedContentProvider.DBCOL_T_INDEX));
		}
		}
		finally{
			c1.close();
			c2.close();
			c3.close();
		}
		//contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
		return null;
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.UniversalDAO#insert(com.szas.sync.Tuple)
	 */
	@Override
	public void insert(T element) {
		long id = element.getId();
		Cursor c1 = contentResolver.query(SyncedContentProvider.ContentUri, 
				new String[] {SyncedContentProvider.DBCOL_ID}, 
				SyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		Cursor c2 = contentResolver.query(InProgressContentProvider.ContentUri, 
				new String[] {InProgressContentProvider.DBCOL_ID},
				InProgressContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		Cursor c3 = contentResolver.query(NotSyncedContentProvider.ContentUri, 
				new String[] {NotSyncedContentProvider.DBCOL_ID},
				NotSyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		if(c1.getCount()>0 || c2.getCount() > 0 || c3.getCount() > 0)
			//object already in some table
			return;
		ContentValues contentValues = new ContentValues();
		contentValues.put(NotSyncedContentProvider.DBCOL_ID, Long.toString(id));
		contentValues.put(NotSyncedContentProvider.DBCOL_status, LocalTuple.Status.INSERTING.ordinal());
		contentValues.put(NotSyncedContentProvider.DBCOL_T, new JSONSerializer().include("*").serialize(element));
		contentResolver.insert(NotSyncedContentProvider.ContentUri, contentValues);
		
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.UniversalDAO#delete(com.szas.sync.Tuple)
	 */
	@Override
	public void delete(T element) {
		long id = element.getId();
		Cursor inElementsCursor = contentResolver.query(SyncedContentProvider.ContentUri, 
				new String[] {SyncedContentProvider.DBCOL_ID}, 
				SyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		Cursor inSyncingElementsCursor = contentResolver.query(InProgressContentProvider.ContentUri, 
				new String[] {InProgressContentProvider.DBCOL_ID},
				InProgressContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		Cursor inElementsToSyncCursor = contentResolver.query(NotSyncedContentProvider.ContentUri, 
				new String[] {NotSyncedContentProvider.DBCOL_ID},
				NotSyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		if(inElementsCursor.getCount()>0 || inSyncingElementsCursor.getCount() > 0){
			ContentValues contentValues = new ContentValues();
			contentValues.put(NotSyncedContentProvider.DBCOL_ID, Long.toString(id));
			contentValues.put(NotSyncedContentProvider.DBCOL_status, LocalTuple.Status.DELETING.ordinal());
			contentValues.put(NotSyncedContentProvider.DBCOL_T, new JSONSerializer().include("*").serialize(element));
			contentResolver.insert(NotSyncedContentProvider.ContentUri, contentValues);
		}
		else if (inElementsToSyncCursor.getCount()>0){
			contentResolver.delete(NotSyncedContentProvider.ContentUri, NotSyncedContentProvider.DBCOL_ID + " =? ", new String[]{Long.toString(id)});
		}
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.UniversalDAO#update(com.szas.sync.Tuple)
	 */
	@Override
	public void update(T element) {
		long id = element.getId();
		Cursor inElementsCursor = contentResolver.query(SyncedContentProvider.ContentUri, 
				new String[] {SyncedContentProvider.DBCOL_ID}, 
				SyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		Cursor inSyncingElementsCursor = contentResolver.query(InProgressContentProvider.ContentUri, 
				new String[] {InProgressContentProvider.DBCOL_ID},
				InProgressContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		Cursor inElementsToSyncCursor = contentResolver.query(NotSyncedContentProvider.ContentUri, 
				new String[] {NotSyncedContentProvider.DBCOL_ID, NotSyncedContentProvider.DBCOL_status},
				NotSyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)}, null);
		if(!(inElementsCursor.getCount()>0)
				&& !(inSyncingElementsCursor.getCount()>0)
				&& !(inElementsToSyncCursor.getCount()>0))
			//no elements to update in database
			return;
		LocalTuple.Status status = LocalTuple.Status.UPDATING;
		if(inElementsToSyncCursor.getCount()>0 &&
				inElementsToSyncCursor.getInt(NotSyncedContentProvider.DBCOL_status_INDEX)==LocalTuple.Status.INSERTING.ordinal()){
			status = LocalTuple.Status.INSERTING;
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put(NotSyncedContentProvider.DBCOL_ID, id);
		contentValues.put(NotSyncedContentProvider.DBCOL_status, status.ordinal());
		contentValues.put(NotSyncedContentProvider.DBCOL_T, new JSONSerializer().include("*").serialize(element));
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.DAOObserverProvider#addDAOObserver(com.szas.sync.DAOObserver)
	 */
	@Override
	public void addDAOObserver(DAOObserver daoObserver) {
		daoObservers.add(daoObserver);
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.DAOObserverProvider#removeDAOObserver(com.szas.sync.DAOObserver)
	 */
	@Override
	public boolean removeDAOObserver(DAOObserver daoObserver) {
		return daoObservers.remove(daoObserver);
	}
	
	protected void notifyContentObservers(boolean whileSync) {
		for (DAOObserver daoObserver : daoObservers) {
			daoObserver.onChange(whileSync);
		}
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.local.LocalDAO#getElementsToSync()
	 */
	@Override
	public ArrayList<LocalTuple<T>> getElementsToSync() {
		ArrayList<LocalTuple<T>> ret =
			new ArrayList<LocalTuple<T>>();
		Cursor syncingElements = contentResolver.query(InProgressContentProvider.ContentUri, 
				new String[] {InProgressContentProvider.DBCOL_ID, InProgressContentProvider.DBCOL_T}, 
				null, null, null);
		if(syncingElements.getCount() <= 0){
			DBContentProvider.moveFromOneTableToAnother(InProgressContentProvider.DBTABLE2, NotSyncedContentProvider.DBTABLE3, true);
		}
		syncingElements.requery();
		if(syncingElements.getCount()>0){
			syncingElements.moveToFirst();
			do{
				ret.add(new JSONDeserializer<LocalTuple<T>>().deserialize(
						syncingElements.getString(InProgressContentProvider.DBCOL_T_INDEX)));
			}while(syncingElements.moveToNext());
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.local.LocalDAO#getUnknownElementsToSync()
	 */
	@Override
	public ArrayList<Object> getUnknownElementsToSync() {
		ArrayList<Object> objects =
			new ArrayList<Object>();
		ArrayList<LocalTuple<T>> elementsToSync =
			getElementsToSync();
		for (LocalTuple<T> elementToSync : elementsToSync) {
			objects.add(elementToSync);
		}
		return objects;
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.local.LocalDAO#getLastTimestamp()
	 */
	@Override
	public long getLastTimestamp() {
		return context.getSharedPreferences("TimeStamp", 0).getLong("timestamp", Context.MODE_PRIVATE);
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.local.LocalDAO#setLastTimestamp(long)
	 */
	@Override
	public void setLastTimestamp(long lastTimestamp) {
		SharedPreferences.Editor editor = context.getSharedPreferences("timestamp", Context.MODE_PRIVATE).edit();
		editor.putLong("timestampe", lastTimestamp);
		editor.commit();
		
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.local.LocalDAO#setSyncedElements(java.util.ArrayList)
	 */
	@Override
	public void setSyncedElements(ArrayList<RemoteTuple<T>> syncedElements) {
		DBContentProvider.cleanTable(InProgressContentProvider.DBTABLE2);
		for (RemoteTuple<T> remoteTuple : syncedElements) {
			T remoteElement = remoteTuple.getElement();
			long id = remoteElement.getId();
			contentResolver.delete(SyncedContentProvider.ContentUri, SyncedContentProvider.DBCOL_ID + " = ?", new String[]{Long.toString(id)});
			if (remoteTuple.isDeleted() == false){
				ContentValues contentValues = new ContentValues();
				contentValues.put(SyncedContentProvider.DBCOL_ID, id);
				contentValues.put(SyncedContentProvider.DBCOL_T, new JSONSerializer().include("*").serialize(remoteTuple));
				contentResolver.insert(SyncedContentProvider.ContentUri, contentValues);
			}
		}
		notifyContentObservers(true);
	}

	/* (non-Javadoc)
	 * @see com.szas.sync.local.LocalDAO#setSyncedUnknownElements(java.util.ArrayList)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setSyncedUnknownElements(ArrayList<Object> syncedElements)
			throws WrongObjectThrowable {
		ArrayList<RemoteTuple<T>> ret = 
			new ArrayList<RemoteTuple<T>>();
		for (Object element: syncedElements) {
			try {
				ret.add((RemoteTuple<T>)element);
			} catch (ClassCastException exception) {
				throw new WrongObjectThrowable();
			}
		}
		setSyncedElements(ret);
		
	}
	
	

}
