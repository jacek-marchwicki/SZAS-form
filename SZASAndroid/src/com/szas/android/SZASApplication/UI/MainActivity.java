package com.szas.android.SZASApplication.UI;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
import com.szas.android.SZASApplication.R;
import com.szas.android.SZASApplication.SyncService;
import com.szas.data.FieldTextBoxDataTuple;
import com.szas.data.FieldTextBoxTuple;
import com.szas.data.FieldTuple;
import com.szas.data.QuestionnaireTuple;
import com.szas.sync.local.LocalTuple;

import flexjson.JSONSerializer;

//"http://szas-form.appspot.com/syncnoauth
/**
 * @author pszafer@gmail.com LEGEND: XXX - adnotation FIXME - something wrong
 *         TODO - not implemented yet
 */
public class MainActivity extends ListActivity {

	//private LocalDAO<QuestionnaireTuple> questionnaireDAO;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		startService(new Intent(context, SyncService.class));

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
									MainActivity.this.finish();
								}
							}).setNegativeButton(R.string.no, null).show();
			return true;
		case R.id.about_item:
			try {
				AboutDialog.AboutDialogBuilder.createAboutWindow(this).show();
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			startActivity(i);
		}
	};

	String[] listViewElementsArray;

	private class GetItemFromDatabase extends
			AsyncTask<Integer, Integer, ArrayAdapter<String>> {
		ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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
					getApplicationContext(), R.layout.main, getItemForList());
			if (arrayAdapter != null && arrayAdapter.getCount() > 0) {
				return arrayAdapter;
			}
			return null;
		}

		private String[] getItemForList() {
			ArrayList<String> array = new ArrayList<String>();
			LocalDAOContener.loadContext(getApplicationContext());
			Collection<QuestionnaireTuple> qq = LocalDAOContener
					.getQuestionnaireTuples();
			for (QuestionnaireTuple q : qq) {
				array.add(q.getName());
			}
			listViewElementsArray = new String[array.size()];
			array.toArray(listViewElementsArray);
			return listViewElementsArray;
		}

		protected void onPostExecute(ArrayAdapter<String> arrayAdapter) {
			progressDialog.dismiss();
			if (arrayAdapter != null){
				setListAdapter(arrayAdapter);
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(onItemClickListener);
			}
		}
	}

}