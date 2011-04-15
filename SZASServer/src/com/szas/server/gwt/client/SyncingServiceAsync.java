package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;

public interface SyncingServiceAsync {

	void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
			AsyncCallback<ArrayList<SyncedElementsHolder>> callback);

	void dummy(SerializableWhiteSpace d,
			AsyncCallback<SerializableWhiteSpace> callback);

}
