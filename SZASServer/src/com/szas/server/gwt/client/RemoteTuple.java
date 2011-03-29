package com.szas.server.gwt.client;


public class RemoteTuple<T extends Tuple> {
	private boolean deleted;
	private T element;

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
	}
	
	public void setElement(T element) {
		this.element = element;
	}
	public T getElement() {
		return element;
	}
}
