package com.szas.server.gwt.client.sync;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.szas.server.gwt.client.SerializableWhiteSpace;
import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;

public interface SyncingServiceAsync {

	void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
			AsyncCallback<ArrayList<SyncedElementsHolder>> callback);

	void dummy(SerializableWhiteSpace d,
			AsyncCallback<SerializableWhiteSpace> callback);

}
