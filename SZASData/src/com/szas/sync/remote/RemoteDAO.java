package com.szas.sync.remote;

import java.util.ArrayList;

import com.szas.sync.Tuple;
import com.szas.sync.UniversalDAO;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.local.LocalTuple;

public interface RemoteDAO<T extends Tuple> extends UniversalDAO<T> {
	public ArrayList<RemoteTuple<T>> syncElements(ArrayList<LocalTuple<T>> elements, long lastTimestamp);
	public ArrayList<Object> syncUnknownElements(ArrayList<Object> elements, long lastTimestamp) throws WrongObjectThrowable;
	long getTimestamp();
}
