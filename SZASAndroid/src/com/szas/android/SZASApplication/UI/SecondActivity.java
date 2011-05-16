/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
import com.szas.android.SZASApplication.QuestionnaireTypeRow;
import com.szas.android.SZASApplication.R;
import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;

/**
 * @author pszafer@gmail.com
 * 
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 * @param <T>
 */
public class SecondActivity extends ListActivity {
	// private Context context = null;

	private String questionnaryName;
	List<QuestionnaireTuple> questionnaireTuples;
	List<FilledQuestionnaireTuple> filledQuestionnaireTuples;
	List<QuestionnaireTypeRow> mQuestionnaireTypeRows;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// context = getApplicationContext();
		String text = getIntent().getExtras().getString("title");
		questionnaryName = getIntent().getExtras()
				.getString("questionnaryName");
		setTitle(getString(R.string.second_window_title) + " " + text);
		new GetItemFromDatabase().execute(0);
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
			/*
			 * AlertDialog.Builder builder = new AlertDialog.Builder(
			 * SecondActivity.this); items = new String[] {
			 * getString(R.string.form_item1), getString(R.string.form_item2),
			 * getString(R.string.form_item3) };
			 * builder.setTitle("Pick a tile set"); builder.setItems(items, new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int item) {
			 * Toast.makeText(SecondActivity.this, "You selected: " +
			 * items[item], Toast.LENGTH_LONG) .show(); dialog.dismiss(); } });
			 * AlertDialog alert = builder.create(); alert.show();
			 */
			Intent i = new Intent(SecondActivity.this,
					QuestionnaireActivity.class);
			
			i.putExtra("title", ((TextView) view.findViewById(R.id.second_screen_textview)).getText());
			
			long _id = mQuestionnaireTypeRows.get((int) id).getId();
			i.putExtra("questionnaryID", mQuestionnaireTypeRows.get(0).getId());
			if(id >0) i.putExtra("filledQuestionnaireID", String.valueOf(_id));
			i.putExtra("questionnaryType", mQuestionnaireTypeRows.get((int) id).getType());
			startActivity(i);
		}
	};

	private class GetItemFromDatabase extends
			AsyncTask<Integer, Integer, CustomArrayAdapter> {
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
			progressDialog.show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected CustomArrayAdapter doInBackground(Integer... params) {
			CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(
					getApplicationContext(), R.layout.second_screen, getItemForList());
			if (arrayAdapter != null && arrayAdapter.getCount() > 0) {
				return arrayAdapter;
			}
			return null;
		}

		/**
		 * Get departments to show in the listView
		 * 
		 * @return departments String[]
		 */
		private List<QuestionnaireTypeRow> getItemForList() {
			questionnaireTuples = new ArrayList<QuestionnaireTuple>(LocalDAOContener.getQuestionnaireTuplesByName(questionnaryName));
			filledQuestionnaireTuples = new ArrayList<FilledQuestionnaireTuple>(LocalDAOContener.getFilledQuestionnaireTupleByName(questionnaryName));
			List<QuestionnaireTypeRow> questionnaireTypeRows = new ArrayList<QuestionnaireTypeRow>();
			long id =  questionnaireTuples.get(0).getId();
			questionnaireTypeRows.add(new QuestionnaireTypeRow( questionnaireTuples.get(0).getName(), 0,id));
			for(FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples){
				long id2 = filledQuestionnaireTuple.getId();
				questionnaireTypeRows.add(new QuestionnaireTypeRow(filledQuestionnaireTuple.getName(), 1, id2));
			//	elements.put(filledQuestionnaireTuple.getId(), filledQuestionnaireTuple);
			}
			mQuestionnaireTypeRows = questionnaireTypeRows;
			return questionnaireTypeRows;
		}

		protected void onPostExecute(CustomArrayAdapter arrayAdapter) {
			progressDialog.dismiss();
			if (arrayAdapter != null) {
				setListAdapter(arrayAdapter);
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(onItemClickListener);
			}
		}
	}
	
	private class CustomArrayAdapter extends ArrayAdapter<QuestionnaireTypeRow> {
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


		List<QuestionnaireTypeRow> objects;
		

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater layoutInflater = getLayoutInflater();
			View row= layoutInflater.inflate(R.layout.second_screen, parent, false);
			QuestionnaireTypeRow questionnaireTypeRow = objects.get(position);
			TextView textView = (TextView) row.findViewById(R.id.second_screen_textview);
			int type= questionnaireTypeRow.getType() ;
			if(type== 0)
				textView.setBackgroundColor(android.graphics.Color.BLACK);
			else if(type==1)
				textView.setBackgroundColor(android.graphics.Color.DKGRAY);
			textView.setText(questionnaireTypeRow.getName());
			return row;
		}
		
		
	}
}
