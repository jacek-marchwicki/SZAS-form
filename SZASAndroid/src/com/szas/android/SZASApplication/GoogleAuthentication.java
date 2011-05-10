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
import android.os.Bundle;

/**
 * @author pszafer@gmail.com
 *	class to login into google account | oauth | etc
 */
//XXX FINISH ACCOUNT MANAGER LOADING
public class GoogleAuthentication {
	private final String gaeAppBaseUrl = "http://szas-form.appspot.com/";
//	private final String sGaeAppBaseUrl = "http://szas-form.appspot.com/";
	
	private final String gaeAppLoginUrl = gaeAppBaseUrl + "_ah/login";
	private static final String AUTH_TOKEN_TYPE = "ah";
	private static AccountManager accountManager = null;
	private static String authtoken = null;
	
	private Cookie authCookie = null;
	
	private static GoogleAuthentication googleAuthentication;
	/**
	 * Get cookie which allow you to have access to gae
	 * @return
	 */
	public Cookie getAuthCookie() {
		return authCookie;
	}
	
	public void setAuthCookie(Cookie authCookie){
		this.authCookie = authCookie;
	}
	
	/**
	 * Creates new instance of class to reload authCookie
	 * @return
	 */
	public static GoogleAuthentication getNewGoogleAuthentication(){
		googleAuthentication = new GoogleAuthentication();
		return googleAuthentication;
	}
	
	/**
	 * Gives actual state of GoogleAuthentication class
	 * @return
	 */
	public static GoogleAuthentication getGoogleAuthentication(){
		if(googleAuthentication == null)
			getNewGoogleAuthentication();
		return googleAuthentication;
	}
	
	/**
	 * Do nothing
	 */
	private GoogleAuthentication() {
	}
	
	/**
	 * Don't wanna to create more cookie than one because google could ban our service
	 */
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}
	
	public boolean Connect(AccountManager accountManager){
		GoogleAuthentication.accountManager = accountManager;
		boolean retVal = true;
		if(authCookie == null){
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
	
	private String getToken() throws OperationCanceledException, AuthenticatorException, IOException{
		Account[] accounts = accountManager.getAccounts();
		accountManager.invalidateAuthToken("com.google", authtoken);
		final AccountManagerFuture<Bundle> accountManagerFuture = accountManager.getAuthToken(accounts[0], AUTH_TOKEN_TYPE, null, null, null, null);
		Bundle authTokenBundle = accountManagerFuture.getResult();
		authtoken = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
		return authtoken;
		
	}
	
	private Cookie getAuthCookie(String authtoken) throws ClientProtocolException, IOException{
		DefaultHttpClient httpClient = new DefaultHttpClient();
        Cookie retObj = null;
        String cookieUrl = gaeAppLoginUrl + "?continue=" 
                + URLEncoder.encode(gaeAppBaseUrl,"UTF-8") + "&auth=" + URLEncoder.encode 
                (authtoken,"UTF-8"); 
        HttpGet httpget = new HttpGet(cookieUrl);
        HttpResponse response = httpClient.execute(httpget);
        if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK ||
                        response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                if (httpClient.getCookieStore().getCookies().size() > 0) {
                        retObj=   httpClient.getCookieStore().getCookies().get(0);
                }
        }
        return retObj;
	}
}
