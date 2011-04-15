package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.szas.sync.ContentObserver;
import com.szas.sync.local.LocalDAO;
import com.szas.sync.local.LocalSyncHelper;
import com.szas.sync.local.SyncObserver;

public class AutoSyncer {
	private static final int TIMER_DELAY = 1000;
	private static final int START_SYNC_TIME = 10;
	private static final int MAX_SYNC_TIME = 5*60;

	private int actualSyncTime = START_SYNC_TIME;
	private int waitTime = 0;
	private boolean syncing = false;
	private boolean newDataThroughSyncing = false;
	private LocalSyncHelper syncHelper;
	private Timer timer;
	private boolean forceSync = false;
	private SyncObserver syncObserver;

	private ArrayList<AutoSyncerObserver> syncerObservers =
		new ArrayList<AutoSyncerObserver>();

	/**
	 * add observer
	 * @param autoSyncerObserver instance of AutoSyncerObserver
	 */
	public void addAutoSyncerObserver(AutoSyncerObserver autoSyncerObserver) {
		syncerObservers.add(autoSyncerObserver);
	}
	/**
	 * remove observer
	 * @param autoSyncerObserver instance of AutoSyncerObserver
	 * @return true if element was in the list
	 */
	public boolean removeAutoSyncerObserver(AutoSyncerObserver autoSyncerObserver) {
		return syncerObservers.remove(autoSyncerObserver);
	}

	private void notifyAutoSyncerObserversStarted() {
		for (AutoSyncerObserver autoSyncerObserver : syncerObservers) {
			autoSyncerObserver.onStarted();
		}
	}

	private void notifyAutoSyncerObserversSuccess() {
		for (AutoSyncerObserver autoSyncerObserver : syncerObservers) {
			autoSyncerObserver.onSuccess();
		}
	}

	private void notifyAutoSyncerObserversFail() {
		for (AutoSyncerObserver autoSyncerObserver : syncerObservers) {
			autoSyncerObserver.onFail();
		}
	}

	private void notifyAutoSyncerObserversWait(int waitTime) {
		for (AutoSyncerObserver autoSyncerObserver : syncerObservers) {
			autoSyncerObserver.onWait(waitTime);
		}
	}

	private class MyTimer extends Timer {

		@Override
		public void run() {
			waitTime--;
			notifyAutoSyncerObserversWait(waitTime);
			if (waitTime == 0) {
				timer.cancel();
				syncHelper.sync();
			}
		}
	}

	public interface AutoSyncerObserver {
		public void onStarted();
		public void onSuccess();
		public void onFail();
		public void onWait(int waitTime);
	}

	public AutoSyncer(LocalSyncHelper syncHelper) {
		this.syncHelper = syncHelper;
		this.timer = new MyTimer();
		syncObserver = new SyncObserver() {

			@Override
			public void onSucces() {
				syncSuccessed();
			}

			@Override
			public void onStart() {
				syncStarted();
			}

			@Override
			public void onFail(Throwable caught) {
				syncFail(caught);
			}
		};
		syncHelper.addSyncObserver(syncObserver);
	}

	@Override
	public void finalize() throws Throwable {
		syncHelper.addSyncObserver(syncObserver);
		super.finalize();
	}

	/**
	 * Add LocalDAO for watching changes
	 * @param dao
	 */
	public void addWatcher(LocalDAO<?> dao) {
		dao.addContentObserver(new ContentObserver() {

			@Override
			public void onChange(boolean whileSync) {
				if (whileSync)
					return;
				trySync();
			}
		});
	}

	protected void calculateIncrementedTime() {
		actualSyncTime = actualSyncTime * 2;
		if (actualSyncTime > MAX_SYNC_TIME)
			actualSyncTime = MAX_SYNC_TIME;
	}

	protected void syncFail(Throwable caught) {
		notifyAutoSyncerObserversFail();
		syncing = false;
		calculateIncrementedTime();
		newDataThroughSyncing = false;
		if (forceSync) {
			forceSync = false;
			syncHelper.sync();
		} else {
			scheduleSync();
		}
	}

	protected void syncStarted() {
		notifyAutoSyncerObserversStarted();
		syncing = true;
	}

	protected void syncSuccessed() {
		notifyAutoSyncerObserversSuccess();
		syncing = false;
		actualSyncTime = START_SYNC_TIME;
		if (forceSync) {
			forceSync = false;
			syncHelper.sync();
		} else if (newDataThroughSyncing) {
			scheduleSync();
		}
		newDataThroughSyncing = false;
	}

	private void scheduleSync() {
		if (waitTime != 0)
			return;
		waitTime = actualSyncTime;
		timer.scheduleRepeating(TIMER_DELAY);
	}

	
	/**
	 * Inform AutoSyncer that are changes pending to commit
	 */
	public void trySync() {
		if (syncing) {
			if (!forceSync)
				newDataThroughSyncing = true;
		} else {
			scheduleSync();
		}
	}

	
	/**
	 * Force to sync now
	 */
	public void syncNow() {
		if (syncing) {
			forceSync = true;
		} else {
			syncHelper.sync();
			timer.cancel();
			waitTime = 0;
		}
	}
}
