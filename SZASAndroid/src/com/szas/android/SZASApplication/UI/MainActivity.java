package com.szas.android.SZASApplication.UI;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

//"http://szas-form.appspot.com/syncnoauth
/**
 * @author pszafer@gmail.com LEGEND: XXX - adnotation FIXME - something wrong
 *         TODO - not implemented yet
 */
public class MainActivity extends ListActivity {

	// private LocalDAO<QuestionnaireTuple> questionnaireDAO;
	private Context context;
	String[] listViewElementsArray;
	RefreshSyncReceiver refreshSyncReceiver;
	private IntentFilter intentFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		refreshSyncReceiver = new RefreshSyncReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(Constans.broadcastMessage);
		registerReceiver(refreshSyncReceiver, intentFilter);
		startService(new Intent(context, SyncService.class));
		executeTask();
	}

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
		unregisterReceiver(refreshSyncReceiver);
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
				// TODO Auto-generated catch block
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

	/**
	 * Click on item in the list
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(MainActivity.this, SecondActivity.class);
			i.putExtra("title", ((TextView) view.findViewById(R.id.list_item_firstscreen)).getText());
			i.putExtra("questionnaryName", listViewElementsArray[(int) id]);
			startActivityForResult(i, 0);
		}
	};

	private void executeTask() {
		new GetItemFromDatabase().execute(0);
	}

	private class GetItemFromDatabase extends
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
			progressDialog.show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected CustomDepartmentAdapter doInBackground(Integer... params) {
			CustomDepartmentAdapter arrayAdapter = new CustomDepartmentAdapter(
					context, R.layout.main, getItemForList());
			if (arrayAdapter != null && arrayAdapter.getCount() > 0) {
				return arrayAdapter;
			}
			return null;
		}

		@Override
		protected void onPostExecute(CustomDepartmentAdapter arrayAdapter) {
			progressDialog.dismiss();
			if (arrayAdapter != null) {
				setListAdapter(arrayAdapter);
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(onItemClickListener);
				Log.v("MainActivity", "ok");
			} else {
				String temp = context.getString(R.string.problem_information);
				arrayAdapter = new CustomDepartmentAdapter(context, R.layout.problem_main, new String[] {  temp});
				setListAdapter(arrayAdapter);
				try {
					Constans.RefreshSyncAdapter.refreshSyncAdapter(context);
				} catch (Exception e) {
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
				executeTask();
			}
		}

	}

	public class CustomDepartmentAdapter extends ArrayAdapter<String> {

		String[] objects;
		private final int textViewResourceId;

		public CustomDepartmentAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			this.textViewResourceId = textViewResourceId;
			this.objects = new String[objects.length];
			this.objects = objects;
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
			if(row == null){
				LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(textViewResourceId, parent, false);
			}
			int textViewID, iconID;
			if(R.layout.main == textViewResourceId){
				textViewID = R.id.list_item_firstscreen;
				iconID = R.id.department_icon;
			}
			else{
				textViewID = R.id.list_item_firstscreen_problem;
				iconID = R.id.department_icon_problem;
			}
			TextView label = (TextView) row
					.findViewById(textViewID);
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