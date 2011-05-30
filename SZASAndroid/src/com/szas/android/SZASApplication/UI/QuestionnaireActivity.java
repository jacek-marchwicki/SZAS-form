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
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
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
	RelativeLayout relativeLayout;
	ScrollView sv;
	/**
	 * Text view to show header
	 */
	TextView text;

	/**
	 * Forms for input data
	 */
	EditText editText;
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
	 * Local variable to save FilledQuestionnaireTuple
	 */
	private FilledQuestionnaireTuple filledQuestionnaireTuple;

	/**
	 * Filled fields from filledQuestionnaireTuple
	 */
	private ArrayList<FieldTuple> filledFields;
	/**
	 * Local variable to save empty questionnaireTuple
	 */
	private QuestionnaireTuple questionnaireTuple;

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
		relativeLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		relativeLayout.setLayoutParams(params);
		linear.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		linear.setLayoutParams(linearParams);
	//	relativeLayout.addView(linear);
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
		for (FieldTuple fieldTuple : filledFields)
			if (fieldTuple.getName().equals(
					editText.getTag(R.id.nameTag).toString())) {
				((FieldTextBoxTuple) fieldTuple).setValue((editText.getText())
						.toString());
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
		EditText editText;
		MultiAutoCompleteTextView multiAutoCompleteTextView;

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

		ProgressDialog progressDialog;

		/**
		 * Empty fields to fill
		 */
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
		protected Integer doInBackground(Integer... params) {
			if (_filledId == null) {
				questionnaireTuple = LocalDAOContener
						.getQuestionnaireTupleById(_id);
				questionnaireName = questionnaireTuple.getName();
				this.questionnaireFields = questionnaireTuple.getFields();
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
			if (result == 0) {
				loadData(questionnaireFields);
			} else {
				loadData(filledFields);
			}
			setContentView(sv);
			progressDialog.dismiss();
			super.onPostExecute(result);
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
			boolean isOnList = ((FieldTuple) object).isOnList();
			String txt = "";
			// REGULAR TEXT BOX
			if ((object instanceof FieldTextBoxDataTuple)
					|| (object instanceof FieldTextBoxTuple)) {
				editText = new EditText(QuestionnaireActivity.this);
				editText.setOnFocusChangeListener(new CustomOnFocusChangeListener(
						editText));
				txt = ((FieldTextBoxTuple) object).getValue();
				if (txt != null)
					editText.setText(txt);
			} else
			// MULTILINE TEXT BOX
			if ((object instanceof FieldTextAreaTuple)
					|| (object instanceof FieldTextAreaDataTuple)) {
				editText = new MultiAutoCompleteTextView(
						QuestionnaireActivity.this);
				((MultiAutoCompleteTextView) editText).setSingleLine(false);
				editText.setOnFocusChangeListener(new CustomOnFocusChangeListener(
						((MultiAutoCompleteTextView) editText)));
				editText.setMaxLines(4);
				editText.setMinLines(2);
				editText.setScrollbarFadingEnabled(true);
				txt = ((FieldTextAreaTuple) object).getValue();
				if (txt != null)
					editText.setText(txt);
			} else
			// INTEGER TEXT BOX
			if ((object instanceof FieldIntegerBoxTuple)
					|| (object instanceof FieldIntegerBoxDataTuple)) {
				editText = new EditText(QuestionnaireActivity.this);
				editText.setOnFocusChangeListener(new CustomOnFocusChangeListener(
						editText));
				editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
				int val = ((FieldIntegerBoxTuple) object).getValue();
				if (val > -1)
					editText.setText(val);
			}
			if (isOnList)
				editText.setBackgroundColor(android.graphics.Color.CYAN);

			editText.addTextChangedListener(new CustomTextWatcher());
			editText.setTag(R.id.nameTag, name);
			editText.setTag(R.id.onListTag, Boolean.toString(isOnList));
			linear.addView(text);
			linear.addView(editText);
		}
		LinearLayout linear2 = new LinearLayout(QuestionnaireActivity.this);
		SeekBar seekBar = new SeekBar(QuestionnaireActivity.this);
		TextView textView = new TextView(QuestionnaireActivity.this);
		LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textView.setLayoutParams(params2);
		seekBar.setLayoutParams(params2);
		try {
			textView.setText("test");
			seekBar.setMax(180);
			//seekBarView.addView(seekBar);
			//seekBarView.addView(textView);
			//linear.addView(seekBarView);
			linear2.addView(textView);
			linear2.addView(seekBar);
			linear.addView(linear2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// linear.addView(view);
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
