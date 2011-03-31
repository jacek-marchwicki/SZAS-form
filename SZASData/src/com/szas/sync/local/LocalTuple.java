package com.szas.sync.local;

import java.io.Serializable;

import com.szas.sync.Tuple;

public class LocalTuple<T extends Tuple> implements Serializable {
	private static final long serialVersionUID = 1L;
	public LocalTuple() {
	}
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
