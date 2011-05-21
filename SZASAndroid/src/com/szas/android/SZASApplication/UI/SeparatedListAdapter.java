/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.szas.android.SZASApplication.ObservableAdapter;
import com.szas.android.SZASApplication.R;
import com.szas.android.SZASApplication.UI.SecondActivity.CustomArrayAdapter;

/**
 * @author pszafer@gmail.com
 * 
 */
public class SeparatedListAdapter extends BaseAdapter implements
		ObservableAdapter {

	public final Map<String, CustomArrayAdapter> sections = new LinkedHashMap<String, CustomArrayAdapter>();
	public final ArrayAdapter<String> headers;
	public final static int TYPE_SECTION_HEADER = 0;

	public SeparatedListAdapter(Context context) {
		headers = new ArrayAdapter<String>(context, R.layout.list_header);
	}

	public void addSection(String section, CustomArrayAdapter adapter) {
		if (section != null && adapter != null) {
			this.headers.add(section);
			this.sections.put(section, adapter);
			adapter.registerDataSetObserver(mDataSetObserver);
		}
	}

	public void refreshSection(String section, CustomArrayAdapter adapter) {
		this.sections.put(section, adapter);
	}

	public Object getItem(int position) {
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return section;
			if (position < size)
				return adapter.getItem(position - 1);

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}

	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for (Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}

	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 1;
		for (Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}

	public int getItemViewType(int position) {
		int type = 1;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return TYPE_SECTION_HEADER;
			if (position < size)
				return type + adapter.getItemViewType(position - 1);

			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return headers.getView(sectionnum, convertView, parent);
			if (position < size)
				return adapter.getView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void removeObserver() {
		// Notify all our children that they should release their observers too.
		for (Map.Entry<String, CustomArrayAdapter> it : sections.entrySet()) {
			if (it.getValue() instanceof ObservableAdapter) {
				ObservableAdapter adapter = (ObservableAdapter) it.getValue();
				adapter.removeObserver();
			}
		}
	}

	public void clear() {
		headers.clear();
		sections.clear();
		notifyDataSetInvalidated();
	}

	private DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			notifyDataSetChanged();
		}
	};

}
