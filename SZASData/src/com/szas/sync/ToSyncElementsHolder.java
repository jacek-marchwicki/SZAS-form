package com.szas.sync;

import java.util.ArrayList;

public final class ToSyncElementsHolder {
	public String className;
	public ArrayList<Object> elementsToSync; // ArrayList<LocalTuple<T>>
	public long lastTimestamp;
}
