package com.szas.sync.local;

/**
 * SyncObserverProvider allow add observer to watch sync realization status changes
 * @author Jacek Marchwicki
 *
 */
public interface SyncObserverProvider {
	
	/**
	 * Add observer to syncObserverProvider
	 * @param syncObserver implementation of SyncObserver to be added
	 */	
	public void addSyncObserver(SyncObserver syncObserver);
	
	/**
	 * Remove observer syncObserverProvider
	 * @param syncObserver implementation of SyncObserver to be removed
	 * @return true if observer was in database
	 */
	boolean removeSyncObserver(SyncObserver syncObserver);
}
