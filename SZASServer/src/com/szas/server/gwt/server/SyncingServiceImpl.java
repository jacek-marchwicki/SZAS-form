package com.szas.server.gwt.server;

import java.util.ArrayList;

import no.eirikb.gwtchannelapi.server.ChannelServer;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.szas.server.StaticSyncer;
import com.szas.server.gwt.client.MessageEvent;
import com.szas.server.gwt.client.SerializableWhiteSpace;
import com.szas.server.gwt.client.SyncingService;
import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;
import com.szas.sync.WrongObjectThrowable;

@SuppressWarnings("serial")
public class SyncingServiceImpl extends RemoteServiceServlet implements
		SyncingService {
	private static final String MY_CHANNEL = "myChannel";

	public SyncingServiceImpl() {
		
	}

	@Override
	public ArrayList<SyncedElementsHolder> sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders) {
		
		try {
			ArrayList<SyncedElementsHolder> value = StaticSyncer.getSyncHelper().sync(toSyncElementsHolders);
			for (ToSyncElementsHolder holder : toSyncElementsHolders) {
				if (holder.elementsToSync.size() > 0) {
					ChannelServer.send(MY_CHANNEL, new MessageEvent());
					break;
				}
			}
			return value;
		} catch (WrongObjectThrowable e) {
			return null;
		}
	}
	
	@Override
	public SerializableWhiteSpace dummy(SerializableWhiteSpace d) {
		return d;
	}

	@Override
	public String getChannel() {
		return ChannelServiceFactory.getChannelService().createChannel(MY_CHANNEL);
	}

}