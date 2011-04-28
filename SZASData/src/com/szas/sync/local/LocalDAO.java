package com.szas.sync.local;

import java.util.ArrayList;

import com.szas.sync.Tuple;
import com.szas.sync.UniversalDAO;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.remote.RemoteTuple;

/**
 * Provides access to some type of data stored in tuple type database
 * it allow to sync to remote server via RemoteDAO
 * @author Jacek Marchwicki
 *
 * @param <T> type of stored tuple
 */
public interface LocalDAO<T extends Tuple> extends UniversalDAO<T>{
	
	/**
	 * Return elements which should be synced - changes from last sync
	 * @return elements which should be synced
	 */
	public ArrayList<LocalTuple<T>> getElementsToSync();
	
	/**
	 * Return elements which should be synced, casted to an ArrayList of Object's
	 * @return elements which should be synced
	 */
	public ArrayList<Object> getUnknownElementsToSync();
	
	/**
	 * Return timestamp of last sync
	 * @return timestamp of last sync, or -1 while first sync
	 */
	public long getLastTimestamp();
	
	/**
	 * Set performed sync timestamp
	 * @param lastTimestamp
	 */
	public void setLastTimestamp(long lastTimestamp);
	
	/**
	 * Persist elements from remote server in local database
	 * @param syncedElements elements from remote server
	 */
	public void setSyncedElements(ArrayList<RemoteTuple<T>> syncedElements);
	
	/**
	 * Persist elements from remote server in local database - casted to object's list
	 * @param syncedElements elements from remote server
	 * @throws WrongObjectThrowable while cast to RemoteTuple<T> fail
	 */
	public void setSyncedUnknownElements(ArrayList<Object> syncedElements) throws WrongObjectThrowable;
}
