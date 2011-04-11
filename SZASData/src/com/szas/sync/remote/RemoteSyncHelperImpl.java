package com.szas.sync.remote;

import java.util.ArrayList;

import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;
import com.szas.sync.WrongObjectThrowable;

public class RemoteSyncHelperImpl implements RemoteSyncHelper {
	
	private ArrayList<ServiceHolder> serviceHolders =
		new ArrayList<ServiceHolder>();

	private static class ServiceHolder {
		
		public String className;
		public RemoteDAO<?> remoteDAO;
		
		public ServiceHolder(String className,
				RemoteDAO<?> remoteService) {
			this.className = className;
			this.remoteDAO = remoteService;
		}
	}
	
	private RemoteDAO<?> findRemoteDAO(String localClassName) {
		for (ServiceHolder serviceHolder : serviceHolders) {
			if (!serviceHolder.className.equals(localClassName))
				continue;
			return serviceHolder.remoteDAO;
		}
		// TODO throw error
		return null;
	}
	
	@Override
	public ArrayList<SyncedElementsHolder> sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders) throws WrongObjectThrowable {
		ArrayList<SyncedElementsHolder> syncedElementsHolders = 
			new ArrayList<SyncedElementsHolder>();
		for (ToSyncElementsHolder toSyncElementsHolder : toSyncElementsHolders) {
			RemoteDAO<?> remoteDAO = findRemoteDAO(toSyncElementsHolder.className);
			
			SyncedElementsHolder syncedElementsHolder = 
				new SyncedElementsHolder();
			syncedElementsHolders.add(syncedElementsHolder);
			syncedElementsHolder.className = toSyncElementsHolder.className;
			syncedElementsHolder.syncedElements = 
				remoteDAO.syncUnknownElements(toSyncElementsHolder.elementsToSync,
						toSyncElementsHolder.lastTimestamp);
			syncedElementsHolder.syncTimestamp =
				remoteDAO.getTimestamp();
		}
		return syncedElementsHolders;
	}
	@Override
	public void append(String className,
			RemoteDAO<?> remoteService) {
		ServiceHolder serviceHolder =
			new ServiceHolder(className, remoteService);
		serviceHolders.add(serviceHolder);
		
	}

}
