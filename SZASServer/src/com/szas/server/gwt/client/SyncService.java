package com.szas.server.gwt.client;

public interface SyncService {
	public void append(Class<? extends LocalTuple<?>> tupleClass, LocalService<?> localService);
	public void sync();
}
