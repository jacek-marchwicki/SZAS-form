package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LocalSyncHelperImpl implements LocalSyncHelper {
	
	private final SyncingServiceAsync syncingServiceAsync =
		GWT.create(SyncingService.class);
	
	private ArrayList<ServiceHolder> serviceHolders =
		new ArrayList<ServiceHolder>();

	private static class ServiceHolder {
		
		public Class<? extends Tuple> tupleClass;
		public LocalDAO<? extends Tuple> localService;
		
		public ServiceHolder(Class<? extends Tuple> tupleClass,
				LocalDAO<?> localService) {
			this.tupleClass = tupleClass;
			this.localService = localService;
		}
	}


	@Override
	public void append(Class<? extends Tuple> tupleClass,
			LocalDAO<?> localService) {
		ServiceHolder serviceHolder =
			new ServiceHolder(tupleClass, localService);
		serviceHolders.add(serviceHolder);
	}

	@Override
	public void sync() {
		ArrayList<ToSyncElementsHolder> toSyncElementsHolders = 
			new ArrayList<ToSyncElementsHolder>();
		
		for (ServiceHolder serviceHolder : serviceHolders) {
			ToSyncElementsHolder toSyncElementsHolder = 
				new ToSyncElementsHolder();
			toSyncElementsHolder.elementsToSync = 
				serviceHolder.localService.getUnknownElementsToSync();
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
