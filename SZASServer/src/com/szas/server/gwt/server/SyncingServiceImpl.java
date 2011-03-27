package com.szas.server.gwt.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.szas.server.gwt.client.SyncingService;
import com.szas.server.gwt.client.SyncServiceImpl.ToSyncElementsHolder;

@SuppressWarnings("serial")
public class SyncingServiceImpl extends RemoteServiceServlet implements
		SyncingService {

	@Override
	public void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders) {
		// TODO Auto-generated method stub

	}

}
