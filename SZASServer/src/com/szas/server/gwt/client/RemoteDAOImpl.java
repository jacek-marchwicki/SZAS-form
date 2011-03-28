package com.szas.server.gwt.client;

import java.util.ArrayList;

public class RemoteDAOImpl<T extends Tuple> implements RemoteDAO<T>, UniversalDAO<T> {

	@Override
	public ArrayList<T> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(T element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(T element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(T element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void syncElements(ArrayList<LocalTuple<T>> elements) {
		// TODO Auto-generated method stub
	}

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
