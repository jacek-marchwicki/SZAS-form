package com.szas.server.gwt.client;

import java.util.ArrayList;

public interface RemoteDAO<T extends Tuple> extends UniversalDAO<T> {
	public ArrayList<RemoteTuple<T>> syncElements(ArrayList<LocalTuple<T>> elements, long lastTimestamp);
	public ArrayList<Object> syncUnknownElements(ArrayList<Object> elements, long lastTimestamp) throws WrongObjectThrowable;
	long getTimestamp();
}
