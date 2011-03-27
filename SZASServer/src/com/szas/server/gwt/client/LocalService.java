package com.szas.server.gwt.client;

import java.util.ArrayList;

public interface LocalService<T extends Tuple> extends UniversalService<T> {
	public ArrayList<LocalTuple<T>> getElementsToSync();
}
