package com.szas.server.gwt.client;

public class LocalTuple extends UniversalTuple {
	public enum Status {
		INSERTING,
		UPDATING,
		DELETING
	}
	
	private Status status;
	
	public void setStatus(Status status) {
		this.status = status;
	}
	public Status getStatus() {
		return status;
	}
	
}
