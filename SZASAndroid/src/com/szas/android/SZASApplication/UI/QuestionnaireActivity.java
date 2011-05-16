/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long _id = getIntent().getExtras().getLong("questionnaryID");
		String _Fid = getIntent().getExtras().getString("filledQuestionnaireID");
		this._Fid = _Fid;
		ScrollView sv = new ScrollView(this);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		linear = new LinearLayout(this);
		linear.setOrientation(LinearLayout.VERTICAL);

		sv.addView(linear);
		if(_Fid == null){
			QuestionnaireTuple questionnaireTuple;
			questionnaireTuple = LocalDAOContener.getQuestionnaireTupleById(_id);
			questionnaireName = questionnaireTuple.getName();
			ArrayList<FieldDataTuple> fields = questionnaireTuple.getFields();
			for (FieldDataTuple fieldDataTuple : fields) {
			if (fieldDataTuple instanceof FieldTextBoxDataTuple) {
				text = new TextView(this);
				String name = fieldDataTuple.getName().toString();
				text.setText(name);
				editText = new EditText(this);
				editText.addTextChangedListener(new CustomTextWatcher(editText));
				editText.setTag(name);
				linear.addView(text);
				linear.addView(editText);
			} else
			if (fieldDataTuple instanceof FieldTextAreaDataTuple) {
				text = new TextView(this);
				String name = fieldDataTuple.getName().toString();
				text.setText(name);
				multiAutoCompleteTextView = new MultiAutoCompleteTextView(this);
				multiAutoCompleteTextView
						.addTextChangedListener(new CustomTextWatcher(
								multiAutoCompleteTextView));
				linear.addView(text);
				linear.addView(multiAutoCompleteTextView);
			}
		}
		}
		else{
			filledQuestionnaireTuple = LocalDAOContener.getFilledQuestionnaireTupleById(Long.parseLong(_Fid));
			questionnaireName = filledQuestionnaireTuple.getName();
			//FIXME if not uploaded to server we got null here
			filledFields = filledQuestionnaireTuple.getFilledFields();
			for(FieldTuple fieldTuple: filledFields){
				if(fieldTuple instanceof FieldTextBoxTuple){
					text = new TextView(this);
					String name = fieldTuple.getName().toString();
					text.setText(name);
					editText = new EditText(this);
					String txt = ((FieldTextBoxTuple) fieldTuple).getValue();
					if(txt!= null)editText.setText(txt);
					editText.addTextChangedListener(new CustomTextWatcher(editText));
					editText.setTag(name);
					linear.addView(text);
					linear.addView(editText);
				} else
					if (fieldTuple instanceof FieldTextAreaTuple) {
						text = new TextView(this);
						String name = fieldTuple.getName().toString();
						text.setText(name);
						multiAutoCompleteTextView = new MultiAutoCompleteTextView(this);
						String txt = ((FieldTextAreaTuple)fieldTuple).getValue();
						if(txt!= null) multiAutoCompleteTextView.setText(txt);
						multiAutoCompleteTextView
								.addTextChangedListener(new CustomTextWatcher(
										multiAutoCompleteTextView));
						linear.addView(text);
						linear.addView(multiAutoCompleteTextView);
					}
			}
		}
		setContentView(sv);
	}
	

	private class CustomTextWatcher implements TextWatcher {
		private EditText mEditText;
		private MultiAutoCompleteTextView mMultiAutoCompleteTextView;

		public CustomTextWatcher(EditText e) {
			mEditText = e;
		}

		public CustomTextWatcher(MultiAutoCompleteTextView m) {
			mMultiAutoCompleteTextView = m;
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
	    	if(mEditText != null){
	    		if(filledQuestionnaireTuple == null){ 
	    			filledQuestionnaireTuple = new FilledQuestionnaireTuple();
	    			filledQuestionnaireTuple.setName(questionnaireName);
	    		}
	    		if(filledFields == null){
	    			filledFields = new ArrayList<FieldTuple>();
	    			FieldTuple fieldTuple = new FieldTuple();
	    			fieldTuple.setName(mEditText.getTag().toString());
	    			((FieldTextBoxTuple)fieldTuple).setValue((mEditText.getText()).toString());
	    			filledFields.add(fieldTuple);
	    			//1
	    			filledQuestionnaireTuple.setFilledFields(filledFields);
	    			LocalDAOContener.insertFilledQuestionnaireTuple(filledQuestionnaireTuple);
	    		}
	    		else
	    		{
	    			for(FieldTuple fieldTuple : filledFields)
	    				if(fieldTuple.getName().equals(mEditText.getTag().toString())){
	    					((FieldTextBoxTuple)fieldTuple).setValue((mEditText.getText()).toString());
	    					break;
	    				}
	    			filledQuestionnaireTuple.setFilledFields(filledFields);
	    			LocalDAOContener.updateFilledQuestionnaireTuple(filledQuestionnaireTuple);
	    		}
	    		
	    		
	    	}
	    	else if(mMultiAutoCompleteTextView != null){
	    		
	    	}
	    }

		public void afterTextChanged(Editable s) {
		}
	}

}
