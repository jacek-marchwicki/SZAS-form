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
import com.szas.data.FieldTextBoxDataTuple;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String _id = getIntent().getExtras().getString("questionnaryID");
		ScrollView sv = new ScrollView(this);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		linear = new LinearLayout(this);
		linear.setOrientation(LinearLayout.VERTICAL);

		sv.addView(linear);

		Long id = Long.parseLong(_id);
		QuestionnaireTuple questionnaireTuple = LocalDAOContener
				.getQuestionnaireTupleById(id);
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
			}
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
	    		FilledQuestionnaireTuple filledQuestionnaireTuple = new FilledQuestionnaireTuple();
	    		
	    		String name = (String) mEditText.getTag();
	    		filledQuestionnaireTuple.setName(name);
	    		
	    		ArrayList<FieldTuple> arrayList = new ArrayList<FieldTuple>();
	  //  		FieldDataTuple fieldDataTuple = new

	    		//arrayList.add()
	    //		mEditText.getText();
	    		
	    	}
	    	else if(mMultiAutoCompleteTextView != null){
	    		
	    	}
	    }

		public void afterTextChanged(Editable s) {
		}
	}

}
