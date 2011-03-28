package com.szas.server.gwt.client;

import java.util.ArrayList;

public interface RemoteDAO<T extends Tuple> extends UniversalDAO<T> {
	public void syncElements(ArrayList<LocalTuple<T>> elements);
	public void syncUnknownElements(ArrayList<Object> elements);
}
