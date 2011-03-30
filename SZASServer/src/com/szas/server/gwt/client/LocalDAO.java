package com.szas.server.gwt.client;

import java.util.ArrayList;

public interface LocalDAO<T extends Tuple> extends UniversalDAO<T>{
	public ArrayList<LocalTuple<T>> getElementsToSync();
	public ArrayList<Object> getUnknownElementsToSync();
	public long getLastTimestamp();
	public void setLastTimestamp(long lastTimestamp);
	public void setSyncedElements(ArrayList<RemoteTuple<T>> syncedElements);
	public void setSyncedUnknownElements(ArrayList<Object> syncedElements) throws WrongObjectThrowable;
}
