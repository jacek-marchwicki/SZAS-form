package com.szas.server.gwt.client;

import java.util.ArrayList;

public interface RemoteSyncHelper {
	public void append(Class<? extends Tuple> tupleClass, RemoteDAO<?> localService);
	Void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders);
}
