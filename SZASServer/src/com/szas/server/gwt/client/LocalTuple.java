package com.szas.server.gwt.client;

public class LocalTuple<T extends Tuple> {
	public enum Status {
		INSERTING,
		UPDATING,
		DELETING,
		SYNCED
	}
	
	private Status status;
	private T element;
	
	public void setStatus(Status status) {
		this.status = status;
	}
	public Status getStatus() {
		return status;
	}
	public void setElement(T element) {
		this.element = element;
	}
	public T getElement() {
		return element;
	}
	
}
