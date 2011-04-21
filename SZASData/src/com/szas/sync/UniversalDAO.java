package com.szas.sync;

import java.util.Collection;


public interface UniversalDAO<T extends Tuple> extends ContentObserverProvider {
	public Collection<T> getAll();
	public T getById(long id);
	public void insert(T element);
	public void delete(T element);
	public void update(T element);
}
