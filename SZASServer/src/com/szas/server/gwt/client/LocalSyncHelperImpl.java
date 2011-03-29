package com.szas.server.gwt.client;

import java.util.ArrayList;

public class LocalSyncHelperImpl implements LocalSyncHelper {
	
	private SyncLocalService syncLocalService;
	public LocalSyncHelperImpl(SyncLocalService syncLocalService) {
		this.syncLocalService = syncLocalService;
	}
	
	private ArrayList<ServiceHolder> serviceHolders =
		new ArrayList<ServiceHolder>();

	private static class ServiceHolder {
		
		public String className;
		public LocalDAO<? extends Tuple> localService;
		
		public ServiceHolder(String className,
				LocalDAO<?> localService) {
			this.className = className;
			this.localService = localService;
		}
	}


	@Override
	public void append(String className,
			LocalDAO<?> localService) {
		ServiceHolder serviceHolder =
			new ServiceHolder(className, localService);
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
			toSyncElementsHolder.className =
				serviceHolder.className;
		}
		getSyncLocalService().sync(toSyncElementsHolders, new SyncLocalServiceResult() {
			
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

	public void setSyncLocalService(SyncLocalService syncLocalService) {
		this.syncLocalService = syncLocalService;
	}

	public SyncLocalService getSyncLocalService() {
		return syncLocalService;
	}

}
