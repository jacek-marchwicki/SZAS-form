package com.szas.server.gwt.client;

public interface SyncLocalServiceResult {
	void onSuccess(Void result);
	void onFailure(Throwable caught);
}
