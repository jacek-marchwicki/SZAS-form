/**
 * 
 */
package com.szas.android.SZASApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import update.RemoteTupleManager;

import com.szas.sync.Tuple;
import com.szas.sync.remote.RemoteTuple;

import deprecated.SyncService;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;


/**
 * @author Pawel Szafer email - pszafer@gmail.com
 * 
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter{

	private final AccountManager accountManager;
	private final Context context;
	private Date mLastUpdated;
	
	/**
	 * 
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		this.context = context;
		this.accountManager = AccountManager.get(context);
	}
	
	/* (non-Javadoc)
	 * @see android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts.Account, android.os.Bundle, java.lang.String, android.content.ContentProviderClient, android.content.SyncResult)
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient contentProviderClient, SyncResult syncResult) {
		List<RemoteTuple<Tuple>> remoteTuples;
		String authtoken = null;
		//get authToken
		//get cookie for auth
		remoteTuples = new ArrayList<RemoteTuple<Tuple>>(); //XXX instead of ArrayList make get from network
		mLastUpdated = new Date();
		RemoteTupleManager.syncUsers(context, account.name, remoteTuples);
		try {
			
			SyncService.performSync(context, account, extras, contentProviderClient, syncResult);
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		}
		
	}
	
}
