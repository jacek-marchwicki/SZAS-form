package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.szas.android.SZASApplication.Constans;
import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
import com.szas.android.SZASApplication.QuestionnaireTypeRow;
import com.szas.android.SZASApplication.R;
import com.szas.data.FieldDataTuple;
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
	private CustomArrayAdapter itemCustomArrayAdapter;
	boolean isProblemInformationShowed = false;
	private String questionnaryName;
	List<QuestionnaireTuple> questionnaireTuples;
	List<FilledQuestionnaireTuple> filledQuestionnaireTuples;
	private static List<QuestionnaireTypeRow> mQuestionnaireTypeRows;
	private List<QuestionnaireTypeRow> itemForList;
	private Context context;
	private IntentFilter intentFilter;
	private SeparatedListAdapter arrayAdapter;
	private ArrayList<String> isOnListStrings;

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
		intentFilter.addAction(Constans.changesFilledMessage);
		registerReceiver(refreshSyncReceiver, intentFilter);
		this.context = getApplicationContext();
		String text = getIntent().getExtras().getString("title");
		questionnaryName = getIntent().getExtras()
				.getString("questionnaryName");
		setTitle(getString(R.string.second_window_title) + " " + text);
		itemForList = new ArrayList<QuestionnaireTypeRow>();
		itemCustomArrayAdapter = new CustomArrayAdapter(context,
				R.layout.second_screen, itemForList);
		arrayAdapter = new SeparatedListAdapter(context);
		executeTask();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			new GetFilledItemsFromDatabase().execute(1);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		registerReceiver(refreshSyncReceiver, intentFilter);
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
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
				AlertsDialog.DialogBuilder.createHelpWindow(this,
						getString(R.string.help2)).show();
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

	private void executeTask() {
		new GetFirstTimeItemFromDatabase().execute(0);
	}

	private OnCreateContextMenuListener onCreateContextMenuListener = new OnCreateContextMenuListener() {

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			long idd = info.id;
			if (idd > 2) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.context_menu, menu);
			}
		}
	};

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deleteitem:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			long id = info.id;
			if (id > 2) {
				id -= 2;
				deleteItem(id);
				LocalDAOContener.refreshFilledQuestionnaireTuples();
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
			if (id > 2) {
				id -= 2;
			} else if (id == 1)
				id = 0;
			i.putExtra("title", ((TextView) view
					.findViewById(R.id.list_item_title)).getText());
			long _id = mQuestionnaireTypeRows.get((int) id).getId();
			i.putExtra("questionnaryID", mQuestionnaireTypeRows.get(0).getId());
			if (id > 0)
				i.putExtra("filledQuestionnaireID", String.valueOf(_id));
			i.putExtra("questionnaryType", mQuestionnaireTypeRows.get((int) id)
					.getType());
			i.putStringArrayListExtra("isOnListStrings", isOnListStrings);
			startActivityForResult(i, 1);
		}
	};

	/**
	 * Remove selected item from list from database
	 * 
	 * @param id
	 *            id of item
	 */
	private void deleteItem(long id) {
		long _id = mQuestionnaireTypeRows.get((int) id).getId();
		LocalDAOContener.deleteFilledQuestionnaireTuple(LocalDAOContener
				.getFilledQuestionnaireTupleById(_id));
	}

	/**
	 * Get filled items by questionnaireName
	 * 
	 * @return returns List<QuestionnaireTypeRow>
	 */
	private List<QuestionnaireTypeRow> getFilledItemsForList() {
		filledQuestionnaireTuples = new ArrayList<FilledQuestionnaireTuple>(
				LocalDAOContener
						.getFilledQuestionnaireTupleByName(questionnaryName));
		List<QuestionnaireTypeRow> questionnaireTypeRows = new ArrayList<QuestionnaireTypeRow>();
		for (FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples) {
			long id2 = filledQuestionnaireTuple.getId();

			String fullName = "";
			if (isOnListStrings != null) {
				ArrayList<FieldTuple> filledFields = filledQuestionnaireTuple
						.getFilledFields();
				for (FieldTuple fieldTuple : filledFields) {
					if (isOnListStrings.contains(fieldTuple.getName())) {
						String value = ((FieldTextBoxTuple) fieldTuple).getValue();
						if(value.equals("")){
							fullName += fieldTuple.getName() + " ";
						}
						else
						{
							fullName += value
								+ " ";
						}
					}
				}
			}
			if (!fullName.equals(""))
				fullName = fullName.substring(0, fullName.lastIndexOf(" "));
			questionnaireTypeRows.add(new QuestionnaireTypeRow(
					filledQuestionnaireTuple.getName(), 1, id2, fullName));
		}
		mQuestionnaireTypeRows.addAll(1, questionnaireTypeRows);
		return questionnaireTypeRows;
	}

	private List<QuestionnaireTypeRow> getEmptyItemsForList() {
		questionnaireTuples = new ArrayList<QuestionnaireTuple>(
				LocalDAOContener.getQuestionnaireTuplesByName(questionnaryName));
		List<QuestionnaireTypeRow> questionnaireTypeRows = new ArrayList<QuestionnaireTypeRow>();
		QuestionnaireTuple questionnaireTuple = questionnaireTuples.get(0);
		long id = questionnaireTuple.getId();
		ArrayList<FieldDataTuple> fields = questionnaireTuple.getFields();
		for (FieldDataTuple field : fields) {
			if (field.isOnList()) {
				if (isOnListStrings == null)
					isOnListStrings = new ArrayList<String>();
				isOnListStrings.add(field.getName());
			}
		}
		QuestionnaireTypeRow questionnaireTypeRow = new QuestionnaireTypeRow(
				questionnaireTuple.getName(), 0, id, "");
		questionnaireTypeRows.add(questionnaireTypeRow);
		mQuestionnaireTypeRows.add(0, questionnaireTypeRow);
		return questionnaireTypeRows;
	}

	private class GetFirstTimeItemFromDatabase extends
			AsyncTask<Integer, Integer, Map<String, ?>> {
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
			SecondActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.show();
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Map<String, ?> doInBackground(Integer... params) {
			mQuestionnaireTypeRows = new ArrayList<QuestionnaireTypeRow>();
			List<QuestionnaireTypeRow> emptyItems = getEmptyItemsForList();
			List<QuestionnaireTypeRow> itemForList = getFilledItemsForList();
			Map<String, List<QuestionnaireTypeRow>> map = new HashMap<String, List<QuestionnaireTypeRow>>();
			if (emptyItems.size() > 0)
				map.put("empty", emptyItems);
			if (itemForList.size() > 0)
				map.put("filled", itemForList);
			if (map.size() > 0)
				return map;
			return null;
		}

		protected void onPostExecute(Map<String, ?> map) {
			progressDialog.dismiss();
			if (map != null) {
				@SuppressWarnings("unchecked")
				List<QuestionnaireTypeRow> emptyItem = (List<QuestionnaireTypeRow>) map
						.get("empty");
				@SuppressWarnings("unchecked")
				List<QuestionnaireTypeRow> filledItems = (List<QuestionnaireTypeRow>) map
						.get("filled");
				arrayAdapter.addSection(
						getString(R.string.empty_questionnaire),
						new CustomArrayAdapter(context, R.layout.second_screen,
								emptyItem));
				SecondActivity.this.itemForList = filledItems != null ? filledItems
						: new ArrayList<QuestionnaireTypeRow>();
				SecondActivity.this.itemCustomArrayAdapter
						.notifyDataSetChanged();
				arrayAdapter.addSection(
						getString(R.string.filled_questionnaire),
						SecondActivity.this.itemCustomArrayAdapter);
				setListAdapter(arrayAdapter);
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(onItemClickListener);
				lv.setOnCreateContextMenuListener(onCreateContextMenuListener);
				isProblemInformationShowed = false;
			} else {
				isProblemInformationShowed = true;
				ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(
						context,
						R.layout.main,
						new String[] { getString(R.string.problem_information) });
				setListAdapter(arrayAdapter2);
			}
		}

	}

	private class GetFilledItemsFromDatabase extends
			AsyncTask<Integer, Integer, List<QuestionnaireTypeRow>> {

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
			QuestionnaireTypeRow questionnaireTypeRow = mQuestionnaireTypeRows
					.get(0);
			mQuestionnaireTypeRows.clear();
			mQuestionnaireTypeRows.add(questionnaireTypeRow);
			List<QuestionnaireTypeRow> filledItems = getFilledItemsForList();
			SecondActivity.this.itemForList = filledItems;
			if (filledItems != null && filledItems.size() > 0)
				return filledItems;
			return null;
		}

		protected void onPostExecute(List<QuestionnaireTypeRow> items) {
			if (items != null) {
				SecondActivity.this.itemCustomArrayAdapter
						.notifyDataSetChanged();
				isProblemInformationShowed = false;
			} else {
				isProblemInformationShowed = true;
				ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(
						context,
						R.layout.main,
						new String[] { getString(R.string.problem_information) });
				setListAdapter(arrayAdapter2);
			}
		}

	}

	private class RefreshSyncReceiver extends BroadcastReceiver {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.BroadcastReceiver#onReceive(android.content.Context,
		 * android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			String info = intent.getStringExtra("info");
			if (info != null && info.equals("filled")) {
				if (isProblemInformationShowed) {
					new GetFirstTimeItemFromDatabase().execute(1);
				} else
					new GetFilledItemsFromDatabase().execute(0);
			}
		}

	}

	public class CustomArrayAdapter extends ArrayAdapter<QuestionnaireTypeRow> {

		List<QuestionnaireTypeRow> objects;

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

		public int getCount() {
			return this.objects.size();
		}

		public QuestionnaireTypeRow getItem(int index) {
			return this.objects.get(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#notifyDataSetChanged()
		 */
		@Override
		public void notifyDataSetChanged() {
			this.objects = SecondActivity.this.itemForList;
			super.notifyDataSetChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.second_screen, parent, false);
			}
			QuestionnaireTypeRow questionnaireTypeRow = objects.get(position);
			TextView textView = (TextView) row
					.findViewById(R.id.list_item_title);
			ImageView view = (ImageView) row.findViewById(R.id.filled_icon);
			Drawable drawable = getContext().getResources().getDrawable(
					R.drawable.icon);
			view.setImageDrawable(drawable);
			textView.setText(questionnaireTypeRow.getFullName().equals("") ? questionnaireTypeRow
					.getName() : questionnaireTypeRow.getFullName());
			return row;
		}
	}
}
