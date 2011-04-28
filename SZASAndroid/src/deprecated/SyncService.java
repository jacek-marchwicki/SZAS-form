/**
 * 
 */
package deprecated;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.szas.android.SZASApplication.AccountCredentials;
import com.szas.android.SZASApplication.DBContentProvider;
import com.szas.android.SZASApplication.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.app.Application;
import android.app.Service;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author pszafer@gmail.com
 *
 */
public class SyncService extends Service{
	private static final String ACCOUNT_TYPE = "com.google";
	private static final String KEY_TYPE = "com.szas.android.SZASApplication.SYNC_TYPE";
	private static final String KEY_ID = "om.szas.android.SZASApplication.SYNC_ID";
	
	private static SyncAdapter syncAdapter = null;
	private static ContentResolver contentResolver = null;
	private static Context context = null;
	
	
	
	private static final Object mLockSyncAdapter = new Object();
	private final HandlerThread mSyncThread = new HandlerThread("SZASSyncThread");
	
	
	
	private static final AtomicBoolean syncPending = new AtomicBoolean(false);
	
	public static void requestSync(Context context, int type, long id){
		Account[] accounts = AccountManager.get(context).getAccountsByType(ACCOUNT_TYPE);
		/**
		 * Bundle maybe
		 */
		Bundle extras = new Bundle();
		extras.putInt(KEY_TYPE, type);
		extras.putLong(KEY_ID, id);
		for(Account account : accounts){
			if(ContentResolver.getSyncAutomatically(account, DBContentProvider.AUTHORITY))
				ContentResolver.requestSync(account, DBContentProvider.AUTHORITY, null);
		}
	}
	
	public SyncService() {
		super();
	}
	
	private static DBContentProvider getContentProvider(Context context){
		ContentResolver cr = context.getContentResolver();
		ContentProviderClient client = cr.acquireContentProviderClient(DBContentProvider.AUTHORITY);
		return (DBContentProvider) client.getLocalContentProvider();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("jeb", "ane");
		int returnValue = super.onStartCommand(intent, flags, startId);
		context = getApplicationContext();
		if(syncAdapter == null) 
			syncAdapter = new SyncAdapter(context, false);
		return returnValue;
	}
	
	public static void doAccount(){
		AccountCredentials accountCredentials = new AccountCredentials(context);
		accountCredentials.getAuthToken(0);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onDestroy(){
		mSyncThread.quit();
	}

	public static boolean performSync(Context context, Account account, Bundle extras, ContentProviderClient client, SyncResult syncResult)
	throws OperationCanceledException
	{
		if(!syncPending.compareAndSet(false, true))
			return false;
		performSyncImplementation(context, account, extras, syncResult);
		syncPending.set(true);
		synchronized (syncPending) {
			syncPending.notifyAll();
		}
		return true;
	}
	
	public static void waitForSyncing(){
		synchronized (syncPending) {
			while(syncPending.get()){
				try {
					syncPending.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}
	
	private static void performSyncImplementation(Context context, Account account, Bundle extras, SyncResult syncResult)
	{
		if(extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false)){
			if(account!=null && ContentResolver.getIsSyncable(account, DBContentProvider.AUTHORITY)<0){
				try {
					ContentResolver.setIsSyncable(account, DBContentProvider.AUTHORITY, getIsSyncable(context, account)? 1 : 0);
				} catch (OperationCanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return;
		}
		if(account!=null && ContentResolver.getIsSyncable(account, DBContentProvider.AUTHORITY)<0){
			++syncResult.stats.numSkippedEntries;
			return;
		}
		int type = extras.getInt(SyncService.KEY_TYPE);
		long id = extras.getLong(SyncService.KEY_ID, -1);
		
		contentResolver = context.getContentResolver();
		boolean isFullSync = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isFullSync", false);
	}
	
	private static boolean getIsSyncable(Context context, Account account) throws IOException, OperationCanceledException{
		Account[] accounts = AccountManager.get(context).getAccountsByType(ACCOUNT_TYPE);
		for(Account account2 : accounts){
			if(account.equals(account2))
				return true;
		}
		return false;
	}
}
