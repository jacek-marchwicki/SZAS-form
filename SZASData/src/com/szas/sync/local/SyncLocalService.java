package com.szas.sync.local;

import java.util.ArrayList;

import com.szas.sync.ToSyncElementsHolder;


public interface SyncLocalService {
	void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
			SyncLocalServiceResult callback);
}
