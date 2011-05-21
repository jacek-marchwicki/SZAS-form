package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.szas.android.SZASApplication.Constans;
import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
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
	private static List<QuestionnaireTypeRow> mQuestionnaireTypeRows;
	private Context context;
	private IntentFilter intentFilter;
	
	/**
	 * Items showed in AlertDialog ListAdapter
	 */
	String[] items;
	private BroadcastReceiver refreshSyncReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		refreshSyncReceiver = new RefreshSyncReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(Constans.broadcastMessage);
		registerReceiver(refreshSyncReceiver, intentFilter);
		this.context = getApplicationContext();
		String text = getIntent().getExtras().getString("title");
		questionnaryName = getIntent().getExtras()
				.getString("questionnaryName");
		setTitle(getString(R.string.second_window_title) + " " + text);
		new GetItemFromDatabase().execute(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			new GetItemFromDatabase().execute(1);
		}
	};
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		registerReceiver(refreshSyncReceiver, intentFilter);
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see android.app.ListActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		unregisterReceiver(refreshSyncReceiver);
		super.onDestroy();
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
									setResult(Constans.RESULT_EXIT);
									SecondActivity.this.finish();
								}
							}).setNegativeButton(R.string.no, null).show();
			return true;
		case R.id.about_item:
			try {
				AlertsDialog.DialogBuilder.createAboutWindow(this).show();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.help_item:
			try {
				AlertsDialog.DialogBuilder.createHelpWindow(this, getString(R.string.help2)).show();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.refresh_item:
			Constans.RefreshSyncAdapter.refreshSyncAdapter(context);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void executeTask(){
		new GetItemFromDatabase().execute(0);
	}

	private OnCreateContextMenuListener onCreateContextMenuListener = new OnCreateContextMenuListener() {
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			AdapterContextMenuInfo info =
	            (AdapterContextMenuInfo) menuInfo;
			long idd = info.id;
			if(idd > 2){
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.context_menu, menu);
			}
		}
	};
	
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deleteitem:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			long id = info.id;
			if(id>2){
				id -= 2;
				deleteItem(id);
				new GetItemFromDatabase().execute(1);
			}
			return true;
		case R.id.cancelitem:
			return false;
		default:
			return super.onContextItemSelected(item);
		}
	};
	
	/**
	 * List of departments item clicked
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(SecondActivity.this,
					QuestionnaireActivity.class);
			if(id>2){
				id -= 2;
			}
			else if(id == 1) 
				id = 0;
			i.putExtra("title", ((TextView) view
					.findViewById(R.id.list_item_title)).getText());
			long _id = mQuestionnaireTypeRows.get((int) id).getId();
			i.putExtra("questionnaryID", mQuestionnaireTypeRows.get(0).getId());
			if (id > 0)
				i.putExtra("filledQuestionnaireID", String.valueOf(_id));
			i.putExtra("questionnaryType", mQuestionnaireTypeRows.get((int) id)
					.getType());
			startActivityForResult(i, 1);
		}
	};
	
	private void deleteItem(long id){
		long _id = mQuestionnaireTypeRows.get((int) id).getId();
		LocalDAOContener.deleteFilledQuestionnaireTuple(LocalDAOContener.getFilledQuestionnaireTupleById(_id));
	}

	private class GetItemFromDatabase extends
			AsyncTask<Integer, Integer, List<QuestionnaireTypeRow>> {
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
		protected List<QuestionnaireTypeRow> doInBackground(Integer... params) {
			if (params[0] == 1) {
				LocalDAOContener.refreshFilledQuestionnaireTuples();
			}
			List<QuestionnaireTypeRow> itemForList = getItemForList();
			if(itemForList.size()>0)
				return itemForList;
			return null;
		}

		protected void onPostExecute(List<QuestionnaireTypeRow> itemForList) {
			progressDialog.dismiss();
			if (itemForList != null) {

				SeparatedListAdapter arrayAdapter = new SeparatedListAdapter(context);
				ArrayList<QuestionnaireTypeRow> questionnaireTypeRows = new ArrayList<QuestionnaireTypeRow>();
				questionnaireTypeRows.add(itemForList.get(0));
				itemForList.remove(0);
				arrayAdapter.addSection(getString(R.string.empty_questionnaire), new CustomArrayAdapter(context, R.layout.second_screen,questionnaireTypeRows));
				arrayAdapter.addSection(getString(R.string.filled_questionnaire), new CustomArrayAdapter(context, R.layout.second_screen, itemForList));
				setListAdapter(arrayAdapter);
				ListView lv = getListView();
				
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(onItemClickListener);
				lv.setOnCreateContextMenuListener(onCreateContextMenuListener);
			} else {
				ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(
						context,
						R.layout.main,
						new String[] { getString(R.string.problem_information) });
				setListAdapter(arrayAdapter2);
			}
		}

		/**
		 * Get departments to show in the listView
		 * 
		 * @return departments String[]
		 */
		private List<QuestionnaireTypeRow> getItemForList() {
			questionnaireTuples = new ArrayList<QuestionnaireTuple>(
					LocalDAOContener
							.getQuestionnaireTuplesByName(questionnaryName));
			filledQuestionnaireTuples = new ArrayList<FilledQuestionnaireTuple>(
					LocalDAOContener
							.getFilledQuestionnaireTupleByName(questionnaryName));
			List<QuestionnaireTypeRow> questionnaireTypeRows = new ArrayList<QuestionnaireTypeRow>();
			long id = questionnaireTuples.get(0).getId();
			questionnaireTypeRows.add(new QuestionnaireTypeRow(
					questionnaireTuples.get(0).getName(), 0, id, ""));
			for (FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples) {
				long id2 = filledQuestionnaireTuple.getId();
				String fullName = "";
				for (FieldTuple fieldTuple : filledQuestionnaireTuple
						.getFilledFields()) {
					if (fieldTuple.isOnList())
						fullName += ((FieldTextBoxTuple) fieldTuple).getValue()
								+ " ";
				}
				if (!fullName.equals(""))
					fullName = fullName.substring(0,
							fullName.lastIndexOf(" "));
				questionnaireTypeRows.add(new QuestionnaireTypeRow(
						filledQuestionnaireTuple.getName(), 1, id2, fullName));
			}
			mQuestionnaireTypeRows = new ArrayList<QuestionnaireTypeRow>(questionnaireTypeRows);
			Collections.copy(mQuestionnaireTypeRows, questionnaireTypeRows);
			return questionnaireTypeRows;
		}
	}
	
	private class RefreshSyncReceiver extends BroadcastReceiver{

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			String info = intent.getStringExtra("info");
			if(info!= null){
				executeTask(); 
			}
		}
		
	}
}
