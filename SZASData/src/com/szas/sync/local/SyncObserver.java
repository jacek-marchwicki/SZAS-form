package com.szas.sync.local;

/**
 * Interface of class which can watch for sync realization
 * @author Jacek Marchwicki
 *
 */
public interface SyncObserver {
	
	/**
	 * Executed while sync permofrm
	 */
	public void onSucces();
	
	/**
	 * Executed while sync cause failure
	 * @param caught error which cause failure
	 */
	public void onFail(Throwable caught);
	
	/**
	 * Executed while syncing started
	 */
	void onStart();
}
