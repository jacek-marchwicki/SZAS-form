package com.szas.android.SZASApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.szas.android.SZASApplication.DBContentProvider.DatabaseContentHelper;
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
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */

public class SQLLocalDAO<T extends Tuple> implements LocalDAO<T> {

	ContentResolver contentResolver = null;
	Context context = null;

	/**
	 * Log tag
	 */
	private static final String LOGTAG = "SZAS_SQLLOCALDAO";
	
	/**
	 * DAO Observers
	 */
	Collection<DAOObserver> daoObservers = new ArrayList<DAOObserver>();
	private String name;

	/**
	 * Constructor to load context and contentResolver
	 */
	public SQLLocalDAO(Context context, String name) {
		this.name = name;
		this.contentResolver = context.getContentResolver();
		this.context = context;
		

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.UniversalDAO#getAll()
	 */
	@Override
	public Collection<T> getAll() {
		HashMap<Long, T> allElements = new HashMap<Long, T>();
		try{
		Cursor c1 = contentResolver.query(
				DatabaseContentHelper.contentUriSyncedElements, new String[] {
						DatabaseContentHelper.DBCOL_ID,
						DatabaseContentHelper.DBCOL_T,
						DatabaseContentHelper.DBCOL_type }, DatabaseContentHelper.DBCOL_type + " = ?",
						new String[] { name }, null);
		Cursor c2 = contentResolver.query(
				DatabaseContentHelper.contentUriInProgressSyncingElements,
				new String[] { DatabaseContentHelper.DBCOL_ID,
						DatabaseContentHelper.DBCOL_T,
						DatabaseContentHelper.DBCOL_type,
						DatabaseContentHelper.DBCOL_status
						},  DatabaseContentHelper.DBCOL_type + " = ?",
						new String[] { name }, null);
		Cursor c3 = contentResolver.query(
				DatabaseContentHelper.contentUriNotSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_ID,
						DatabaseContentHelper.DBCOL_T,
						DatabaseContentHelper.DBCOL_type,
						DatabaseContentHelper.DBCOL_status
						},  DatabaseContentHelper.DBCOL_type + " = ?",
						new String[] { name }, null);
		try {
			if (c1 != null && c1.getCount() > 0) {
				c1.moveToFirst();
				do {
					Long id = c1.getLong(DatabaseContentHelper.DBCOL_ID_INDEX);
					String cc = c1
					.getString(DatabaseContentHelper.DBCOL_T_INDEX);
					RemoteTuple<T> tuple = new JSONDeserializer<RemoteTuple<T>>().deserialize(cc);
					allElements.put(id,tuple.getElement());
				} while (c1.moveToNext());
			}

			if (c2 != null && c2.getCount() > 0) {
				c2.moveToFirst();
				do {
					long objId = c2
							.getLong(DatabaseContentHelper.DBCOL_ID_INDEX);
					allElements.remove(objId);
					LocalTuple.Status status = LocalTuple.Status.values()[c2
							.getInt(DatabaseContentHelper.DBCOL_status_INDEX)];
					if (!status.equals(LocalTuple.Status.DELETING))
						allElements
								.put(objId,
										new JSONDeserializer<T>().deserialize(c2
												.getString(DatabaseContentHelper.DBCOL_T_INDEX)));
				} while (c2.moveToNext());
			}

			if (c3 != null && c3.getCount() > 0) {
				c3.moveToFirst();
				do {
					long objId = c3
							.getLong(DatabaseContentHelper.DBCOL_ID_INDEX);
					allElements.remove(objId);
					int aa = c3.getInt(DatabaseContentHelper.DBCOL_status_INDEX);
					LocalTuple.Status status = LocalTuple.Status.values()[aa];
					if (!status.equals(LocalTuple.Status.DELETING)){
						String element = c3.getString(DatabaseContentHelper.DBCOL_T_INDEX);
						LocalTuple<T> localTuple = new JSONDeserializer<LocalTuple<T>>().deserialize(element);
						allElements
								.put(objId,localTuple.getElement());
					}
				} while (c3.moveToNext());
			}

		} finally {
			c1.close();
			c2.close();
			c3.close();
		}
		}catch(Exception e){
			e.printStackTrace();
			Log.v(LOGTAG, e.toString());
		}
		return allElements.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.UniversalDAO#getById(long)
	 */
	@Override
	public T getById(long id) {
		Cursor c1 = contentResolver.query(
				DatabaseContentHelper.contentUriNotSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_status,
						DatabaseContentHelper.DBCOL_T },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		Cursor c2 = contentResolver.query(
				DatabaseContentHelper.contentUriInProgressSyncingElements,
				new String[] { DatabaseContentHelper.DBCOL_status,
						DatabaseContentHelper.DBCOL_T },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		Cursor c3 = contentResolver.query(
				DatabaseContentHelper.contentUriSyncedElements,
				new String[] { 
						DatabaseContentHelper.DBCOL_ID,
						DatabaseContentHelper.DBCOL_T },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		try {
			// not synced
			if (c1 != null && c1.getCount() > 0) {
				c1.moveToFirst();
				LocalTuple.Status status = LocalTuple.Status.values()[c1
						.getInt(DatabaseContentHelper.DBCOL_status_INDEX-2)]; //XXX index is this what we need in cursor
				if (status.equals(LocalTuple.Status.DELETING))
					return null;
				LocalTuple<T> localTuple = new JSONDeserializer<LocalTuple<T>>().deserialize(c1.getString(DatabaseContentHelper.DBCOL_T_INDEX));
				return localTuple.getElement(); 
			}

			// in progress
			if (c2 != null && c2.getCount() > 0) {
				c2.moveToFirst();
				LocalTuple.Status status = LocalTuple.Status.values()[c2
						.getInt(DatabaseContentHelper.DBCOL_status_INDEX-2)];
				if (status.equals(LocalTuple.Status.DELETING))
					return null;
				LocalTuple<T> localTuple = new JSONDeserializer<LocalTuple<T>>().deserialize(c2.getString(DatabaseContentHelper.DBCOL_T_INDEX));
				return localTuple.getElement(); 
			}

			// synced
			if (c3 != null && c3.getCount() > 0) {
				c3.moveToFirst();
				String a = c3.getString(DatabaseContentHelper.DBCOL_T_INDEX);
				RemoteTuple<T> tuple = new JSONDeserializer<RemoteTuple<T>>().deserialize(a);//FIXME czy tabela synced zawsze ma remoteTuple?
				return tuple.getElement();
			}
		}catch(Exception e){
			Log.v(LOGTAG, e.toString());
		}
		finally {
			c1.close();
			c2.close();
			c3.close();
		}
		return null;
	}

	public Collection<T> getByType(String typeName) {
		HashMap<Long, T> allElements = new HashMap<Long, T>();
		Cursor c1 = contentResolver.query(
				DatabaseContentHelper.contentUriNotSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_status,
						DatabaseContentHelper.DBCOL_T },
				DatabaseContentHelper.DBCOL_type + " = ?",
				new String[] { typeName }, null);
		Cursor c2 = contentResolver.query(
				DatabaseContentHelper.contentUriInProgressSyncingElements,
				new String[] { DatabaseContentHelper.DBCOL_status,
						DatabaseContentHelper.DBCOL_T },
				DatabaseContentHelper.DBCOL_type + " = ?",
				new String[] { typeName }, null);
		Cursor c3 = contentResolver.query(
				DatabaseContentHelper.contentUriSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_T },
				DatabaseContentHelper.DBCOL_type + " = ?",
				new String[] { typeName }, null);
		try {
			if (c1 != null && c1.getCount() > 0) {
				c1.moveToFirst();
				do {
					Long id = c1.getLong(DatabaseContentHelper.DBCOL_ID_INDEX);
					String cc = c1
					.getString(DatabaseContentHelper.DBCOL_T_INDEX);
					RemoteTuple<T> tuple = new JSONDeserializer<RemoteTuple<T>>().deserialize(cc);
					allElements.put(id,tuple.getElement());
				} while (c1.moveToNext());
			}

			if (c2 != null && c2.getCount() > 0) {
				c2.moveToFirst();
				do {
					long objId = c2
							.getLong(DatabaseContentHelper.DBCOL_ID_INDEX);
					allElements.remove(objId);
					LocalTuple.Status status = LocalTuple.Status.values()[c2
							.getInt(DatabaseContentHelper.DBCOL_status_INDEX)];
					if (!status.equals(LocalTuple.Status.DELETING))
						allElements
								.put(objId,
										new JSONDeserializer<T>().deserialize(c2
												.getString(DatabaseContentHelper.DBCOL_T_INDEX)));
				} while (c2.moveToNext());
			}

			if (c3 != null && c3.getCount() > 0) {
				c3.moveToFirst();
				do {
					long objId = c3
							.getLong(DatabaseContentHelper.DBCOL_ID_INDEX);
					allElements.remove(objId);
					LocalTuple.Status status = LocalTuple.Status.values()[c3
							.getInt(DatabaseContentHelper.DBCOL_status_INDEX)];
					if (!status.equals(LocalTuple.Status.DELETING))
						allElements
								.put(objId,
										new JSONDeserializer<T>().deserialize(c3
												.getString(DatabaseContentHelper.DBCOL_T_INDEX)));
				} while (c3.moveToNext());
			}
		} finally {
			c1.close();
			c2.close();
			c3.close();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.UniversalDAO#insert(com.szas.sync.Tuple)
	 */
	@Override
	public void insert(T element) {
		long id = element.getId();
		Cursor c1 = contentResolver.query(
				DatabaseContentHelper.contentUriSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_ID },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		Cursor c2 = contentResolver.query(
				DatabaseContentHelper.contentUriInProgressSyncingElements,
				new String[] { DatabaseContentHelper.DBCOL_ID },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		Cursor c3 = contentResolver.query(
				DatabaseContentHelper.contentUriNotSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_ID },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		if ((c1 != null && c1.getCount() > 0)
				|| (c2 != null && c2.getCount() > 0)
				|| (c3 != null && c3.getCount() > 0))
			// objects already in some table
			return;
		ContentValues contentValues = new ContentValues();
		contentValues.put(DatabaseContentHelper.DBCOL_ID, Long.toString(id));
		contentValues.put(DatabaseContentHelper.DBCOL_status,
				LocalTuple.Status.INSERTING.ordinal());
		String type = null;
		String serialized = new JSONSerializer().include("*")
				.serialize(element);
		try {
			type = new JSONObject(serialized).getJSONObject("element")
					.getString("class");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		contentValues.put(DatabaseContentHelper.DBCOL_type, type != null ? type
				: "");
		contentValues.put(DatabaseContentHelper.DBCOL_T, serialized);
		contentResolver.insert(DatabaseContentHelper.contentUriSyncedElements,
				contentValues);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.UniversalDAO#delete(com.szas.sync.Tuple)
	 */
	@Override
	public void delete(T element) {
		long id = element.getId();
		Cursor inElementsCursor = contentResolver.query(
				DatabaseContentHelper.contentUriSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_ID },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		Cursor inSyncingElementsCursor = contentResolver.query(
				DatabaseContentHelper.contentUriInProgressSyncingElements,
				new String[] { DatabaseContentHelper.DBCOL_ID },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		Cursor inElementsToSyncCursor = contentResolver.query(
				DatabaseContentHelper.contentUriNotSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_ID },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		if (inElementsCursor != null && inElementsCursor.getCount() > 0
				|| inSyncingElementsCursor.getCount() > 0) {
			ContentValues contentValues = new ContentValues();
			contentValues
					.put(DatabaseContentHelper.DBCOL_ID, Long.toString(id));
			contentValues.put(DatabaseContentHelper.DBCOL_status,
					LocalTuple.Status.DELETING.ordinal());
			contentValues.put(DatabaseContentHelper.DBCOL_T,
					new JSONSerializer().include("*").serialize(element));
			contentResolver.insert(
					DatabaseContentHelper.contentUriSyncedElements,
					contentValues);
		} else if (inElementsToSyncCursor != null
				&& inElementsToSyncCursor.getCount() > 0) {
			contentResolver.delete(
					DatabaseContentHelper.contentUriSyncedElements,
					DatabaseContentHelper.DBCOL_ID + " =? ",
					new String[] { Long.toString(id) });
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.UniversalDAO#update(com.szas.sync.Tuple)
	 */
	@Override
	public void update(T element) {
		long id = element.getId();
		Cursor inElementsCursor = contentResolver.query(
				DatabaseContentHelper.contentUriSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_ID },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		Cursor inSyncingElementsCursor = contentResolver.query(
				DatabaseContentHelper.contentUriInProgressSyncingElements,
				new String[] { DatabaseContentHelper.DBCOL_ID },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		Cursor inElementsToSyncCursor = contentResolver.query(
				DatabaseContentHelper.contentUriNotSyncedElements,
				new String[] { DatabaseContentHelper.DBCOL_ID,
						DatabaseContentHelper.DBCOL_status },
				DatabaseContentHelper.DBCOL_ID + " = ?",
				new String[] { Long.toString(id) }, null);
		if (inElementsCursor != null && inSyncingElementsCursor != null
				&& inElementsToSyncCursor != null
				&& !(inElementsCursor.getCount() > 0)
				&& !(inSyncingElementsCursor.getCount() > 0)
				&& !(inElementsToSyncCursor.getCount() > 0))
			// no elements to update in database
			return;
		LocalTuple.Status status = LocalTuple.Status.UPDATING;
		if (inElementsToSyncCursor != null
				&& inElementsToSyncCursor.getCount() > 0
				&& inElementsToSyncCursor
						.getInt(DatabaseContentHelper.DBCOL_status_INDEX) == LocalTuple.Status.INSERTING
						.ordinal()) {
			status = LocalTuple.Status.INSERTING;
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put(DatabaseContentHelper.DBCOL_ID, id);
		contentValues.put(DatabaseContentHelper.DBCOL_status, status.ordinal());
		LocalTuple<T> localTuple = new LocalTuple<T>();
		localTuple.setStatus(status);
		localTuple.setElement(element);
		String type = null;
		String serialized = new JSONSerializer().include("*")
				.serialize(localTuple);
		try {
			type = new JSONObject(serialized).getJSONObject("element").getString("class");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		contentValues.put(DatabaseContentHelper.DBCOL_type, type != null ? type
				: "");
		contentValues.put(DatabaseContentHelper.DBCOL_T, serialized);
		contentResolver.insert(DatabaseContentHelper.contentUriNotSyncedElements, contentValues);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.szas.sync.DAOObserverProvider#addDAOObserver(com.szas.sync.DAOObserver
	 * )
	 */
	@Override
	public void addDAOObserver(DAOObserver daoObserver) {
		daoObservers.add(daoObserver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.szas.sync.DAOObserverProvider#removeDAOObserver(com.szas.sync.DAOObserver
	 * )
	 */
	@Override
	public boolean removeDAOObserver(DAOObserver daoObserver) {
		return daoObservers.remove(daoObserver);
	}

	/**
	 * notify observers onChange
	 * 
	 * @param whileSync
	 */
	protected void notifyContentObservers(boolean whileSync) {
		for (DAOObserver daoObserver : daoObservers) {
			daoObserver.onChange(whileSync);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.local.LocalDAO#getElementsToSync()
	 */
	@Override
	public ArrayList<LocalTuple<T>> getElementsToSync() {
		ArrayList<LocalTuple<T>> ret = new ArrayList<LocalTuple<T>>();
		Cursor syncingElements = contentResolver.query(
				DatabaseContentHelper.contentUriInProgressSyncingElements,
				new String[] { DatabaseContentHelper.DBCOL_ID,
						DatabaseContentHelper.DBCOL_T }, null, null, null);
		if (syncingElements == null || syncingElements.getCount() <= 0) {
			DBContentProvider.moveFromOneTableToAnother(
					DatabaseContentHelper.tableNameInProgressSyncingElements,
					DatabaseContentHelper.tableNameNotSyncedElements, true);
		}
		syncingElements = contentResolver.query(
				DatabaseContentHelper.contentUriInProgressSyncingElements,
				new String[] { DatabaseContentHelper.DBCOL_ID,
						DatabaseContentHelper.DBCOL_T }, null, null, null);
		if (syncingElements != null && syncingElements.getCount() > 0) {
			syncingElements.moveToFirst();
			do {
				String a = syncingElements.getString(DatabaseContentHelper.DBCOL_T_INDEX);
				LocalTuple<T> localTuple = new JSONDeserializer<LocalTuple<T>>().deserialize(a);
				ret.add(localTuple);
			} while (syncingElements.moveToNext());
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.local.LocalDAO#getUnknownElementsToSync()
	 */
	@Override
	public ArrayList<Object> getUnknownElementsToSync() {
		ArrayList<Object> objects = new ArrayList<Object>();
		ArrayList<LocalTuple<T>> elementsToSync = getElementsToSync();
		for (LocalTuple<T> elementToSync : elementsToSync) {
			objects.add(elementToSync);
		}
		return objects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.local.LocalDAO#getLastTimestamp()
	 */
	@Override
	public long getLastTimestamp() {
		return context.getSharedPreferences("TimeStamp", 0).getLong(
				"timestamp", Context.MODE_PRIVATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.local.LocalDAO#setLastTimestamp(long)
	 */
	@Override
	public void setLastTimestamp(long lastTimestamp) {
		SharedPreferences.Editor editor = context.getSharedPreferences(
				"timestamp", Context.MODE_PRIVATE).edit();
		editor.putLong("timestampe", lastTimestamp);
		editor.commit();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.szas.sync.local.LocalDAO#setSyncedElements(java.util.ArrayList)
	 */
	@Override
	public void setSyncedElements(ArrayList<RemoteTuple<T>> syncedElements) {
		DBContentProvider
				.cleanTable(DatabaseContentHelper.tableNameInProgressSyncingElements);
		for (RemoteTuple<T> remoteTuple : syncedElements) {
			T remoteElement = remoteTuple.getElement();
			long id = remoteElement.getId();
			contentResolver.delete(
					DatabaseContentHelper.contentUriSyncedElements,
					DatabaseContentHelper.DBCOL_ID + " = ?",
					new String[] { Long.toString(id) });
			if (remoteTuple.isDeleted() == false) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(DatabaseContentHelper.DBCOL_ID, id);
				String type = null;
				String serialized = new JSONSerializer().include("*")
						.serialize(remoteTuple);
				try {
					type = new JSONObject(serialized).getJSONObject("element")
							.getString("class");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				contentValues.put(DatabaseContentHelper.DBCOL_type,
						type != null ? type : "");
				contentValues.put(DatabaseContentHelper.DBCOL_T, serialized);
				contentResolver.insert(
						DatabaseContentHelper.contentUriSyncedElements,
						contentValues);
			}
		}
		notifyContentObservers(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.szas.sync.local.LocalDAO#setSyncedUnknownElements(java.util.ArrayList
	 * )
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setSyncedUnknownElements(ArrayList<Object> syncedElements)
			throws WrongObjectThrowable {
		ArrayList<RemoteTuple<T>> ret = new ArrayList<RemoteTuple<T>>();
		for (Object element : syncedElements) {
			try {
				ret.add((RemoteTuple<T>) element);
			} catch (ClassCastException exception) {
				throw new WrongObjectThrowable();
			}
		}
		setSyncedElements(ret);
	}
}
