package com.szas.server.gwt.client;

public class RemoteTuple extends UniversalTuple {
	private boolean deleted;

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
	}
}
