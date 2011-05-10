/**
 * 
 */
package com.szas.android.SZASApplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.szas.data.UserTuple;
import com.szas.sync.Tuple;
import com.szas.sync.remote.RemoteTuple;

import flexjson.JSONDeserializer;

/**
 * @author pszafer@gmail.com
 * 
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private final AccountManager accountManager;
	private final Context context;

	/**
	 * Last updated time XXX NOT USED
	 */
	private Date mLastUpdated;

	/**
	 * GAE URL
	 */
	String gaeUrl = "http://szas-form.appspot.com/";

	/**
	 * GAE sync URL
	 */
	String gaeSyncUrl = gaeUrl + "sync?oauth_token=";
	GoogleAuthentication googleAuthentication;

	/**
	 * Constructor to load needed parameters
	 * 
	 * @param context
	 *            context
	 * @param autoInitialize
	 *            needed by syncAdapter
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		this.context = context;
		this.accountManager = AccountManager.get(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts
	 * .Account, android.os.Bundle, java.lang.String,
	 * android.content.ContentProviderClient, android.content.SyncResult)
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient contentProviderClient, SyncResult syncResult) {
		List<RemoteTuple<Tuple>> remoteTuples;
		googleAuthentication = GoogleAuthentication.getGoogleAuthentication();
		try {
			remoteTuples = fetchFromNetwork(googleAuthentication);
			for (RemoteTuple<Tuple> remoteTuple : remoteTuples)
				SyncService.getUsersdao().insert(
						(UserTuple) remoteTuple.getElement());
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mLastUpdated = new Date();
	}

	/**
	 * Method to download syncing elements from network
	 * 
	 * @param googleAuthentication
	 *            - googleAuth class
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	// TODO dunno how to know if needed to do get
	private ArrayList<RemoteTuple<Tuple>> fetchFromNetwork(
			GoogleAuthentication googleAuthentication)
			throws ClientProtocolException, IOException {
		URL url = new URL(gaeUrl + "sync");
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("Cookie", googleAuthentication.getAuthCookie()
				.getName()
				+ "="
				+ googleAuthentication.getAuthCookie().getValue());
		conn.setDoOutput(true);
		// conn.setRequestMethod("POST");
		InputStream inputStream = null;
		inputStream = conn.getInputStream();
		if (inputStream != null) {
			ArrayList<RemoteTuple<Tuple>> result = new JSONDeserializer<ArrayList<RemoteTuple<Tuple>>>()
					.deserialize(new Scanner(inputStream).useDelimiter("\\A")
							.next());
			return result;
		}
		return null;
	}

	/**
	 * Method to authenticate user and connect to GAE
	 * 
	 * @return TODO don't know how use it
	 */
	private boolean isUserAuthenticated() {
		boolean retVal;
		String result = null;
		googleAuthentication = GoogleAuthentication
				.getNewGoogleAuthentication();
		googleAuthentication.Connect(accountManager);
		result = "error"; // FIXME here fetchFromNetwork or try login, something
		retVal = result.equals("true");
		return retVal;
	}
}
