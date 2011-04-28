package com.szas.sync;

import java.io.Serializable;
import java.util.ArrayList;

public final class ToSyncElementsHolder implements Serializable {
	public ToSyncElementsHolder() {
	}
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * className name of remote DAO's sync service
	 */
	public String className;
	
	/**
	 * elements to by stored on remote DAO
	 * casted from ArrayList<LocalTuple<T>> to ArrayList<Object>
	 */
	public ArrayList<Object> elementsToSync;
	
	/**
	 * Last sync timestamp, -1 if no sync was performed
	 */
	public long lastTimestamp;
}
