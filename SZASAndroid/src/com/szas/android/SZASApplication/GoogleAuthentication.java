/**
 * 
 */
package com.szas.android.SZASApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author pszafer@gmail.com class to login into google account | oauth | etc
 *         based on
 *         https://code.google.com/p/nimbits/source/browse/trunk/Java/Nimbits
 *         +Data+Logging/src/com/nimbits/google/
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
// XXX FINISH ACCOUNT MANAGER LOADING
public class GoogleAuthentication {

	/**
	 * Http URL to GAE Service
	 */
	private final String gaeAppBaseUrl = "http://szas-form.appspot.com/";

	/**
	 * Https URL to GAE Service XXX commented because not used
	 */
	// private final String sGaeAppBaseUrl = "http://szas-form.appspot.com/";

	/**
	 * Login to GAE Service used with authCookie
	 */
	private final String gaeAppLoginUrl = gaeAppBaseUrl + "_ah/login";

	/**
	 * Type of token in Android AccountManager to get from android
	 * accountmanager which allows to login into GAE
	 */
	private static final String AUTH_TOKEN_TYPE = "ah";

	private static AccountManager accountManager = null;

	private static String authtoken = null;

	private Cookie authCookie = null;

	private static GoogleAuthentication googleAuthentication;

	private final Account account;

	private Context context;

	/**
	 * Get cookie which allow you to have access to GAE
	 * 
	 * @return authCookie
	 */
	public Cookie getAuthCookie() {
		Log.v("GoogleAuthentication", "Cookie value: " + authCookie.getValue());
		return authCookie;
	}

	/**
	 * Set authentication cookie
	 * 
	 * @param authCookie
	 *            cookie from google service
	 */
	public void setAuthCookie(Cookie authCookie) {
		this.authCookie = authCookie;
	}

	/**
	 * Creates new instance of class to reload authCookie
	 * @param account 
	 * 
	 * @return
	 */
	public static GoogleAuthentication getNewGoogleAuthentication(Account account) {
		googleAuthentication = new GoogleAuthentication(account);
		return googleAuthentication;
	}

	/**
	 * Gives actual state of GoogleAuthentication class
	 * @param account 
	 * 
	 * @return
	 */
	public static GoogleAuthentication getGoogleAuthentication(Account account) {
		if (googleAuthentication == null)
			getNewGoogleAuthentication(account);
		return googleAuthentication;
	}

	/**
	 * Do nothing
	 * @param account 
	 */
	private GoogleAuthentication(Account account) {
		this.account = account;
	}

	/**
	 * Don't wanna to create more cookie than one because google could ban our
	 * service not used
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * Connect to google service and authCookie
	 * 
	 * @param accountManager
	 * @param context 
	 * @return true if connected, false if error
	 */
	public boolean connect(AccountManager accountManager, Context context) {
		GoogleAuthentication.accountManager = accountManager;
		this.context = context;
		boolean retVal = true;
		if (authCookie == null) {
			String authtoken;
			try {
				authtoken = getToken();
				authCookie = getAuthCookie(authtoken);
			} catch (OperationCanceledException e) {
				retVal = false;
			} catch (AuthenticatorException e) {
				retVal = false;
			} catch (IOException e) {
				retVal = false;
			}
		}
		return retVal;
	}

	/**
	 * Get token from accountManager
	 * 
	 * @return String authentication token
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	private String getToken() throws OperationCanceledException,
			AuthenticatorException, IOException {
		accountManager.invalidateAuthToken("com.google", authtoken);
		final AccountManagerFuture<Bundle> accountManagerFuture = accountManager
				.getAuthToken(account, AUTH_TOKEN_TYPE, true, null, null);
		Bundle authTokenBundle = accountManagerFuture.getResult();
		authtoken = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN);
		 if (authtoken == null) {
             // No auth token - will need to ask permission from user.
             Intent intent = new Intent("com.google.ctp.AUTH_PERMISSION");
             intent.putExtra("AccountManagerBundle", authTokenBundle);
             context.sendBroadcast(intent);
         }
		return authtoken;

	}

	/**
	 * Download cookie from GAE, using authToken
	 * 
	 * @param authtoken
	 *            authentication token
	 * @return cookie
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private Cookie getAuthCookie(String authtoken)
			throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Cookie retObj = null;
		String cookieUrl = gaeAppLoginUrl + "?continue="
				+ URLEncoder.encode(gaeAppBaseUrl, "UTF-8") + "&auth="
				+ URLEncoder.encode(authtoken, "UTF-8");
		HttpGet httpget = new HttpGet(cookieUrl);
		HttpResponse response = httpClient.execute(httpget);
		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK
				|| response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
			if (httpClient.getCookieStore().getCookies().size() > 0) {
				//TODO CHECK IF ASCID COOKIE
				retObj = httpClient.getCookieStore().getCookies().get(0);
			}
		}
		return retObj;
	}
}
