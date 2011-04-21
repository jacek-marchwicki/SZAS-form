package com.szas.server.gwt.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.szas.server.StaticSyncer;
import com.szas.server.gwt.client.SerializableWhiteSpace;
import com.szas.server.gwt.client.SyncingService;
import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;
import com.szas.sync.WrongObjectThrowable;

@SuppressWarnings("serial")
public class SyncingServiceImpl extends RemoteServiceServlet implements
		SyncingService {

	public SyncingServiceImpl() {
		
	}

	@Override
	public ArrayList<SyncedElementsHolder> sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders) {
		
		try {
			ArrayList<SyncedElementsHolder> value = StaticSyncer.getSyncHelper().sync(toSyncElementsHolders);
			return value;
		} catch (WrongObjectThrowable e) {
			return null;
		}
	}
	
	@Override
	public SerializableWhiteSpace dummy(SerializableWhiteSpace d) {
		return d;
	}

}