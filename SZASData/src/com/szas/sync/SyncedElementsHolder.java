package com.szas.sync;

import java.io.Serializable;
import java.util.ArrayList;

public final class SyncedElementsHolder implements Serializable {
	public SyncedElementsHolder() {
	}
	private static final long serialVersionUID = 1L;
	public String className;
	public ArrayList<Object> syncedElements; // ArrayList<RemoteTuple<T>>
	public long syncTimestamp;
}
