package com.szas.sync;

import java.util.ArrayList;


public interface UniversalDAO<T extends Tuple> extends ContentObserverProvider {
	public ArrayList<T> getAll();
	public void insert(T element);
	public void delete(T element);
	public void update(T element);
}
