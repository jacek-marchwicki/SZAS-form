package com.szas.sync;

import java.io.Serializable;
import java.util.ArrayList;

public final class ToSyncElementsHolder implements Serializable {
	private static final long serialVersionUID = 1L;
	public ToSyncElementsHolder() {
	}
	public String className;
	public ArrayList<Object> elementsToSync; // ArrayList<LocalTuple<T>>
	public long lastTimestamp;
}
