package com.szas.sync.local;

import java.io.Serializable;

import com.szas.sync.Tuple;

/**
 * LocalTuple is class dedicated to store information about changes in LocalDAO
 * @author Jacek Marchwicki
 *
 * @param <T> Type of data stored in LocalTuple
 */
public class LocalTuple<T extends Tuple> implements Serializable {
	private static final long serialVersionUID = 1L;
	public LocalTuple() {
	}
	public enum Status {
		INSERTING,
		UPDATING,
		DELETING
	}
	
	private Status status;
	private T element;
	
	/**
	 * Set type of change in tuple
	 * @param status type of change in tuple
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * Get type of change in tuple
	 * @return type of change in tuple
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * Set element stored in localTuple
	 * @param element to be stored in localTuple
	 */
	public void setElement(T element) {
		this.element = element;
	}
	
	/**
	 * Get element stored in localTuple
	 * @return element stored in localTuple
	 */
	public T getElement() {
		return element;
	}
	
}
