/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.szas.android.SZASApplication.R;


/**
 * @author pszafer@gmail.com
 * 
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class SecondActivity extends ListActivity {
	//private Context context = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//context = getApplicationContext();
		String text = getIntent().getExtras().getString("title");
		setTitle(getString(R.string.second_window_title) + " " + text);

		setListAdapter(new ArrayAdapter<String>(this, R.layout.main,
				getItemForList()));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(onItemClickListener);
	}

	/**
	 * Get departments to show in the listView
	 * 
	 * @return departments String[]
	 */
	private String[] getItemForList() {
		// XXX somehow download or get from db lists of departments
		return new String[] { "ble1", "ble2", "ble3" };
	}

	/**
	 * 
	 */
	public SecondActivity() {

	}

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
			AlertDialog.Builder builder = new AlertDialog.Builder(
					SecondActivity.this);
			items = new String[] { getString(R.string.form_item1),
					getString(R.string.form_item2),
					getString(R.string.form_item3) };
			builder.setTitle("Pick a tile set");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					Toast.makeText(SecondActivity.this,
							"You selected: " + items[item], Toast.LENGTH_LONG)
							.show();
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};
}
