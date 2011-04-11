package com.szas.sync.local;

import java.util.ArrayList;
import java.util.Collection;

public class SyncObserverProviderImpl implements SyncObserverProvider {
	Collection<SyncObserver> syncObservers = 
		new ArrayList<SyncObserver>();
	@Override
	public void addSyncObserver(SyncObserver syncObserver) {
		syncObservers.add(syncObserver);
	}

	@Override
	public boolean removeSyncObserver(SyncObserver syncObserver) {
		return syncObservers.remove(syncObserver);
	}
	
	protected void notifySyncObserverSucses() {
		for (SyncObserver syncObserver : syncObservers) {
			syncObserver.onSucces();
		}
	}
	
	protected void notifySyncObserverFail(Throwable caught) {
		for (SyncObserver syncObserver : syncObservers) {
			syncObserver.onFail(caught);
		}
	}
	
	protected void notifySyncObserverStart() {
		for (SyncObserver syncObserver : syncObservers ) {
			syncObserver.onStart();
		}
	}

}
