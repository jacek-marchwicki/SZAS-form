package com.szas.sync.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.szas.sync.ContentObserverProviderImpl;
import com.szas.sync.Tuple;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.remote.RemoteTuple;

public class LocalDAOImpl<T extends Tuple>
extends ContentObserverProviderImpl implements LocalDAO<T> {
	
	private static final long serialVersionUID = 1L;

	private long lastTimestamp = -1;
	
	private HashMap<Long, T> elements =
		new HashMap<Long, T>();
	
	/**
	 * if not null means - syncing
	 */
	private HashMap<Long, LocalTuple<T>> syncingElements = null;
	
	private HashMap<Long, LocalTuple<T>> elementsToSync =
		new HashMap<Long, LocalTuple<T>>(); 
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getAll() {
		HashMap<Long, T> allElements;
		allElements = (HashMap<Long, T>) elements.clone();
		
		if (syncingElements != null) {
			for (Long objId : syncingElements.keySet()) {
				LocalTuple<T> localTuple = syncingElements.get(objId);
				allElements.remove(objId);
				if (localTuple.getStatus() != LocalTuple.Status.DELETING) {
					allElements.put(objId,localTuple.getElement());
				}
			}
		}
		for (Long objId : elementsToSync.keySet()) {
			LocalTuple<T> localTuple = elementsToSync.get(objId);
			allElements.remove(objId);
			if (localTuple.getStatus() != LocalTuple.Status.DELETING) {
				allElements.put(objId,localTuple.getElement());
			}
		}
		return allElements.values();
	}

	@Override
	public void insert(T element) {
		long id = element.getId();
		Long objId = new Long(id);

		boolean inElements = elements.get(objId) != null;
		boolean inSyncingElements = syncingElements != null && syncingElements.get(objId) != null;
		boolean inElementsToSync = elementsToSync.get(objId) != null;
		
		if (inElements) {
			// item already in elements
			return;
		}
		if (inSyncingElements) {
			// item already in syncing elements
			return;
		}
		if (inElementsToSync) {
			// item already in elements to sync
			return;
		}
		LocalTuple<T> localTuple = new LocalTuple<T>();
		localTuple.setStatus(LocalTuple.Status.INSERTING);
		localTuple.setElement(element);
		elementsToSync.put(objId, localTuple);
		notifyContentObservers(false);
	}

	@Override
	public void delete(T element) {
		long id = element.getId();
		Long objId = new Long(id);
		boolean inElements = elements.get(objId) != null;
		boolean inSyncingElements = syncingElements != null && syncingElements.get(objId) != null;
		boolean inElementsToSync = elementsToSync.get(objId) != null;
		
		if (inElements || inSyncingElements) {
			LocalTuple<T> localTuple = new LocalTuple<T>();
			localTuple.setStatus(LocalTuple.Status.DELETING);
			localTuple.setElement(element);
			elementsToSync.put(objId, localTuple);
		} else if (inElementsToSync) {
			elementsToSync.remove(objId);
		} else {
			// there are no object
			return;
		}
		notifyContentObservers(false);
		return;
	}

	@Override
	public void update(T element) {
		long id = element.getId();
		Long objId = new Long(id);
		boolean inElements = elements.get(objId) != null;
		boolean inSyncingElements = syncingElements != null && syncingElements.get(objId) != null;
		boolean inElementsToSync = elementsToSync.get(objId) != null;
		
		if ((! inElements) && (! inSyncingElements) && (! inElementsToSync)) {
			// theare are no element in database
			return;
		}
		LocalTuple.Status status = LocalTuple.Status.UPDATING;
		if (inElementsToSync) {
			LocalTuple<T> before = elementsToSync.get(objId);
			if (before.getStatus() == LocalTuple.Status.INSERTING) {
				// not synced before inserting
				status = LocalTuple.Status.INSERTING;
			}
		}
		LocalTuple<T> localTuple = new LocalTuple<T>();
		localTuple.setStatus(status);
		localTuple.setElement(element);
		elementsToSync.put(objId, localTuple);
		notifyContentObservers(false);
	}

	@Override
	public ArrayList<LocalTuple<T>> getElementsToSync() {
		ArrayList<LocalTuple<T>> ret =
			new ArrayList<LocalTuple<T>>();
		
		if (syncingElements == null)
		{
			syncingElements = elementsToSync;
			elementsToSync = new HashMap<Long, LocalTuple<T>>();
		}
		for (LocalTuple<T> localTuple : syncingElements.values()) {
			ret.add(localTuple);
		}
		return ret;
	}

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

	@Override
	public long getLastTimestamp() {
		return lastTimestamp;
	}

	@Override
	public void setLastTimestamp(long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}

	@Override
	public void setSyncedElements(ArrayList<RemoteTuple<T>> syncedElements) {
		syncingElements = null;
		for (RemoteTuple<T> remoteTuple : syncedElements) {
			T remoteElement = remoteTuple.getElement();
			long id = remoteElement.getId();
			Long objId = new Long(id);
			elements.remove(objId);
			
			if (remoteTuple.isDeleted() == false)
				elements.put(objId, remoteElement);
		}
		notifyContentObservers(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSyncedUnknownElements(ArrayList<Object> syncedElements) throws WrongObjectThrowable {
		ArrayList<RemoteTuple<T>> ret = 
			new ArrayList<RemoteTuple<T>>();
		for (Object element: syncedElements) {
			// TODO - WrongObjectThrowable if casting not work
			try {
				ret.add((RemoteTuple<T>)element);
			} catch (ClassCastException exception) {
				throw new WrongObjectThrowable();
			}
		}
		setSyncedElements(ret);
	}

}
