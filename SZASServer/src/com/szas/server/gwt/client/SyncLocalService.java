package com.szas.server.gwt.client;

import java.util.ArrayList;


public interface SyncLocalService {
	void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
			SyncLocalServiceResult callback);
}
