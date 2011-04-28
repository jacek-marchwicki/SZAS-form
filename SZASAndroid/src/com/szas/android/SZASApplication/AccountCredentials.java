/**
 * 
 */
package com.szas.android.SZASApplication;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

/**
 * @author xps
 *
 */
public class AccountCredentials {
	public static final String AUTH_TOKEN_TYPE = "ah";
	private String authToken = null;
	private Context context;
	
	public AccountCredentials(Context context) {
		this.context = context;
	}
	
	public void getAuthToken(int accountId){
		accountId = 0;						//we get only google account's, so get first in the list
		AccountManager accountManager = AccountManager.get(context);
		Account[] accounts = accountManager.getAccounts();
		accountManager.invalidateAuthToken("com.google", authToken);
		final AccountManagerFuture<Bundle> accountManagerFuture = accountManager
		.getAuthToken(accounts[0], AUTH_TOKEN_TYPE, false, new GetAuthTokenCallback(), null);
		try {
			final Bundle authTokenBundle = accountManagerFuture.getResult();
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void onGetAuthToken(Bundle bundle) {
		authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
	}
	
	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
		public void run(final AccountManagerFuture<Bundle> result) {
			new Runnable() {
				@Override
				public void run() {
					Bundle bundle;
					try {
						bundle = result.getResult();
						Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
						if(intent != null) {
							// User input required
							context.startActivity(intent); //FIXME should start activity here, but it is impossible from service
						} else {
							onGetAuthToken(bundle);
						}
					} catch (OperationCanceledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (AuthenticatorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
					
				}
			};
		}
	};
}
