package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SyncingServiceAsync {

	void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
			AsyncCallback<Void> callback);

}
