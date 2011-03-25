package com.szas.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.szas.data.*;

public class MainActivity extends Activity {
	private ExampleData exampleData;
	public MainActivity() {
		exampleData = new ExampleData();
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(exampleData.read());
    }
}