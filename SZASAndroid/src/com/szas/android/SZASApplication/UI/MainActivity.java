package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;
import java.util.Collection;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.szas.android.SZASApplication.Constans;
import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
import com.szas.android.SZASApplication.DBContentProvider;
import com.szas.android.SZASApplication.R;
import com.szas.android.SZASApplication.SyncService;
import com.szas.data.QuestionnaireTuple;

//"http://szas-form.appspot.com/syncnoauth
/**
 * @author pszafer@gmail.com LEGEND: XXX - adnotation FIXME - something wrong
 *         TODO - not implemented yet
 */
public class MainActivity extends ListActivity {

	// private LocalDAO<QuestionnaireTuple> questionnaireDAO;
	private Context context;
	String[] listViewElementsArray;
	private Handler handler;
	private DBContentObserver dbContentObserver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		startService(new Intent(context, SyncService.class));
		SharedPreferences.Editor editor = context.getSharedPreferences(
				"timestamp", Context.MODE_PRIVATE).edit();
		editor.putLong("timestamp", -1);
		editor.commit();
		new GetItemFromDatabase().execute(0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Constans.RESULT_EXIT){
			MainActivity.this.finish();
		}
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.exit_item:
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.exit)
					.setMessage(R.string.exit_prompt)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									// Stop the activity
									MainActivity.this.finish();
								}
							}).setNegativeButton(R.string.no, null).show();
			return true;
		case R.id.about_item:
			try {
				AboutDialog.AboutDialogBuilder.createAboutWindow(this).show();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.help_item:

			return true;
		case R.id.refresh_item:
			refreshSyncAdapter();
			registerContentObservers();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	/**
	 * Click on item in the list
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(MainActivity.this, SecondActivity.class);
			i.putExtra("title", ((TextView) view).getText());
			i.putExtra("questionnaryName", listViewElementsArray[(int) id]);
			startActivityForResult(i, 0);
		}
	};

	private void refreshSyncAdapter() {
		Account[] accounts = AccountManager.get(context).getAccounts();
		ContentResolver.setIsSyncable(accounts[0],
				"com.szas.android.szasapplication.provider", 1);
		ContentResolver.requestSync(accounts[0],
				"com.szas.android.szasapplication.provider", new Bundle());
		// Account[] accounts =
		// AccountManager.get(getApplicationContext()).getAccounts();
		ContentResolver.setSyncAutomatically(accounts[0],
				"com.szas.android.szasapplication.provider", true);
	}


	private void registerContentObservers() {
		ContentResolver cr = getContentResolver();
		dbContentObserver = new DBContentObserver(handler);
		cr.registerContentObserver(
				DBContentProvider.DatabaseContentHelper.contentUriSyncedElements,
				true, dbContentObserver);
		cr.registerContentObserver(
				DBContentProvider.DatabaseContentHelper.contentUriInProgressSyncingElements,
				true, dbContentObserver);
		cr.registerContentObserver(
				DBContentProvider.DatabaseContentHelper.contentUriNotSyncedElements,
				true, dbContentObserver);
	}

	private void unregisterContentObservers() {
		ContentResolver cr = getContentResolver();
		dbContentObserver = new DBContentObserver(handler);
		if (dbContentObserver != null) {
			cr.unregisterContentObserver(dbContentObserver);
		}
	}
	

	private class GetItemFromDatabase extends
			AsyncTask<Integer, Integer, ArrayAdapter<String>> {
		ProgressDialog progressDialog;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setTitle(getString(R.string.app_name));
			progressDialog.setIcon(R.drawable.icon);
			progressDialog.setMessage(getString(R.string.loading_progressbar));
			progressDialog.show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ArrayAdapter<String> doInBackground(Integer... params) {
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					context, R.layout.main, getItemForList());
			if (arrayAdapter != null && arrayAdapter.getCount() > 0) {
				return arrayAdapter;
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayAdapter<String> arrayAdapter) {
			progressDialog.dismiss();
			if (arrayAdapter != null) {
				setListAdapter(arrayAdapter);
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(onItemClickListener);
				Log.v("MainActivity", "ok");
			} else {
				String temp = context.getString(R.string.problem_information);
				arrayAdapter = new ArrayAdapter<String>(
						context,
						R.layout.main,
						new String[] {  temp});
				setListAdapter(arrayAdapter);
				try{
				registerContentObservers();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(arrayAdapter);
		}
		
		private String[] getItemForList() {
			ArrayList<String> array = new ArrayList<String>();
			LocalDAOContener.loadContext(context);
			Collection<QuestionnaireTuple> qq = LocalDAOContener
					.getQuestionnaireTuples();
			for (QuestionnaireTuple q : qq) {
				array.add(q.getName());
			}
			listViewElementsArray = new String[array.size()];
			array.toArray(listViewElementsArray);
			return listViewElementsArray;
		}
	}

	private class DBContentObserver extends ContentObserver {

		public DBContentObserver(Handler handler) {
			super(handler);
		}

		public void onChange(boolean selfChange) {
			handler.post(new Runnable() {
				public void run() {
					new GetItemFromDatabase().execute(0);
				}
			});
			unregisterContentObservers();
		}
	}

}