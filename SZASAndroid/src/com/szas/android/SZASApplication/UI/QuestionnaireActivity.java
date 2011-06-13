/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import crl.android.pdfwriter.PDFWriter;
import crl.android.pdfwriter.StandardFonts;

/**
 * @author pszafer@gmail.com
 * 
 */
public class QuestionnaireActivity extends Activity {

	/**
	 * Linear layout and view to build screen
	 */
	LinearLayout linear;
	ScrollView sv;
	/**
	 * Text view to show header
	 */
	TextView text;

	/**
	 * Forms for input data
	 */
	View editText;
	RadioButton radioButton;
	MultiAutoCompleteTextView multiAutoCompleteTextView;

	/**
	 * ID of empty questionnaire /main ID/
	 */
	long _id;

	/**
	 * ID different than null if SecondActivity opens filled questionnaire
	 */
	String _filledId = null;

	/**
	 * Check if data was already saved, because we won't save it too many times
	 */
	boolean alreadySaved;

	/**
	 * Name of opened questionnaire
	 */
	String questionnaireName = null;

	/**
	 * Used to know if we exit Activity without change focus of any view
	 */
	int changed = 0;

	/**
	 * Used to know if we added some text to view
	 */
	int counter = 0;

	/**
	 * Local variable to save empty questionnaireTuple
	 */
	private QuestionnaireTuple questionnaireTuple;

	/**
	 * Local variable to save FilledQuestionnaireTuple
	 */
	private FilledQuestionnaireTuple filledQuestionnaireTuple;

	/**
	 * Empty fields to fill
	 */
	private ArrayList<FieldDataTuple> questionnaireFields;

	/**
	 * Filled fields from filledQuestionnaireTuple
	 */
	private ArrayList<FieldTuple> filledFields;

	ArrayList<String> isOnListStrings;

	/**
	 * Flag to know if AsyncTask is working to avoid recreating it on change
	 * screen orientation
	 */
	private boolean mShownDialog = false;

	private final static int DIALOG_ID = 1;

	private GetQuestonnaireFromDB mTask;

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);

		if (id == DIALOG_ID) {
			mShownDialog = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.questionnaires_layout);
		Intent i = getIntent();
		this._id = i.getExtras().getLong("questionnaryID");
		this.isOnListStrings = i.getStringArrayListExtra("isOnListStrings");
		this._filledId = i.getExtras().getString("filledQuestionnaireID");
		alreadySaved = false;
		Object retained = getLastNonConfigurationInstance();
		if (retained instanceof GetQuestonnaireFromDB) {
			mTask = (GetQuestonnaireFromDB) retained;
			mTask.setActivity(this);
		} else {
			mTask = new GetQuestonnaireFromDB(QuestionnaireActivity.this);
			mTask.execute(0);
		}

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		mTask.setActivity(null);
		return mTask;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ID:
			ProgressDialog progressDialog = new ProgressDialog(
					QuestionnaireActivity.this);
			progressDialog.setTitle(getString(R.string.app_name));
			progressDialog.setIcon(R.drawable.icon);
			progressDialog.setMessage(getString(R.string.loading_progressbar));
			return progressDialog;
		}
		return super.onCreateDialog(id);
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
		case R.id.savepdf:
			createPDF();
			return true;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
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

	/**
	 * Save all items on screen to local filledQuestionnaireTuple and
	 * sqlLocalDAO which are EditText or MultiAutoCompleteTextView
	 */
	private void saveAll() {
		int childCount = linear.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View view = linear.getChildAt(i);
			if (view.getClass() == MultiAutoCompleteTextView.class) {
				insertChanges((MultiAutoCompleteTextView) view);
			} else if (view.getClass() == EditText.class) {
				insertChanges((EditText) view);
			}
		}
		saveAllChanges();
	}

	/**
	 * Save changes into local dao and sql local dao for single item
	 * 
	 * @param editText
	 *            editText with data to insert
	 */
	private void saveChanges(EditText editText) {
		insertChanges(editText);
		saveAllChanges();
	}

	/**
	 * Save changes into local dao and sql local dao for single item
	 * 
	 * @param multiAutoCompleteTextView
	 *            MultiAutoCompleteTextView with data to insert
	 */
	private void saveChanges(MultiAutoCompleteTextView editText) {
		insertChanges(editText);
		saveAllChanges();
	}

	/**
	 * Insert changes into local filledQuestionnaireTuple from editText
	 * 
	 * @param editText
	 *            editText with data to insert
	 */
	private void insertChanges(EditText editText) {
		++changed;
		if (filledQuestionnaireTuple == null) {
			filledQuestionnaireTuple = questionnaireTuple.getFilled();
		}
		filledFields = filledQuestionnaireTuple.getFilledFields();
		String tag = editText.getTag(R.id.nameTag).toString();
		boolean isInteger = editText.getInputType() == EditorInfo.TYPE_CLASS_NUMBER ? true
				: false;
		for (FieldTuple fieldTuple : filledFields)
			if (fieldTuple.getName().equals(tag)) {
				if (!isInteger) {
					((FieldTextBoxTuple) fieldTuple).setValue((editText
							.getText()).toString());
				} else {
					((FieldIntegerBoxTuple) fieldTuple).setValue(Integer
							.parseInt(editText.getText().toString()));
				}
				break;
			}
		filledQuestionnaireTuple.setFilledFields(filledFields);
	}

	/**
	 * Insert changes into local filledQuestionnaireTuple from
	 * multiAutoCompleteTextView
	 * 
	 * @param multiAutoCompleteTextView
	 *            multiAutoCompleteTextView with data to insert
	 */
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

	/**
	 * Save all changes into local DAO (SQL LOCAL DAO)
	 */
	private void saveAllChanges() {
		LocalDAOContener
				.insertUpdateFilledQuestionnaireTuple(filledQuestionnaireTuple);
	}

	private void onTaskCompleted(int asyncResult) {
		if (mShownDialog) {
			dismissDialog(DIALOG_ID);
		}
		linear = (LinearLayout) findViewById(R.id.linearLayout1);
		if (asyncResult == 0) {
			loadData(questionnaireFields);
		} else {
			loadData(filledFields);
		}
	}

	/**
	 * Class to listen of focus changed in EditText or MultiAutoCompleteTextView
	 * 
	 * Distinct for MultiAutoCompleteTextView and EditText don't needed because
	 * MultiAutoCompleteTextView inherits from EditText
	 * 
	 * @author pszafer@gmail.com
	 * 
	 */
	private class CustomOnFocusChangeListener implements
			View.OnFocusChangeListener {
		private EditText editText;
		private MultiAutoCompleteTextView multiAutoCompleteTextView;

		/**
		 * Constructor for EditText
		 * 
		 * @param editText
		 *            if EditText loaded then get data from editText
		 */
		public CustomOnFocusChangeListener(EditText editText) {
			this.editText = editText;
		}

		/**
		 * Constructor for MultiAutoCompleteTextView
		 * 
		 * @param multiAutoCompleteTextView
		 *            if multiAutoCompleteTextView loaded then get data from
		 *            multiAutoCompleteTextView
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
					}
				} else if (multiAutoCompleteTextView != null) {
					String text = multiAutoCompleteTextView.getText()
							.toString();
					if (text != null && !text.equals("")) {
						saveChanges(multiAutoCompleteTextView);
					}
				}
			}
		}
	}

	/**
	 * Watch if some text was added
	 * 
	 * @author pszafer@gmail.com
	 * 
	 */
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
		 */
		@Override
		public void afterTextChanged(Editable s) {
			// nothing to do
		}
	}

	/**
	 * AsyncTask to get data from sql and avoid ANR problem
	 * 
	 * @author pszafer@gmail.com
	 * 
	 */
	private class GetQuestonnaireFromDB extends
			AsyncTask<Integer, Integer, Integer> {

		/**
		 * Local variable to save empty questionnaireTuple
		 */
		private QuestionnaireTuple questionnaireTuple;

		/**
		 * Local variable to save FilledQuestionnaireTuple
		 */
		private FilledQuestionnaireTuple filledQuestionnaireTuple;

		/**
		 * Empty fields to fill
		 */
		private ArrayList<FieldDataTuple> questionnaireFields;

		/**
		 * Filled fields from filledQuestionnaireTuple
		 */
		private ArrayList<FieldTuple> filledFields;

		QuestionnaireActivity activity;

		private boolean completed;
		private int asyncResult = -1;

		/**
		 * 
		 */
		public GetQuestonnaireFromDB(QuestionnaireActivity activity) {
			this.activity = activity;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			activity.showDialog(DIALOG_ID);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Integer doInBackground(Integer... params) {
			if (_filledId == null) {
				questionnaireTuple = LocalDAOContener
						.getQuestionnaireTupleById(_id);
				questionnaireName = questionnaireTuple.getName();
				questionnaireFields = questionnaireTuple.getFields();
				return 0;
			} else {
				filledQuestionnaireTuple = LocalDAOContener
						.getFilledQuestionnaireTupleById(Long
								.parseLong(_filledId));
				questionnaireName = filledQuestionnaireTuple.getName();

				filledFields = filledQuestionnaireTuple.getFilledFields();
				return 1;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Integer result) {
			this.asyncResult = result;
			completed = true;
			notifyActivityTaskCompleted();
			super.onPostExecute(result);
		}

		private void setActivity(QuestionnaireActivity activity) {
			this.activity = activity;
			if (completed) {
				notifyActivityTaskCompleted();
			}
		}

		private void notifyActivityTaskCompleted() {
			if (activity != null) {
				activity.questionnaireTuple = questionnaireTuple;
				activity.filledQuestionnaireTuple = filledQuestionnaireTuple;
				activity.questionnaireFields = questionnaireFields;
				activity.filledFields = filledFields;
				activity.onTaskCompleted(this.asyncResult);
			}
		}
	}

	/**
	 * LoadData and show on the screen. It's started from AsyncTask
	 * 
	 * Working with EditText with text, EditText with Integer values and
	 * MultiAutoCompleteTextView for multiline text input /long text input/
	 * 
	 * @param objects
	 *            can load two kind of objects - questionnaireFields or
	 *            filledFields
	 */
	private void loadData(ArrayList<?> objects) {

		for (Object object : objects) {
			String name = ((FieldTuple) object).getName().toString();
			text = new TextView(QuestionnaireActivity.this);
			text.setText(name);
			boolean isOnList = false;
			if (this.isOnListStrings != null
					&& this.isOnListStrings.contains(name)) {
				isOnList = true;
			}
			String txt = "";
			// REGULAR TEXT BOX
			if ((object instanceof FieldTextBoxDataTuple)
					|| (object instanceof FieldTextBoxTuple)) {
				editText = new EditText(QuestionnaireActivity.this);
				editText.setOnFocusChangeListener(new CustomOnFocusChangeListener(
						(EditText) editText));
				txt = ((FieldTextBoxTuple) object).getValue();
				if (txt != null)
					((EditText) editText).setText(txt);
				((EditText) editText)
						.addTextChangedListener(new CustomTextWatcher());
			} else
			// MULTILINE TEXT BOX
			if ((object instanceof FieldTextAreaTuple)
					|| (object instanceof FieldTextAreaDataTuple)) {
				editText = new MultiAutoCompleteTextView(
						QuestionnaireActivity.this);
				((MultiAutoCompleteTextView) editText).setSingleLine(false);
				editText.setOnFocusChangeListener(new CustomOnFocusChangeListener(
						((MultiAutoCompleteTextView) editText)));
				((EditText) editText).setMaxLines(4);
				((EditText) editText).setMinLines(2);
				editText.setScrollbarFadingEnabled(true);
				txt = ((FieldTextAreaTuple) object).getValue();
				if (txt != null)
					((EditText) editText).setText(txt);
				((EditText) editText)
						.addTextChangedListener(new CustomTextWatcher());
			} else
			// INTEGER TEXT BOX
			if ((object instanceof FieldIntegerBoxTuple)
					|| (object instanceof FieldIntegerBoxDataTuple)) {
				editText = new EditText(QuestionnaireActivity.this);
				editText.setOnFocusChangeListener(new CustomOnFocusChangeListener(
						((EditText) editText)));
				((EditText) editText)
						.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
				int val = ((FieldIntegerBoxTuple) object).getValue();
				if (val > -1)
					((EditText) editText).setText(String.valueOf(val));
				((EditText) editText)
						.addTextChangedListener(new CustomTextWatcher());
			}
			if (isOnList)
				editText.setBackgroundColor(android.graphics.Color.CYAN);

			editText.setTag(R.id.nameTag, name);
			editText.setTag(R.id.onListTag, Boolean.toString(isOnList));
			linear.addView(text);
			linear.addView(editText);
		}
	}

	/**
	 * Create pdf file Problems with encoding still really alpha
	 */
	private void createPDF() {
		PDFWriter pdfWriter = new PDFWriter();
		pdfWriter.setPageHeight(594);
		pdfWriter.setPageWidth(420);
		pdfWriter.setPageFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_ROMAN,
				StandardFonts.WIN_ANSI_ENCODING);
		int childCount = linear.getChildCount();
		int start = 360;
		int j = 0;
		for (int i = 0; i < childCount; ++i) {
			View view = linear.getChildAt(i);
			if (view.getClass() == EditText.class) {
				pdfWriter.addText(240, start, 14, ((EditText) view).getText()
						.toString());
				++j;
			} else if (view.getClass() == MultiAutoCompleteTextView.class) {
				pdfWriter
						.addText(240, start, 14,
								((MultiAutoCompleteTextView) view).getText()
										.toString());
				++j;
			} else if (view.getClass() == TextView.class) {
				pdfWriter.addText(40, start, 20, ((TextView) view).getText()
						.toString());
				++j;
			}

			if (j == 2) {
				j = 0;
				start -= 10;
				pdfWriter.addLine(30, start, 300, start);
				start -= 20;
			}
		}
		saveAllChanges();
		String s = pdfWriter.asString();
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File file = new File(sdCard, filledFields.get(0).getText() + ".pdf");
			FileOutputStream f = new FileOutputStream(file);
			f.write(s.getBytes("CP-1250"));
			f.flush();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
