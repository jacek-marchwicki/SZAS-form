package com.szas.server.gwt.client;

public interface LocalSyncHelper {
	public void append(String className, LocalDAO<?> localService);
	public void sync();
}
