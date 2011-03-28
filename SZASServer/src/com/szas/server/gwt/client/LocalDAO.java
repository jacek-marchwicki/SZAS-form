package com.szas.server.gwt.client;

import java.util.ArrayList;

public interface LocalDAO<T extends Tuple>{
	public ArrayList<LocalTuple<T>> getElementsToSync();
	public ArrayList<Object> getUnknownElementsToSync();
}
