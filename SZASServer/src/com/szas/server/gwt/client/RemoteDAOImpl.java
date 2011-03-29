package com.szas.server.gwt.client;

import java.util.ArrayList;

public class RemoteDAOImpl<T extends Tuple> implements RemoteDAO<T> {

	ArrayList<RemoteTuple<T>> elements = 
		new ArrayList<RemoteTuple<T>>();
	
	@Override
	public ArrayList<T> getAll() {
		ArrayList<T> ret = new ArrayList<T>();
		for (RemoteTuple<T> localTuple : elements) {
			if (localTuple.isDeleted())
				continue;
			T element = localTuple.getElement();
			ret.add(element);
		}
		return ret;
	}

	@Override
	public void insert(T element) {
		// TODO update modiffication date
		RemoteTuple<T> localTuple = new RemoteTuple<T>();
		localTuple.setDeleted(false);
		localTuple.setElement(element);
		elements.add(localTuple);
	}

	@Override
	public void delete(T element) {
		// TODO update modiffication date
		for (RemoteTuple<T> localTuple : elements) {
			T listElement = localTuple.getElement();
			if (! listElement.equals(element)) 
				continue;
			localTuple.setDeleted(true);
			return;
		}
	}

	@Override
	public void update(T element) {
		// TODO update modiffication date
	}

	@Override
	public void syncElements(ArrayList<LocalTuple<T>> elements) {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public void syncUnknownElements(ArrayList<Object> elements) {
		ArrayList<LocalTuple<T>> knownElements = 
			new ArrayList<LocalTuple<T>>();
		for (Object element : elements) {
			// TODO fix if element not match
			knownElements.add((LocalTuple<T>) element);
		}
		
	}

}
