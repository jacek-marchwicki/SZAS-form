/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
import com.szas.android.SZASApplication.R;
import com.szas.data.FieldDataTuple;
import com.szas.data.FieldTextAreaDataTuple;
import com.szas.data.FieldTextAreaTuple;
import com.szas.data.FieldTextBoxDataTuple;
import com.szas.data.FieldTextBoxTuple;
import com.szas.data.FieldTuple;
import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;

/**
 * @author pszafer@gmail.com
 * 
 */
public class QuestionnaireActivity extends Activity {

	LinearLayout linear;
	TextView text;
	EditText editText;
	RadioButton radioButton;
	MultiAutoCompleteTextView multiAutoCompleteTextView;
	String _Fid = null;
	private FilledQuestionnaireTuple filledQuestionnaireTuple;
	private ArrayList<FieldTuple> filledFields;
	String questionnaireName = null;
	int changed = 0;
	int counter = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long _id = getIntent().getExtras().getLong("questionnaryID");
		String _Fid = getIntent().getExtras()
				.getString("filledQuestionnaireID");
		this._Fid = _Fid;
		ScrollView sv = new ScrollView(this);
		linear = new LinearLayout(this);
		linear.setOrientation(LinearLayout.VERTICAL);

		sv.addView(linear);
		if (_Fid == null) {
			QuestionnaireTuple questionnaireTuple;
			questionnaireTuple = LocalDAOContener
					.getQuestionnaireTupleById(_id);
			questionnaireName = questionnaireTuple.getName();
			ArrayList<FieldDataTuple> fields = questionnaireTuple.getFields();
			for (FieldDataTuple fieldDataTuple : fields) {
				if (fieldDataTuple instanceof FieldTextBoxDataTuple) {
					text = new TextView(this);
					String name = fieldDataTuple.getName().toString();
					text.setText(name);
					editText = new EditText(this);
					editText.addTextChangedListener(new CustomTextWatcher());
					editText.setTag(name);
					linear.addView(text);
					linear.addView(editText);
				} else if (fieldDataTuple instanceof FieldTextAreaDataTuple) {
					text = new TextView(this);
					String name = fieldDataTuple.getName().toString();
					text.setText(name);
					multiAutoCompleteTextView = new MultiAutoCompleteTextView(
							this);
					multiAutoCompleteTextView
							.addTextChangedListener(new CustomTextWatcher());
					linear.addView(text);
					linear.addView(multiAutoCompleteTextView);
				}
			}
		} else {
			filledQuestionnaireTuple = LocalDAOContener
					.getFilledQuestionnaireTupleById(Long.parseLong(_Fid));
			questionnaireName = filledQuestionnaireTuple.getName();
			// FIXME if not uploaded to server we got null here
			filledFields = filledQuestionnaireTuple.getFilledFields();
			for (FieldTuple fieldTuple : filledFields) {
				if (fieldTuple instanceof FieldTextBoxTuple) {
					text = new TextView(this);
					String name = fieldTuple.getName().toString();
					text.setText(name);
					editText = new EditText(this);
					String txt = ((FieldTextBoxTuple) fieldTuple).getValue();
					if (txt != null)
						editText.setText(txt);
					editText.addTextChangedListener(new CustomTextWatcher());
					editText.setOnFocusChangeListener(new CustomOnFocusChangeListener(
							editText));
					editText.setTag(name);
					linear.addView(text);
					linear.addView(editText);
				} else if (fieldTuple instanceof FieldTextAreaTuple) {
					text = new TextView(this);
					String name = fieldTuple.getName().toString();
					text.setText(name);
					multiAutoCompleteTextView = new MultiAutoCompleteTextView(
							this);
					String txt = ((FieldTextAreaTuple) fieldTuple).getValue();
					if (txt != null)
						multiAutoCompleteTextView.setText(txt);
					multiAutoCompleteTextView
							.addTextChangedListener(new CustomTextWatcher());
					multiAutoCompleteTextView
							.setOnFocusChangeListener(new CustomOnFocusChangeListener(
									multiAutoCompleteTextView));
					linear.addView(text);
					linear.addView(multiAutoCompleteTextView);
				}
			}
		}
		setContentView(sv);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.questionnaire_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.saveclose:
			int childCount = linear.getChildCount();
			for(int i=0; i<childCount; ++i){
				View view = linear.getChildAt(i);
				if(view instanceof EditText){
					saveChanges((EditText)view);
				}
				else if (view instanceof MultiAutoCompleteTextView) {
					
				}
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void saveChanges(EditText editText){
		++changed;
		if (filledQuestionnaireTuple == null) {
			filledQuestionnaireTuple = new FilledQuestionnaireTuple();
			filledQuestionnaireTuple.setName(questionnaireName);
		}
		if (filledFields == null) {
			filledFields = new ArrayList<FieldTuple>();
			FieldTuple fieldTuple = new FieldTextBoxTuple();
			fieldTuple.setName(editText.getTag().toString());
			((FieldTextBoxTuple) fieldTuple).setValue((editText
					.getText()).toString());
			filledFields.add(fieldTuple);
			// 1
			filledQuestionnaireTuple.setFilledFields(filledFields);
			LocalDAOContener
					.insertFilledQuestionnaireTuple(filledQuestionnaireTuple);
		} else {
			for (FieldTuple fieldTuple : filledFields)
				if (fieldTuple.getName().equals(
						editText.getTag().toString())) {
					((FieldTextBoxTuple) fieldTuple)
							.setValue((editText.getText())
									.toString());
					break;
				}
			filledQuestionnaireTuple.setFilledFields(filledFields);
			LocalDAOContener
					.updateFilledQuestionnaireTuple(filledQuestionnaireTuple);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if(changed > 0)
			setResult(RESULT_OK); //, getIntent().putExtra("changed", changed)); Could be used to refresh only one row in list
		else
			setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	private class CustomOnFocusChangeListener implements
			View.OnFocusChangeListener {
		EditText editText;
		MultiAutoCompleteTextView multiAutoCompleteTextView;

		/**
		 * 
		 */
		public CustomOnFocusChangeListener(EditText editText) {
			this.editText = editText;
		}

		/**
		 * 
		 */
		public CustomOnFocusChangeListener(
				MultiAutoCompleteTextView multiAutoCompleteTextView) {
			this.multiAutoCompleteTextView = multiAutoCompleteTextView;
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus && counter > 0) {
				counter = 0;
				if (editText != null) {
					saveChanges(editText);
				} else if (multiAutoCompleteTextView != null) {
					++changed;
				}
			}
		}
	}

	private class CustomTextWatcher implements TextWatcher {
		String firstValue;
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			if(changed == 0 )firstValue = s.toString();
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(firstValue.equals(s.toString())){
				--changed;
				counter = 0;
			}
			else{
			++changed;
			++counter;
			}
		}

		public void afterTextChanged(Editable s) {
		}
	}

}
