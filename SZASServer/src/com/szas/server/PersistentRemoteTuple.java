package com.szas.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.szas.sync.Tuple;
import com.szas.sync.remote.RemoteTuple;

@PersistenceCapable
public class PersistentRemoteTuple<T extends Tuple> extends RemoteTuple<T> {
	private static final long serialVersionUID = 1L;
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private boolean deleted;
	
	@Persistent
	private T element;
	
	@Persistent
	private long timestamp;
	
	public PersistentRemoteTuple() {
	}
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

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public Key getKey() {
		return key;
	}
}
