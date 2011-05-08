package com.szas.sync.local;

import java.util.ArrayList;

import com.szas.sync.SyncedElementsHolder;

/**
 * Returns information of data transmission
 * @author Jacek Marchwicki
 *
 */
public interface SyncLocalServiceResult {
	/**
	 * Method should be called while transfer data success
	 * @param result result of data transmission
	 */
	void onSuccess(ArrayList<SyncedElementsHolder> result);
	
	/**
	 * Method should be called while transfer data fail
	 * @param caught error which cause failure
	 */
	void onFailure(Throwable caught);
}
