package com.szas.sync.local;

import java.util.ArrayList;

import com.szas.sync.ToSyncElementsHolder;

/**
 * Implementation of this interface schould allow to transfer data between LocalSyncHelper and RemoteSyncHelper
 * @author Jacek Marchwicki
 *
 */
public interface SyncLocalService {
	/**
	 * This transfer data between LocalSyncHelper and RemoteSyncHelper
	 * then call depending callback method while sync successful or fail
	 * @param toSyncElementsHolders
	 * @param callback
	 */
	void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
			SyncLocalServiceResult callback);
}
