package com.szas.android.SZASApplication.UI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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
import android.util.Log;
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
import com.szas.android.SZASApplication.R;
import com.szas.android.SZASApplication.SyncService;
import com.szas.data.QuestionnaireTuple;
import com.szas.export.CSVExport;
import com.szas.export.WrongCSVFile;

//"http://szas-form.appspot.com/syncnoauth
/**
 * @author pszafer@gmail.com LEGEND: XXX - adnotation FIXME - something wrong
 *         TODO - not implemented yet
 */
public class MainActivity extends ListActivity {

	private Context context;
	String[] listViewElementsArray;
	CustomDepartmentAdapter arrayAdapter;
	private IntentFilter intentFilter;
	private BroadcastReceiver refreshSyncReceiver;
	private String[] itemForList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		refreshSyncReceiver = new RefreshSyncReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(Constans.changesQuestionnaireMessage);
		itemForList = new String[0];
		arrayAdapter = new CustomDepartmentAdapter(context, R.layout.main,
				itemForList);
		context.registerReceiver(refreshSyncReceiver, intentFilter);
		startService(new Intent(context, SyncService.class));
		setListAdapter(arrayAdapter);
		csvExport = new CSVExport();
		executeTask();
	}

	/**
	 * Result of coming back to this activity from SecondActivity.java
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constans.RESULT_EXIT) {
			MainActivity.this.finish();
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		try {
			unregisterReceiver(refreshSyncReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

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

	/**
	 * Options menu
	 */
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
				AlertsDialog.DialogBuilder.createAboutWindow(this).show();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.help_item:
			try {
				AlertsDialog.DialogBuilder.createHelpWindow(this,
						getString(R.string.help1)).show();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.refresh_item:
			Constans.RefreshSyncAdapter.refreshSyncAdapter(context);
			return true;
		case R.id.preferencesmenu:
			Constans.createDirectory(context, true);
			Constans.createDirectory(context, false);
			Intent i = new Intent(MainActivity.this, Preferences.class);
			startActivity(i);
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
			i.putExtra("title", ((TextView) view
					.findViewById(R.id.list_item_firstscreen)).getText());
			i.putExtra("questionnaryName", listViewElementsArray[(int) id]);
			try {
				unregisterReceiver(refreshSyncReceiver);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			startActivityForResult(i, 0);
		}
	};

	private OnCreateContextMenuListener onCreateContextMenuListener = new OnCreateContextMenuListener() {

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.csv_main_screen_context, menu);
		}
	};
	private CSVExport csvExport;

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		long id = info.id;
		switch (item.getItemId()) {
		case R.id.csvexport:
			Constans.createDirectory(context, false);
			try {
				csvExport
						.exportCSVToFile(
								Constans.mCSVExportDirectory + "/"+listViewElementsArray[(int) id],
								LocalDAOContener
										.getFilledQuestionnaireTupleByName(listViewElementsArray[(int) id]));
				// TODO file chooser in preferences
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.csvimport:
			Constans.createDirectory(context, true);
			ArrayList<QuestionnaireTuple> questionnaireTuples = new ArrayList<QuestionnaireTuple>(
					LocalDAOContener
							.getQuestionnaireTuplesByName(listViewElementsArray[(int) id]));
			try {
				csvExport.importCSVFromFile(Constans.mCSVImportDirectory,
						questionnaireTuples.get(0).getFilled());
				// TODO file name to input and in preferences to choose
				// directory to save
				// XXX how it is about get(0). is any time
				// getQuestionnaireTuplesByName -> should be by id
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WrongCSVFile e) {
				e.printStackTrace();
			}
			return true;
		case R.id.cancelitem:
			return false;
		default:
			return super.onContextItemSelected(item);
		}
	};

	/**
	 * Execute task in AsyncTask
	 */
	private void executeTask() {
		new GetFirstTimeItemFromDatabase().execute(0);
	}

	private String[] getItemForList() {
		ArrayList<String> array = new ArrayList<String>();
		Collection<QuestionnaireTuple> qq = LocalDAOContener
				.getQuestionnaireTuples();
		for (QuestionnaireTuple q : qq) {
			array.add(q.getName());
		}
		listViewElementsArray = new String[array.size()];
		array.toArray(listViewElementsArray);
		return listViewElementsArray;
	}

	/**
	 * Class to load items from database. Set loading window with progressbar
	 * because first time this could take more time that expected
	 * 
	 * @author pszafer@gmail.com
	 * 
	 */
	private class GetFirstTimeItemFromDatabase extends
			AsyncTask<Integer, Integer, CustomDepartmentAdapter> {
		ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			progressDialog.setTitle(getString(R.string.app_name));
			progressDialog.setIcon(R.drawable.icon);
			progressDialog.setMessage(getString(R.string.loading_progressbar));
			MainActivity.this.runOnUiThread(new Runnable() {
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
		protected CustomDepartmentAdapter doInBackground(Integer... params) {
			LocalDAOContener.loadContext(context);
			itemForList = getItemForList();
			CustomDepartmentAdapter arrayAdapter = new CustomDepartmentAdapter(
					context, R.layout.main, itemForList);
			if (arrayAdapter != null && arrayAdapter.getCount() > 0) {
				return arrayAdapter;
			}
			return null;
		}

		@Override
		protected void onPostExecute(CustomDepartmentAdapter arrayAdapter) {
			progressDialog.dismiss();
			if (arrayAdapter != null) {
				MainActivity.this.arrayAdapter = arrayAdapter;
				setListAdapter(MainActivity.this.arrayAdapter);
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(onItemClickListener);
				lv.setOnCreateContextMenuListener(onCreateContextMenuListener);
				Log.v("MainActivity", "ok");
			} else {
				String temp = context.getString(R.string.problem_information);
				MainActivity.this.arrayAdapter = new CustomDepartmentAdapter(
						context, R.layout.problem_main, new String[] { temp });
				setListAdapter(MainActivity.this.arrayAdapter);
				try {
					Constans.RefreshSyncAdapter.refreshSyncAdapter(context);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(arrayAdapter);
		}

	}

	/**
	 * Refresh CustomDepartmentAdapter using notifyDataSetChanged in AsyncTask
	 * to save UI
	 * 
	 * @author pszafer@gmail.com
	 * 
	 */
	private class RefreshItemsFromDatabase extends
			AsyncTask<Integer, Integer, String[]> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String[] doInBackground(Integer... params) {
			String[] array = getItemForList();
			MainActivity.this.itemForList = array;
			return array;
		}

		@Override
		protected void onPostExecute(String[] stringAdapter) {
			if (stringAdapter != null && stringAdapter.length > 0) {
				MainActivity.this.arrayAdapter.notifyDataSetChanged();
			} else {
				String temp = context.getString(R.string.problem_information);
				MainActivity.this.arrayAdapter = new CustomDepartmentAdapter(
						context, R.layout.problem_main, new String[] { temp });
				setListAdapter(MainActivity.this.arrayAdapter);
				try {
					Constans.RefreshSyncAdapter.refreshSyncAdapter(context);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(stringAdapter);
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
			if (info != null) {
				new RefreshItemsFromDatabase().execute(0);
			}
		}

	}

	public class CustomDepartmentAdapter extends ArrayAdapter<String> {

		String[] objects;
		private int textViewResourceId;

		public CustomDepartmentAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			this.textViewResourceId = textViewResourceId;
			this.objects = new String[objects.length];
			this.objects = objects;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#notifyDataSetChanged()
		 */
		@Override
		public void notifyDataSetChanged() {
			objects = MainActivity.this.itemForList;
			if(MainActivity.this.arrayAdapter.textViewResourceId == R.layout.problem_main){
				MainActivity.this.arrayAdapter.textViewResourceId = R.layout.main;
			}
			super.notifyDataSetChanged();
		}

		public int getCount() {
			return this.objects.length;
		}

		public String getItem(int index) {
			return this.objects[index];
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(textViewResourceId, parent, false);
			}
			int textViewID, iconID;
			if (R.layout.main == textViewResourceId) {
				textViewID = R.id.list_item_firstscreen;
				iconID = R.id.department_icon;
			} else {
				textViewID = R.id.list_item_firstscreen_problem;
				iconID = R.id.department_icon_problem;
			}
			TextView label = (TextView) row.findViewById(textViewID);
			label.setText(objects[position]);
			ImageView icon = (ImageView) row.findViewById(iconID);
			String information = context
					.getString(R.string.problem_information);
			if (information != null && information.equals(objects[position]))
				icon.setImageResource(R.drawable.sad_icon);
			else {
				icon.setImageResource(R.drawable.icon2);
			}
			return row;
		}
	}

}