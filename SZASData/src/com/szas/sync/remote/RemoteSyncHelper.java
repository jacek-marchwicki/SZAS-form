package com.szas.sync.remote;

import java.util.ArrayList;

import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;
import com.szas.sync.WrongObjectThrowable;

public interface RemoteSyncHelper {
	public void append(String className, RemoteDAO<?> localService);
	ArrayList<SyncedElementsHolder> sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders) throws WrongObjectThrowable;
}
