package com.szas.sync.local;

/**
 * LocalSyncHelper allow to package DAO's data and sync them at the same time
 * @author Jacek Marchwicki
 *
 */
public interface LocalSyncHelper extends SyncObserverProvider {
	/**
	 * Add DAO to sync
	 * @param className name of remote DAO's sync service
	 * @param localService local DAO to be synced
	 */
	public void append(String className, LocalDAO<?> localService);
	
	/**
	 * Perform sync operation on all appended DAO's
	 */
	public void sync();
}
