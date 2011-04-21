package com.szas.sync.local;

public interface SyncObserverProvider {
	public void addSyncObserver(SyncObserver syncObserver);
	boolean removeSyncObserver(SyncObserver syncObserver);
}
