package com.szas.server.gwt.client;

import java.util.ArrayList;

public class UniversalServiceImpl<T extends UniversalTuple> implements UniversalService<T> {
	
	private ArrayList<T> elements = new ArrayList<T>();
	private int lastId = 0;

	@Override
	public ArrayList<T> getAll() {
		return elements;
	}

	@Override
	public void insert(T element) {
		element.setId(lastId);
		lastId+=1;
		elements.add(element);
	}

	@Override
	public void delete(T element) {
		elements.remove(element);
	}

	@Override
	public void update(T element) {
		
	}

}
