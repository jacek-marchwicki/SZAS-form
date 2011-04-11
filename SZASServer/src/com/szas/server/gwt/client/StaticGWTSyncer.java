package com.szas.server.gwt.client;

import java.util.ArrayList;

import no.eirikb.gwtchannelapi.client.Channel;
import no.eirikb.gwtchannelapi.client.ChannelListener;
import no.eirikb.gwtchannelapi.client.Message;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.szas.data.UserTuple;
import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.local.LocalDAO;
import com.szas.sync.local.LocalDAOImpl;
import com.szas.sync.local.LocalSyncHelper;
import com.szas.sync.local.LocalSyncHelperImpl;
import com.szas.sync.local.SyncLocalService;
import com.szas.sync.local.SyncLocalServiceResult;

public final class StaticGWTSyncer {
	private final static class GWTSyncLocalService implements SyncLocalService {

		@Override
		public void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
				final SyncLocalServiceResult callback) {
			syncingService.sync(toSyncElementsHolders, new AsyncCallback<ArrayList<SyncedElementsHolder>>() {
				
				@Override
				public void onSuccess(ArrayList<SyncedElementsHolder> result) {
					if (result == null)
					{
						callback.onFailure(new WrongObjectThrowable());
						return;
					}
					callback.onSuccess(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
			});
		}
		
	}
	private final static GWTSyncLocalService syncLocalService;
	private final static LocalSyncHelper syncHelper;
	private final static LocalDAO<UserTuple> usersDAO;
	private final static SyncingServiceAsync syncingService = GWT
	.create(SyncingService.class);
	static {
		syncLocalService = new GWTSyncLocalService();
		syncHelper = new LocalSyncHelperImpl(syncLocalService);
		usersDAO = new LocalDAOImpl<UserTuple>();
		getSynchelper().append("users", getUsersdao());
		syncingService.getChannel(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Channel channel = new Channel(result);
				channel.addChannelListener(new ChannelListener() {
					
					@Override
					public void onReceive(Message message) {
						StaticGWTSyncer.getSynchelper().sync();
					}
				});
				channel.join();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public static LocalDAO<UserTuple> getUsersdao() {
		return usersDAO;
	}
	public static LocalSyncHelper getSynchelper() {
		return syncHelper;
	}
}
