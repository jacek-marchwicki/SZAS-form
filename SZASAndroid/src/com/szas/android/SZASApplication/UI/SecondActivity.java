/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import com.szas.android.SZASApplication.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author pszafer@gmail.com
 *
 */
public class SecondActivity extends ListActivity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		String text = getIntent().getExtras().getString("title");
		setTitle(getString(R.string.second_window_title) + " " + text);
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main, getItemForList()));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		//lv.setOnItemClickListener(onItemClickListener);
	}
	
	private String[] getItemForList(){
		//XXX somehow download or get from db lists of departments
		return new String[]{ "ble1", "ble2", "ble3"};
	}
	
	/**
	 * 
	 */
	public SecondActivity() {
		
	}
}
