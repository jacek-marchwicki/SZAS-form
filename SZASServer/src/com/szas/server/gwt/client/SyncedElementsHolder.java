package com.szas.server.gwt.client;

import java.util.ArrayList;

public final class SyncedElementsHolder {
	public String className;
	public ArrayList<Object> syncedElements; // ArrayList<RemoteTuple<T>>
	public long syncTimestamp;
}
