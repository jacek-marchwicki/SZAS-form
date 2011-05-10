package com.szas.android.SZASApplication.UI;

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

import com.szas.android.SZASApplication.R;

//"http://szas-form.appspot.com/syncnoauth"
public class MainActivity extends ListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main, getItemForList()));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(onItemClickListener);
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
		        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {

		                //Stop the activity
		                MainActivity.this.finish();    
		            }
		        })
		        .setNegativeButton(R.string.no, null)
		        .show();
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
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(MainActivity.this, SecondActivity.class);
			i.putExtra("title", ((TextView) view).getText());
			startActivity(i);
		}
	};
	
	private String[] getItemForList(){
		//XXX somehow download or get from db lists of departments
		return new String[]{ "oddzial1", "oddzial2", "oddzial3"};
	}
	
	
	
	
}