package com.szas.sync.local;

public interface LocalSyncHelper {
	public void append(String className, LocalDAO<?> localService);
	public void sync();
}
