package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;
import java.util.Collection;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
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
import com.szas.data.QuestionnaireTuple;
import com.szas.sync.local.LocalDAO;

//"http://szas-form.appspot.com/syncnoauth
/**
 * @author pszafer@gmail.com LEGEND: XXX - adnotation FIXME - something wrong
 *         TODO - not implemented yet
 */
public class MainActivity extends ListActivity {

	private LocalDAO<QuestionnaireTuple> questionnaireDAO;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(getApplicationContext(), SyncService.class));
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main,
				getItemForList()));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(onItemClickListener);
		
		// Log.v("accountType", accounts[0].type);
		

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

	String[]  listViewElementsArray;
	/**
	 * Get elements which should be displayed in the listView
	 * 
	 * @return
	 */
	private String[] getItemForList() {
		//TODO make run on UI Thread
		ArrayList<String> array = new ArrayList<String>();
		LocalDAOContener.loadContext(getApplicationContext());
		Collection<QuestionnaireTuple> qq = LocalDAOContener.getQuestionnaireTuples();
		for(QuestionnaireTuple q : qq){
			 array.add(q.getName());
		}
		listViewElementsArray = new String[array.size()];
		array.toArray(listViewElementsArray);
		return listViewElementsArray;
	}

}