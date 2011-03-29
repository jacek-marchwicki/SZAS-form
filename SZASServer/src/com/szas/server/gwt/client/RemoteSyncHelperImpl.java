package com.szas.server.gwt.client;

import java.util.ArrayList;

public class RemoteSyncHelperImpl implements RemoteSyncHelper {
	
	private ArrayList<ServiceHolder> serviceHolders =
		new ArrayList<ServiceHolder>();

	private static class ServiceHolder {
		
		public String className;
		public RemoteDAO<?> remoteService;
		
		public ServiceHolder(String className,
				RemoteDAO<?> remoteService) {
			this.className = className;
			this.remoteService = remoteService;
		}
	}
	
	private RemoteDAO<?> findRemoteDAO(String localClassName) {
		for (ServiceHolder serviceHolder : serviceHolders) {
			if (!serviceHolder.className.equals(localClassName))
				continue;
			return serviceHolder.remoteService;
		}
		return null;
	}
	
	@Override
	public Void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders) {
		for (ToSyncElementsHolder toSyncElementsHolder : toSyncElementsHolders) {
			RemoteDAO<?> remoteDAO = findRemoteDAO(toSyncElementsHolder.className);
			remoteDAO.syncUnknownElements(toSyncElementsHolder.elementsToSync);
		}
		return null;
	}
	@Override
	public void append(String className,
			RemoteDAO<?> remoteService) {
		ServiceHolder serviceHolder =
			new ServiceHolder(className, remoteService);
		serviceHolders.add(serviceHolder);
		
	}

}
