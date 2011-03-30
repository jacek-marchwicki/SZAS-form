package com.szas.sync.local;

import java.util.ArrayList;

import com.szas.sync.SyncedElementsHolder;

public interface SyncLocalServiceResult {
	void onSuccess(ArrayList<SyncedElementsHolder> result);
	void onFailure(Throwable caught);
}
