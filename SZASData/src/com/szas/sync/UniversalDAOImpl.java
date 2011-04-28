package com.szas.sync;

import java.util.ArrayList;


public class UniversalDAOImpl<T extends Tuple> extends DAOObserverProviderImpl
implements UniversalDAO<T> {
	private static final long serialVersionUID = 1L;
	private ArrayList<T> elements = new ArrayList<T>();

	@Override
	public ArrayList<T> getAll() {
		return elements;
	}

	@Override
	public void insert(T element) {
		elements.add(element);
		notifyContentObservers(false);
	}

	@Override
	public void delete(T element) {
		elements.remove(element);
		notifyContentObservers(false);
	}

	@Override
	public void update(T element) {
		notifyContentObservers(false);
	}

	@Override
	public T getById(long id) {
		for (T element : elements) {
			if (element.getId() == id)
				return element;
		}
		return null;
	}

}
