/**
 * 
 */
package com.szas.android.SZASApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author pszafer@gmail.com
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class SyncService extends Service {
	private static SyncAdapter syncAdapter = null;

	/**
	 * 
	 */
	public SyncService() 
	{
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("SyncService", "SyncService started");
		if (syncAdapter == null)
			syncAdapter = new SyncAdapter(getApplicationContext(), true);
		Log.v("SyncService syncadapter info: ", syncAdapter.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
	
	 @Override
	    public void onDestroy() {
		 syncAdapter = null;
	    }
}
