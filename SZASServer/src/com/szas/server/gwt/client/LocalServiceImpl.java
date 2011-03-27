package com.szas.server.gwt.client;

import java.util.ArrayList;
import java.util.Random;

public class LocalServiceImpl<T extends Tuple> implements LocalService<T> {
	
	public ArrayList<LocalTuple<T>> elements =
		new ArrayList<LocalTuple<T>>();
	private Random random = new Random();

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
		element.setRandomId(random);
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
			if (localTuple.getStatus() != LocalTuple.Status.SYNCED)
				continue;
			elementsToSync.add(localTuple);
		}
		return elementsToSync;
	}

}
