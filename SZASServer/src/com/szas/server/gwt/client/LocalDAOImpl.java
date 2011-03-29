package com.szas.server.gwt.client;

import java.util.ArrayList;

public class LocalDAOImpl<T extends Tuple> implements LocalDAO<T> {
	
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

}
