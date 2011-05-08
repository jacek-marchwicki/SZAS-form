package com.szas.server.gwt.client.sync;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.szas.server.gwt.client.SerializableWhiteSpace;
import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;

@RemoteServiceRelativePath("syncing")
public interface SyncingService extends RemoteService {
	ArrayList<SyncedElementsHolder> sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders);
	SerializableWhiteSpace dummy(SerializableWhiteSpace d);
}
