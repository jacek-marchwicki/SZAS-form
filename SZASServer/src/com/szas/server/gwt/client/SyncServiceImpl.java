package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SyncServiceImpl implements SyncService {
	
	private final SyncingServiceAsync syncingServiceAsync =
		GWT.create(SyncingService.class);
	
	private class ServiceHolder {
		
		public Class<? extends LocalTuple<?>> tupleClass;
		public LocalService<?> localService;
		
		public ServiceHolder(Class<? extends LocalTuple<?>> tupleClass,
				LocalService<?> localService) {
			this.tupleClass = tupleClass;
			this.localService = localService;
		}
	}
	public class ToSyncElementsHolder {
		public Class<? extends LocalTuple<?>> tupleClass;
		public ArrayList<?> elementsToSync;
	}
	
	ArrayList<ServiceHolder> serviceHolders =
		new ArrayList<SyncServiceImpl.ServiceHolder>();

	@Override
	public void append(Class<? extends LocalTuple<?>> tupleClass,
			LocalService<?> localService) {
		ServiceHolder serviceHolder =
			new ServiceHolder(tupleClass, localService);
		serviceHolders.add(serviceHolder);
	}

	@Override
	public void sync() {
		ArrayList<ToSyncElementsHolder> toSyncElementsHolders = 
			new ArrayList<SyncServiceImpl.ToSyncElementsHolder>();
		
		for (ServiceHolder serviceHolder : serviceHolders) {
			ToSyncElementsHolder toSyncElementsHolder = 
				new ToSyncElementsHolder();
			toSyncElementsHolder.elementsToSync = 
				serviceHolder.localService.getElementsToSync();
			toSyncElementsHolder.tupleClass =
				serviceHolder.tupleClass;
		}
		syncingServiceAsync.sync(toSyncElementsHolders, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				// TODO receive data from server
				// TODO update localServices
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO do something if connection fails
			}
		});
	}

}
