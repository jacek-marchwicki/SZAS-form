package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
import com.szas.android.SZASApplication.DBContentProvider;
import com.szas.android.SZASApplication.QuestionnaireTypeRow;
import com.szas.android.SZASApplication.R;
import com.szas.data.FieldTextBoxTuple;
import com.szas.data.FieldTuple;
import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;

/**
 * @author pszafer@gmail.com
 * 
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class SecondActivity extends ListActivity {
	// private Context context = null;

	private String questionnaryName;
	List<QuestionnaireTuple> questionnaireTuples;
	List<FilledQuestionnaireTuple> filledQuestionnaireTuples;
	List<QuestionnaireTypeRow> mQuestionnaireTypeRows;
	private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getApplicationContext();
		String text = getIntent().getExtras().getString("title");
		questionnaryName = getIntent().getExtras()
				.getString("questionnaryName");
		setTitle(getString(R.string.second_window_title) + " " + text);
		new GetItemFromDatabase().execute(0);
	}

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
									SecondActivity.this.finish();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			new GetItemFromDatabase().execute(1);
		}
		
	};
	
	/**
	 * Items showed in AlertDialog ListAdapter
	 */
	String[] items;

	/**
	 * List of departments item clicked
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			/*
			 * AlertDialog.Builder builder = new AlertDialog.Builder(
			 * SecondActivity.this); items = new String[] {
			 * getString(R.string.form_item1), getString(R.string.form_item2),
			 * getString(R.string.form_item3) };
			 * builder.setTitle("Pick a tile set"); builder.setItems(items, new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int item) {
			 * Toast.makeText(SecondActivity.this, "You selected: " +
			 * items[item], Toast.LENGTH_LONG) .show(); dialog.dismiss(); } });
			 * AlertDialog alert = builder.create(); alert.show();
			 */
			Intent i = new Intent(SecondActivity.this,
					QuestionnaireActivity.class);
			
			i.putExtra("title", ((TextView) view.findViewById(R.id.second_screen_textview)).getText());
			
			long _id = mQuestionnaireTypeRows.get((int) id).getId();
			i.putExtra("questionnaryID", mQuestionnaireTypeRows.get(0).getId());
			if(id >0) i.putExtra("filledQuestionnaireID", String.valueOf(_id));
			i.putExtra("questionnaryType", mQuestionnaireTypeRows.get((int) id).getType());
			startActivityForResult(i, 1);
		}
	};
	private com.szas.android.SZASApplication.UI.SecondActivity.DBContentObserver dbContentObserver;
	private Handler handler;

	private class GetItemFromDatabase extends
			AsyncTask<Integer, Integer, CustomArrayAdapter> {
		ProgressDialog progressDialog;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(SecondActivity.this);
			progressDialog.setMessage(getString(R.string.loading_progressbar));
			progressDialog.setTitle(getString(R.string.app_name));
			progressDialog.setIcon(R.drawable.icon);
			progressDialog.show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected CustomArrayAdapter doInBackground(Integer... params) {
			if(params[0] == 1){
				LocalDAOContener.refreshFilledQuestionnaireTuples();
			}
			CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(
					context, R.layout.second_screen, getItemForList());
			if (arrayAdapter != null && !arrayAdapter.isEmpty()) {
				return arrayAdapter;
			}
			return null;
		}

		/**
		 * Get departments to show in the listView
		 * 
		 * @return departments String[]
		 */
		private List<QuestionnaireTypeRow> getItemForList() {
			questionnaireTuples = new ArrayList<QuestionnaireTuple>(LocalDAOContener.getQuestionnaireTuplesByName(questionnaryName));
			filledQuestionnaireTuples = new ArrayList<FilledQuestionnaireTuple>(LocalDAOContener.getFilledQuestionnaireTupleByName(questionnaryName));
			List<QuestionnaireTypeRow> questionnaireTypeRows = new ArrayList<QuestionnaireTypeRow>();
			long id =  questionnaireTuples.get(0).getId();
			questionnaireTypeRows.add(new QuestionnaireTypeRow( questionnaireTuples.get(0).getName(), 0,id, ""));
			for(FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples){
				long id2 = filledQuestionnaireTuple.getId();
				String fullName = null;
				for(FieldTuple fieldTuple : filledQuestionnaireTuple.getFilledFields()){
					String temp = fieldTuple.getName();
					if(temp.equals("Imię") || temp.equals("Imi?"))
						fullName = ((FieldTextBoxTuple)fieldTuple).getValue(); 
					if(temp.equals("Nazwisko")){
						fullName += " " + ((FieldTextBoxTuple)fieldTuple).getValue();
						break;
					}
				}
				questionnaireTypeRows.add(new QuestionnaireTypeRow(filledQuestionnaireTuple.getName(), 1, id2, fullName));
			//	elements.put(filledQuestionnaireTuple.getId(), filledQuestionnaireTuple);
			}
			mQuestionnaireTypeRows = questionnaireTypeRows;
			return questionnaireTypeRows;
		}

		protected void onPostExecute(CustomArrayAdapter arrayAdapter) {
			progressDialog.dismiss();
			if (arrayAdapter != null) {
				setListAdapter(arrayAdapter);
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(onItemClickListener);
			}else {
				ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(
						context,
						R.layout.main,
						new String[] { getString(R.string.problem_information) });
				setListAdapter(arrayAdapter2);
				registerContentObservers();
			}
		}
	}
	
	private class CustomArrayAdapter extends ArrayAdapter<QuestionnaireTypeRow> {
		/**
		 * @param context
		 * @param textViewResourceId
		 * @param objects
		 */
		public CustomArrayAdapter(Context context, int textViewResourceId,
				List<QuestionnaireTypeRow> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}


		List<QuestionnaireTypeRow> objects;
		

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater layoutInflater = getLayoutInflater();
			View row= layoutInflater.inflate(R.layout.second_screen, parent, false);
			QuestionnaireTypeRow questionnaireTypeRow = objects.get(position);
			TextView textView = (TextView) row.findViewById(R.id.second_screen_textview);
			int type= questionnaireTypeRow.getType();
			if(type== 0)
				textView.setBackgroundColor(android.graphics.Color.BLACK);
			else if(type==1)
				textView.setBackgroundColor(android.graphics.Color.DKGRAY);
			textView.setText(questionnaireTypeRow.getFullName().equals("") ? questionnaireTypeRow.getName():questionnaireTypeRow.getFullName());
			return row;
		}
		
		
	}
	
	public class DBContentObserver extends ContentObserver {

		/**
		 * @param handler
		 */
		public DBContentObserver(Handler handler) {
			super(handler);
		}

		public void onChange(boolean selfChange) {
			handler.post(new Runnable() {
				public void run() {
					new GetItemFromDatabase().execute(1);
				}
			});
			unregisterContentObservers();
		}
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
		if (dbContentObserver != null) { // just paranoia
			cr.unregisterContentObserver(dbContentObserver);
		}
	}
}
