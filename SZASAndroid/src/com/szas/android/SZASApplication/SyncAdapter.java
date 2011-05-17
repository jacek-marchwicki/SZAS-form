/**
 * 
 */
package com.szas.android.SZASApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;
import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;
import com.szas.sync.local.LocalDAO;
import com.szas.sync.local.LocalSyncHelperImpl;
import com.szas.sync.local.SyncLocalService;
import com.szas.sync.local.SyncLocalServiceResult;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author pszafer@gmail.com
 * 
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	/**
	 * Log tag
	 */
	private static final String LOGTAG = "SZAS_SYNC_ADAPTER";

	private final AccountManager accountManager;
	
	private final static class AndroidSyncLocalService implements SyncLocalService {
		
		/**
		 * @param googleAuthentication 
		 * 
		 */
		public AndroidSyncLocalService(GoogleAuthentication googleAuthentication) {
			this.googleAuthentication = googleAuthentication;
		}
		/**
		 * GAE URL
		 */
		String gaeUrl = "http://szas-form.appspot.com/";

		/**
		 * GAE sync URL
		 */
		//String gaeSyncUrl = gaeUrl + "sync?oauth_token=";
		GoogleAuthentication googleAuthentication;

		/* (non-Javadoc)
		 * @see com.szas.sync.local.SyncLocalService#sync(java.util.ArrayList, com.szas.sync.local.SyncLocalServiceResult)
		 */
		@Override
		public void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
				SyncLocalServiceResult callback) {
			
			try {
				ArrayList<SyncedElementsHolder> elements = fetchFromNetwork(toSyncElementsHolders, googleAuthentication);
				Log.v("Sync", "Callback.onSuccess");
				callback.onSuccess(elements);
			} catch (ClientProtocolException e1) {
				callback.onFailure(e1);
				e1.printStackTrace();
			} catch (IOException e1) {
				callback.onFailure(e1);
				e1.printStackTrace();
			}

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
		private ArrayList<SyncedElementsHolder> fetchFromNetwork(
				ArrayList<ToSyncElementsHolder> elementsToSync,
				GoogleAuthentication googleAuthentication)
				throws ClientProtocolException, IOException {
			URL url = new URL(gaeUrl + "sync");
			URLConnection conn = url.openConnection();
			conn.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.addRequestProperty("Cookie", googleAuthentication.getAuthCookie()
					.getName()
					+ "="
					+ googleAuthentication.getAuthCookie().getValue());
			conn.addRequestProperty("Charset", "UTF-8");
			//this do post method
			conn.setDoOutput(true);		
			OutputStream outputStream = conn.getOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(outputStream);
			new JSONSerializer().include("*").serialize(elementsToSync, out);
			out.close();

			InputStream inputStream = null;
			inputStream = conn.getInputStream();
			String encoding = conn.getContentEncoding();
			 if (encoding == null) {
				    encoding = "UTF-8";
				  }
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
			if (inputStream == null) 
				return null;

			ArrayList<SyncedElementsHolder> result =
				new JSONDeserializer<ArrayList<SyncedElementsHolder>>().deserialize(inputStreamReader);
			return result;
		}
		
	}
	

	private LocalDAO<QuestionnaireTuple> questionnaireDAO;

	private LocalDAO<FilledQuestionnaireTuple> filledQuestionnaireDAO;

	private AndroidSyncLocalService syncLocalService;

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
		this.accountManager = AccountManager.get(context);
		setQuestionnaireDAO(new SQLLocalDAO<QuestionnaireTuple>(context, "com.szas.data.QuestionnaireTuple"));
		filledQuestionnaireDAO = new SQLLocalDAO<FilledQuestionnaireTuple>(context, "com.szas.data.FilledQuestionnaireTuple");
		
	
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
		Log.v(LOGTAG, "syncAdapter sync started");
		GoogleAuthentication googleAuthentication = GoogleAuthentication.getGoogleAuthentication(account);
		if(!googleAuthentication.connect(accountManager)){
			syncResult.stats.numAuthExceptions++;
			return;
		}
		if (googleAuthentication.getAuthCookie() == null)
		{
			syncResult.stats.numAuthExceptions++;
			return;
		}
		syncLocalService = new AndroidSyncLocalService(googleAuthentication);
		LocalSyncHelperImpl syncHelper = new LocalSyncHelperImpl(syncLocalService);
		syncHelper.append("questionnaire", getQuestionnaireDAO());
		syncHelper.append("filled", filledQuestionnaireDAO);
		syncHelper.sync();
	}

	/**
	 * @param questionnaireDAO the questionnaireDAO to set
	 */
	public void setQuestionnaireDAO(LocalDAO<QuestionnaireTuple> questionnaireDAO) {
		this.questionnaireDAO = questionnaireDAO;
	}

	/**
	 * @return the questionnaireDAO
	 */
	public LocalDAO<QuestionnaireTuple> getQuestionnaireDAO() {
		return questionnaireDAO;
	}


}
