package com.szas.sync.local;

import java.util.ArrayList;

import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;
import com.szas.sync.Tuple;
import com.szas.sync.WrongObjectThrowable;

public class LocalSyncHelperImpl extends SyncObserverProviderImpl implements LocalSyncHelper {
	
	private SyncLocalService syncLocalService;
	public LocalSyncHelperImpl(SyncLocalService syncLocalService) {
		this.syncLocalService = syncLocalService;
	}
	
	private ArrayList<ServiceHolder> serviceHolders =
		new ArrayList<ServiceHolder>();

	private static class ServiceHolder {
		
		public String className;
		public LocalDAO<? extends Tuple> localDAO;
		
		public ServiceHolder(String className,
				LocalDAO<?> localService) {
			this.className = className;
			this.localDAO = localService;
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
		notifySyncObserverStart();
		ArrayList<ToSyncElementsHolder> toSyncElementsHolders = 
			new ArrayList<ToSyncElementsHolder>();
		
		for (ServiceHolder serviceHolder : serviceHolders) {
			LocalDAO<?> localDAO = serviceHolder.localDAO;
			ToSyncElementsHolder toSyncElementsHolder = 
				new ToSyncElementsHolder();
			toSyncElementsHolder.elementsToSync = 
				localDAO.getUnknownElementsToSync();
			toSyncElementsHolder.className =
				serviceHolder.className;
			toSyncElementsHolder.lastTimestamp = 
				localDAO.getLastTimestamp();
			toSyncElementsHolders.add(toSyncElementsHolder);
		}
		syncLocalService.sync(toSyncElementsHolders, new SyncLocalServiceResult() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught);
			}

			@Override
			public void onSuccess(ArrayList<SyncedElementsHolder> result) {
				success(result);
			}
		});
	}

	protected void fail(Throwable caught) {
		notifySyncObserverFail(caught);
	}

	protected void success(ArrayList<SyncedElementsHolder> result) {
		// TODO receive data from server
		// TODO update localServices
		for (SyncedElementsHolder syncedElementsHolder : result) {
			LocalDAO<?> localDAO = findLocalDAO(syncedElementsHolder.className);
			try {
				localDAO.setSyncedUnknownElements(syncedElementsHolder.syncedElements);
				localDAO.setLastTimestamp(syncedElementsHolder.syncTimestamp);
			} catch (WrongObjectThrowable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		notifySyncObserverSucses();
	}

	private LocalDAO<?> findLocalDAO(String className) {
		for (ServiceHolder serviceHolder : serviceHolders) {
			if (!serviceHolder.className.equals(className))
				continue;
			return serviceHolder.localDAO;
		}
		// TODO Throw error
		return null;
	}

	public void setSyncLocalService(SyncLocalService syncLocalService) {
		this.syncLocalService = syncLocalService;
	}

	public SyncLocalService getSyncLocalService() {
		return syncLocalService;
	}

}
