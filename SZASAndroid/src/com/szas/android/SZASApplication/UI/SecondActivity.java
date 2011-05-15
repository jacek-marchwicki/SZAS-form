/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
import com.szas.android.SZASApplication.R;


/**
 * @author pszafer@gmail.com
 * 
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 * @param <T>
 */
public class SecondActivity<T> extends ListActivity {
	//private Context context = null;

	private String questionnaryName;
	private HashMap<Long, T> elements;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//context = getApplicationContext();
		String text = getIntent().getExtras().getString("title");
		questionnaryName = getIntent().getExtras().getString("questionnaryName");
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
	@SuppressWarnings("unchecked")
	private String[] getItemForList() {
		elements = (HashMap<Long, T>)LocalDAOContener.getTuplesByName(questionnaryName);
		String[] array = new String[elements.size()];
		elements.values().toArray(array);
		return array;
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
/*			AlertDialog.Builder builder = new AlertDialog.Builder(
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
			alert.show();*/
			Intent i = new Intent(SecondActivity.this, QuestionnaireActivity.class);
			i.putExtra("title", ((TextView) view).getText());
			Long[] longArray = new Long[elements.size()];
			elements.keySet().toArray(longArray);
			String _id = longArray[(int) id].toString();
			i.putExtra("questionnaryID", _id);
			startActivity(i);
		}
	};
}
