package com.szas.sync.local;

import java.util.ArrayList;

import com.szas.sync.Tuple;
import com.szas.sync.UniversalDAO;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.remote.RemoteTuple;

public interface LocalDAO<T extends Tuple> extends UniversalDAO<T>{
	public ArrayList<LocalTuple<T>> getElementsToSync();
	public ArrayList<Object> getUnknownElementsToSync();
	public long getLastTimestamp();
	public void setLastTimestamp(long lastTimestamp);
	public void setSyncedElements(ArrayList<RemoteTuple<T>> syncedElements);
	public void setSyncedUnknownElements(ArrayList<Object> syncedElements) throws WrongObjectThrowable;
}
