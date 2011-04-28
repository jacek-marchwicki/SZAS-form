/**
 * 
 */
package com.szas.android.SZASApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author pszafer@gmail.com
 *
 */
public class SyncService extends Service{
	private static final Object syncAdapterLock = new Object();
	private static SyncAdapter syncAdapter = null;
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if(syncAdapter == null)
			syncAdapter = new SyncAdapter(getApplicationContext(), true);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
	
	
	 
	
	
}
