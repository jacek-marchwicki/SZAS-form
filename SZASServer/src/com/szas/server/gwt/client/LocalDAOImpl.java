package com.szas.server.gwt.client;

import java.util.ArrayList;

public class LocalDAOImpl<T extends Tuple> implements LocalDAO<T> {
	
	private long lastTimestamp = -1;
	
	public ArrayList<LocalTuple<T>> elements =
		new ArrayList<LocalTuple<T>>();
	
	@Override
	public ArrayList<T> getAll() {
		ArrayList<T> ret = new ArrayList<T>();
		for (LocalTuple<T> localTuple : elements) {
			if (localTuple.getStatus() == LocalTuple.Status.DELETING)
				continue;
			T element = localTuple.getElement();
			ret.add(element);
		}
		return ret;
	}

	@Override
	public void insert(T element) {
		LocalTuple<T> localTuple = new LocalTuple<T>();
		localTuple.setStatus(LocalTuple.Status.INSERTING);
		localTuple.setElement(element);
		elements.add(localTuple);
	}

	@Override
	public void delete(T element) {
		// TODO what if INSERTING element to DELETE
		for (LocalTuple<T> localTuple : elements) {
			T listElement = localTuple.getElement();
			if (! listElement.equals(element)) 
				continue;
			if (localTuple.getStatus() == LocalTuple.Status.INSERTING)
			{
				elements.remove(localTuple);
				return;
			}
			localTuple.setStatus(LocalTuple.Status.DELETING);
			return;
		}
	}

	@Override
	public void update(T element) {
		// TODO what if INSERTING element to UPDATE
		for (LocalTuple<T> localTuple : elements) {
			T listElement = localTuple.getElement();
			if (! listElement.equals(element)) 
				continue;
			if (localTuple.getStatus() == LocalTuple.Status.INSERTING)
			{
				return;
			}
			localTuple.setStatus(LocalTuple.Status.UPDATING);
		}
	}

	@Override
	public ArrayList<LocalTuple<T>> getElementsToSync() {
		ArrayList<LocalTuple<T>> elementsToSync =
			new ArrayList<LocalTuple<T>>();
		for (LocalTuple<T> localTuple : elements) {
			if (localTuple.getStatus() == LocalTuple.Status.SYNCED)
				continue;
			elementsToSync.add(localTuple);
		}
		return elementsToSync;
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
		// TODO Auto-generated method stub
		for (RemoteTuple<T> remoteTuple : syncedElements) {
			T remoteElement = remoteTuple.getElement();
			LocalTuple<T> found = null;
			for (LocalTuple<T> localTuple : elements) {
				T localElement = localTuple.getElement();
				if (remoteElement.getId() != localElement.getId())
					continue;
				found = localTuple;
			}
			if (remoteTuple.isDeleted()) {
				if (found != null)
					elements.remove(found);
				break;
			}
			if (found == null) {
				found = new LocalTuple<T>();
				elements.add(found);
			}
			found.setStatus(LocalTuple.Status.SYNCED);
			found.setElement(remoteElement);
		}
	}

	@Override
	public void setSyncedUnknownElements(ArrayList<Object> syncedElements) {
		ArrayList<RemoteTuple<T>> ret = 
			new ArrayList<RemoteTuple<T>>();
		for (Object element: syncedElements) {
			// TODO - fix if casting not work
			ret.add((RemoteTuple<T>)element);
		}
		setSyncedElements(ret);
	}

}
