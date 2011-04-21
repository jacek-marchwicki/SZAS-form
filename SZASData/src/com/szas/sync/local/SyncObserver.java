package com.szas.sync.local;

public interface SyncObserver {
	public void onSucces();
	public void onFail(Throwable caught);
	void onStart();
}
