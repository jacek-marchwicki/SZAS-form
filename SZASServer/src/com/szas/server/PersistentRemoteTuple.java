package com.szas.server;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Serialized;

public class PersistentRemoteTuple {
	@Id Long id;
	long timestamp;
	boolean deleted;
	@Serialized
	Serializable element;
	public long insertionTimestamp;
	public String className;
}
