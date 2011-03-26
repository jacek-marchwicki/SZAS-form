package com.szas.server.gwt.client;

import java.util.ArrayList;

public interface UniversalService<T extends UniversalTuple> {
	public ArrayList<T> getAll();
	public void insert(T element);
	public void delete(T element);
	public void update(T element);
}
