/**
 * 
 */
package com.szas.android.SZASApplication;

import com.szas.data.UserTuple;
import com.szas.sync.local.LocalDAO;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author pszafer@gmail.com
 *
 */
public class SyncService extends Service{
	private static SyncAdapter syncAdapter = null;
	 
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		usersSqlDAO = new SQLLocalDAO<UserTuple>(getApplicationContext(), getContentResolver());
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
	
	private static SQLLocalDAO<UserTuple> usersSqlDAO;
	
	public static LocalDAO<UserTuple> getUsersdao() {
		return usersSqlDAO;
	}
	
}
