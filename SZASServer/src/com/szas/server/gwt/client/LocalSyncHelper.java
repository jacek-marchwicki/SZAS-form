package com.szas.server.gwt.client;

public interface LocalSyncHelper {
	public void append(Class<? extends Tuple> tupleClass, LocalDAO<?> localService);
	public void sync();
}
