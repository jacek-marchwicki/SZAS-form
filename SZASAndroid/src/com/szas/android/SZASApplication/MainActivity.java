package com.szas.android.SZASApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.Tuple;
import com.szas.sync.remote.RemoteTuple;

import deprecated.SyncService;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

//"http://szas-form.appspot.com/syncnoauth"
public class MainActivity extends Activity {
	// private ExampleData exampleData;

	public static final String AUTH_TOKEN_TYPE = "ah";
	String authToken;
	
	
	


	private class threader implements Runnable {

		private Object object;

		/**
		 * 
		 */
		public threader(Object object) {
			this.object = object;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Debug.waitForDebugger();
			// setContentView(R.layout.main);
			// TextView textView = (TextView) findViewById(R.id.textView);
			// textView.setText(exampleData.read());
			AccountManager accountManager = AccountManager
					.get(getApplicationContext());
			Account[] accounts = accountManager.getAccounts();
			accountManager.invalidateAuthToken("com.google", authToken);
			final AccountManagerFuture<Bundle> accountManagerFuture = accountManager
					.getAuthToken(accounts[1], AUTH_TOKEN_TYPE, false, new GetAuthTokenCallback(), null);
				/*runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Debug.waitForDebugger();
							final Bundle authTokenBundle = accountManagerFuture.getResult();
				/*			if(authTokenBundle.containsKey(AccountManager.KEY_INTENT)){
								Intent intent = authTokenBundle.getParcelable(AccountManager.KEY_INTENT); 
								int flags = intent.getFlags();
								flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
				                  intent.setFlags(flags);
				                  startActivityForResult(intent, 0);
							authToken = authTokenBundle.getString(
									AccountManager.KEY_AUTHTOKEN);
							if(!authTokenBundle.containsKey(AccountManager.KEY_AUTHTOKEN)){
								if (authToken == null) {
					                // No auth token - will need to ask permission from user.
					                Intent intent = new Intent("com.google.ctp.AUTH_PERMISSION");
					                intent.putExtra("AccountManagerBundle", authTokenBundle);
					                getApplicationContext().sendBroadcast(intent);
					            }
							}
							else{
							InputStream inputStream = null;
							DefaultHttpClient httpClient = new DefaultHttpClient();
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
									5);
							// nameValuePairs
							HttpPost httpPost = new HttpPost(
									"https://szas-form.appspot.com/sync?oauth_token="
											+ authToken);
							HttpResponse response = null;

							response = httpClient.execute(httpPost);
							if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
								inputStream = response.getEntity().getContent();

							if (inputStream != null) {
								ArrayList<SyncedElementsHolder> result = new JSONDeserializer<ArrayList<SyncedElementsHolder>>()
										.deserialize(new Scanner(inputStream)
												.useDelimiter("\\A").next());
								// JsonNode rootNode =
								// objectMapper.readValue(new
								// URL("http://szas-form.appspot.com/syncnoauth"),
								// JsonNode.class);
								ContentValues initialValues = new ContentValues();
								initialValues
										.put(DBContentProvider.DBCOL_ID,
												((Tuple) ((RemoteTuple) result
														.get(0).syncedElements
														.get(0)).getElement())
														.getId());
								initialValues.put(
										DBContentProvider.DBCOL_syncTimestamp,
										result.get(0).syncTimestamp);
								initialValues
										.put(DBContentProvider.DBCOL_status,
												((RemoteTuple) result.get(0).syncedElements
														.get(0)).isDeleted() ? 1
														: 0);
								initialValues
										.put(DBContentProvider.DBCOL_form,
												new JSONSerializer()
														.include("*")
														.serialize(
																(Object) (((RemoteTuple) result
																		.get(0).syncedElements
																		.get(0))
																		.getElement())));
								getContentResolver().insert(
										DBContentProvider.CONTENT_URI,
										initialValues);
								Log.v("test", "test");
							}
							}
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (OperationCanceledException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (AuthenticatorException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});*/

		}

	}

	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;
			try {
				bundle = result.getResult();
				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
				if(intent != null) {
					// User input required
					startActivity(intent);
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
	
	protected void onGetAuthToken(Bundle bundle) {
		authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
	//	new GetCookieTask().execute(auth_token);
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getApplicationContext().startService(new Intent(MainActivity.this, SyncService.class));
		SyncService.doAccount();
	}
}