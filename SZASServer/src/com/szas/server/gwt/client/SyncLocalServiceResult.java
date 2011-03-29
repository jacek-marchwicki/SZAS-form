package com.szas.server.gwt.client;

import java.util.ArrayList;

public interface SyncLocalServiceResult {
	void onSuccess(ArrayList<SyncedElementsHolder> result);
	void onFailure(Throwable caught);
}
