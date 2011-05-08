package com.szas.sync;

import java.io.Serializable;
import java.util.ArrayList;

public final class SyncedElementsHolder implements Serializable {
	public SyncedElementsHolder() {
	}
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * className name of remote DAO's sync service
	 */
	public String className;
	
	/**
	 * elements to be persistent in local DAO from remote server
	 * casted from ArrayList<RemoteTuple<T>> to ArrayList<Object>
	 */
	public ArrayList<Object> syncedElements;
	
	/**
	 * Timestamp of current remote DAO
	 */
	public long syncTimestamp;
}
