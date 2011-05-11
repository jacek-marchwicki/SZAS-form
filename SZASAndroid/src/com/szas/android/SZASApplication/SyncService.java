/**
 * 
 */
package com.szas.android.SZASApplication;

import com.szas.data.UserTuple;
import com.szas.sync.local.LocalDAO;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("SyncService", "SyncService started");
		usersSqlDAO = new SQLLocalDAO<UserTuple>(getApplicationContext(),
				getContentResolver());
		if (syncAdapter == null)
			syncAdapter = new SyncAdapter(getApplicationContext(), true);
		Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
		ContentResolver.setIsSyncable(accounts[0], "com.szas.android.SZASApplication.SyncAdapter", 1);
		ContentResolver.requestSync(accounts[0], "com.szas.android.SZASApplication.SyncAdapter", new Bundle());
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

	/**
	 * SQLLocalDAO of Users
	 */
	private static SQLLocalDAO<UserTuple> usersSqlDAO;

	/**
	 * Ger users dao
	 * 
	 * @return UserTuple
	 */
	public static LocalDAO<UserTuple> getUsersdao() {
		return usersSqlDAO;
	}

}
