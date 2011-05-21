/**
 * 
 */
package com.szas.android.SZASApplication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;

/**
 * @author pszafer@gmail.com XXX not used
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class Constans {

	public static final String broadcastMessage = "com.szas.android.szasapplication.broadcast";
	
	/**
	 * Account type to get credentials
	 */
	public static final String ACCOUNT_TYPE = "com.google";

	public static final String authority = "com.szas.android.szasapplication.provider";
	
	/**
	 * Authtoken type
	 */
	public static final String AUTHTOKEN_TYPE = "com.google";
	
	/**
	 * Result for Activity exit
	 */
	public static final int RESULT_EXIT = 2;
	
	public static class RefreshSyncAdapter{
		public static void refreshSyncAdapter(Context context) {
			Account[] accounts = AccountManager.get(context).getAccounts();
			//List<PeriodicSync> periodicSyncs = ContentResolver.getPeriodicSyncs(accounts[0],authority);
		//	long period = 0;
	//		for(PeriodicSync periodicSync: periodicSyncs){
//				period = periodicSync.period;
			//}
			ContentResolver.setSyncAutomatically(accounts[0], authority, false);
			ContentResolver.setSyncAutomatically(accounts[0], authority, true);
		//	ContentResolver.removePeriodicSync(accounts[0], authority, new Bundle());
//			ContentResolver.addPeriodicSync(accounts[0], authority, new Bundle(), 0);
			//ContentResolver.addPeriodicSync(accounts[0], authority, new Bundle(), period);
		}
	}

}
