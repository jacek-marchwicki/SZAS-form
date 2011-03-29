package com.szas.server.gwt.client;

import java.util.ArrayList;

public class RemoteSyncHelperImpl implements RemoteSyncHelper {
	
	private ArrayList<ServiceHolder> serviceHolders =
		new ArrayList<ServiceHolder>();

	private static class ServiceHolder {
		
		public Class<? extends Tuple> tupleClass;
		public RemoteDAO<?> remoteService;
		
		public ServiceHolder(Class<? extends Tuple> tupleClass,
				RemoteDAO<?> remoteService) {
			this.tupleClass = tupleClass;
			this.remoteService = remoteService;
		}
	}
	
	private RemoteDAO<?> findRemoteDAO(Class<? extends Tuple> localTupleClass) {
		for (ServiceHolder serviceHolder : serviceHolders) {
			if (!serviceHolder.tupleClass.equals(localTupleClass))
				continue;
			return serviceHolder.remoteService;
		}
		return null;
	}
	
	@Override
	public Void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders) {
		for (ToSyncElementsHolder toSyncElementsHolder : toSyncElementsHolders) {
			RemoteDAO<?> remoteDAO = findRemoteDAO(toSyncElementsHolder.tupleClass);
			remoteDAO.syncUnknownElements(toSyncElementsHolder.elementsToSync);
		}
		return null;
	}
	@Override
	public void append(Class<? extends Tuple> tupleClass,
			RemoteDAO<?> remoteService) {
		ServiceHolder serviceHolder =
			new ServiceHolder(tupleClass, remoteService);
		serviceHolders.add(serviceHolder);
		
	}

}
