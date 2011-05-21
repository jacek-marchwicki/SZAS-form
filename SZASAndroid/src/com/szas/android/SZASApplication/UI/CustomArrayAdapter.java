
package com.szas.android.SZASApplication.UI;

import java.util.List;

import com.szas.android.SZASApplication.QuestionnaireTypeRow;
import com.szas.android.SZASApplication.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author pszafer@gmail.com
 *
 */
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
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if(row == null){
				LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.second_screen, parent, false);
			}
			QuestionnaireTypeRow questionnaireTypeRow = objects.get(position);
			TextView textView = (TextView) row.findViewById(R.id.list_item_title);
			ImageView view = (ImageView) row.findViewById(R.id.filled_icon);
			Drawable drawable = getContext().getResources().getDrawable(R.drawable.icon);
			view.setImageDrawable(drawable);
			textView.setText(questionnaireTypeRow.getFullName().equals("") ? questionnaireTypeRow
					.getName() : questionnaireTypeRow.getFullName());
			return row;
		}
	}
