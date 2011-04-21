package com.szas.sync.local;

public interface LocalSyncHelper extends SyncObserverProvider {
	public void append(String className, LocalDAO<?> localService);
	public void sync();
}
