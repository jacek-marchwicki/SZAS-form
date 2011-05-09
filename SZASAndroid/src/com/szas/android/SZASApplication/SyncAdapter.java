/**
 * 
 */
package com.szas.android.SZASApplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.Tuple;
import com.szas.sync.remote.RemoteTuple;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;


/**
 * @author pszafer@gmail.com
 * 
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter{

	private final AccountManager accountManager;
	private final Context context;
	private Date mLastUpdated;
	
	String authToken;
	public static final String AUTH_TOKEN_TYPE = "ah";
	String gaeUrl = "https://szas-form.appspot.com/";
	String gaeSyncUrl = gaeUrl + "sync?oauth_token=";
	
	
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
		remoteTuples = fetchFromNetwork();
		mLastUpdated = new Date();
	
		try {
			
	
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		}
		
	}
	
	private ArrayList<RemoteTuple<Tuple>> fetchFromNetwork(){
		InputStream inputStream = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
				5);
		// nameValuePairs
		HttpPost httpPost = new HttpPost(
				gaeSyncUrl+authToken);
		HttpResponse response = null;

		response = httpClient.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			inputStream = response.getEntity().getContent();

		if (inputStream != null) {
			ArrayList<RemoteTuple<Tuple>> result = new JSONDeserializer<ArrayList<RemoteTuple<Tuple>>>()
					.deserialize(new Scanner(inputStream)
							.useDelimiter("\\A").next());
		return result;
	}
	
	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;
			try {
				bundle = result.getResult();
				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
				if(intent != null) {
					// User input required
		//			startActivity(intent); //XXX how to ask user for persmission to get auth token
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
	
	
	protected void onGetAuthToken(Bundle bundle) {
		authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
		new GetCookieTask().execute(auth_token);
	}
	
	private Cookie getAuthCookie(String authToken) throws ClientProtocolException, IOException {
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 Cookie retObj = null;
		 String cookieUrl = gaeAppLoginUrl + "?continue=" 
		 + URLEncoder.encode(gaeAppBaseUrl,"UTF-8") + "&auth=" + URLEncoder.encode 
		 (authToken,"UTF-8"); 
		 HttpGet httpget = new HttpGet(cookieUrl);
		 HttpResponse response = httpClient.execute(httpget);
		 if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK ||
		 response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT) {

		 if (httpClient.getCookieStore().getCookies().size() > 0) {
		 retObj= httpClient.getCookieStore().getCookies().get(0);
		 }

		 }
		 return retObj;

		 }
	
	}
	
}
