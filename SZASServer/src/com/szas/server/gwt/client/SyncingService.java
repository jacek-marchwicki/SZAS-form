package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("syncing")
public interface SyncingService extends RemoteService {
	void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders);
}
