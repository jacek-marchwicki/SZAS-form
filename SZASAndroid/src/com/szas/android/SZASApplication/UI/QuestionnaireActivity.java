/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.szas.android.SZASApplication.DAOClass.LocalDAOContener;
import com.szas.android.SZASApplication.R;
import com.szas.data.FieldDataTuple;
import com.szas.data.FieldIntegerBoxDataTuple;
import com.szas.data.FieldIntegerBoxTuple;
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
	ScrollView sv;
	MultiAutoCompleteTextView multiAutoCompleteTextView;
	String _filledId = null;
	private FilledQuestionnaireTuple filledQuestionnaireTuple;
	private QuestionnaireTuple questionnaireTuple;
	private ArrayList<FieldTuple> filledFields;
	boolean alreadySaved;
	String questionnaireName = null;
	int changed = 0;
	int counter = 0;
	long _id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this._id = getIntent().getExtras().getLong("questionnaryID");
		this._filledId = getIntent().getExtras().getString(
				"filledQuestionnaireID");
		sv = new ScrollView(this);
		linear = new LinearLayout(this);
		linear.setOrientation(LinearLayout.VERTICAL);
		sv.addView(linear);
		alreadySaved = false;
		new GetQuestonnaireFromDB().execute(0);

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
			alreadySaved = true;
			saveAll();
			onBackPressed();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		alreadySaved = false;
		super.onResume();
	}

	private void saveAll() {
		int childCount = linear.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View view = linear.getChildAt(i);

			if (view.getClass() == EditText.class) {
				insertChanges((EditText) view);
			} else if (view.getClass() == MultiAutoCompleteTextView.class) {
				insertChanges((MultiAutoCompleteTextView) view);
			}
		}
		saveAllChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (changed > 0) {
			if (!alreadySaved) {
				saveAll();
				alreadySaved = true;
			}
			setResult(RESULT_OK);
		} else
			setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	private void saveChanges(EditText editText) {
		insertChanges(editText);
		saveAllChanges();
	}

	private void insertChanges(EditText editText) {
		++changed;
		if (filledQuestionnaireTuple == null) {
			filledQuestionnaireTuple = questionnaireTuple.getFilled();
		}
		filledFields = filledQuestionnaireTuple.getFilledFields();
		for (FieldTuple fieldTuple : filledFields)
			if (fieldTuple.getName().equals(
					editText.getTag(R.id.nameTag).toString())) {
				((FieldTextBoxTuple) fieldTuple).setValue((editText.getText())
						.toString());
				break;
			}
		filledQuestionnaireTuple.setFilledFields(filledFields);
	}

	private void insertChanges(MultiAutoCompleteTextView editText) {
		++changed;
		if (filledQuestionnaireTuple == null) {
			filledQuestionnaireTuple = questionnaireTuple.getFilled();
		}
		filledFields = filledQuestionnaireTuple.getFilledFields();
		for (FieldTuple fieldTuple : filledFields)
			if (fieldTuple.getName().equals(
					editText.getTag(R.id.nameTag).toString())) {
				((FieldTextAreaTuple) fieldTuple).setValue((editText.getText())
						.toString());
				break;
			}
		filledQuestionnaireTuple.setFilledFields(filledFields);
	}

	private void saveAllChanges() {
		LocalDAOContener
				.insertUpdateFilledQuestionnaireTuple(filledQuestionnaireTuple);
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
					String text = editText.getText().toString();
					if (text != null && !text.equals("")) {
						saveChanges(editText);
					} else if (multiAutoCompleteTextView != null) {
						++changed;
					}
				}
			}
		}
	}

	private class CustomTextWatcher implements TextWatcher {
		String firstValue;

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			if (changed == 0 && s != null)
				firstValue = s.toString();
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (firstValue != null && firstValue.equals(s.toString())) {
				--changed;
				counter = 0;
			} else {
				++changed;
				++counter;
			}
		}

		public void afterTextChanged(Editable s) {
		}
	}

	private class GetQuestonnaireFromDB extends
			AsyncTask<Integer, Integer, Boolean> {

		ProgressDialog progressDialog;
		ArrayList<FieldDataTuple> questionnaireFields;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(QuestionnaireActivity.this);
			progressDialog.setTitle(getString(R.string.app_name));
			progressDialog.setIcon(R.drawable.icon);
			progressDialog.setMessage(getString(R.string.loading_progressbar));
			QuestionnaireActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.show();
				}
			});
			super.onPreExecute();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground(Integer... params) {
			if (_filledId == null) {
				questionnaireTuple = LocalDAOContener
						.getQuestionnaireTupleById(_id);
				questionnaireName = questionnaireTuple.getName();
				this.questionnaireFields = questionnaireTuple.getFields();
				return true;
			} else {
				filledQuestionnaireTuple = LocalDAOContener
						.getFilledQuestionnaireTupleById(Long
								.parseLong(_filledId));
				questionnaireName = filledQuestionnaireTuple.getName();
				filledFields = filledQuestionnaireTuple.getFilledFields();
				return false;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				loadText(questionnaireFields);
			} else {
				loadText(filledFields);
			}
			setContentView(sv);
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

	}

	private void loadText(ArrayList<?> objects) {
		for (Object object : objects) {
			String name = ((FieldTuple) object).getName().toString();
			text = new TextView(QuestionnaireActivity.this);
			text.setText(name);
			boolean isOnList = ((FieldTuple) object).isOnList();
			if ((object instanceof FieldTextBoxDataTuple)
					|| (object instanceof FieldTextBoxTuple)) {
				editText = new EditText(QuestionnaireActivity.this);
			} else if ((object instanceof FieldTextAreaTuple)
					|| (object instanceof FieldTextAreaDataTuple)) {
				editText = new MultiAutoCompleteTextView(
						QuestionnaireActivity.this);
				((MultiAutoCompleteTextView) editText).setSingleLine(false);
				editText.setMaxLines(4);
				editText.setMinLines(2);
				editText.setScrollbarFadingEnabled(true);
			} else if ((object instanceof FieldIntegerBoxTuple) || (object instanceof FieldIntegerBoxDataTuple)){
				editText = new EditText(QuestionnaireActivity.this);
				editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			}
			if (isOnList)
				editText.setBackgroundColor(android.graphics.Color.CYAN);
			String txt = ((FieldTuple) object).getText();
			if (txt != null)
				editText.setText(txt);
			editText.addTextChangedListener(new CustomTextWatcher());
			editText.setOnFocusChangeListener(new CustomOnFocusChangeListener(
					editText));
			editText.setTag(R.id.nameTag, name);
			editText.setTag(R.id.onListTag, Boolean.toString(isOnList));
			linear.addView(text);
			linear.addView(editText);
		}

	}

}
