package com.szas.server.gwt.client;

import java.util.ArrayList;

public class UniversalDAOImpl<T extends Tuple> implements UniversalDAO<T> {
	
	private ArrayList<T> elements = new ArrayList<T>();

	@Override
	public ArrayList<T> getAll() {
		return elements;
	}

	@Override
	public void insert(T element) {
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
