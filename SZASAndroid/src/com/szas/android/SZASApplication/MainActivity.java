package com.szas.android.SZASApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.Tuple;
import com.szas.sync.remote.RemoteTuple;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;


//"http://szas-form.appspot.com/syncnoauth"
public class MainActivity extends Activity {
	//private ExampleData exampleData;

	public MainActivity() {
		//exampleData = new ExampleData();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.main);
	//	TextView textView = (TextView) findViewById(R.id.textView);
		//textView.setText(exampleData.read());
		InputStream inputStream = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://szas-form.appspot.com/syncnoauth");
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				inputStream = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (inputStream != null) {
			ArrayList<SyncedElementsHolder> result = new JSONDeserializer<ArrayList<SyncedElementsHolder>>()
				.deserialize(new Scanner(inputStream).useDelimiter("\\A").next());
			// JsonNode rootNode = objectMapper.readValue(new
			// URL("http://szas-form.appspot.com/syncnoauth"), JsonNode.class);
			ContentValues initialValues = new ContentValues();
	        initialValues.put(DBContentProvider.DBCOL_ID, ((Tuple)((RemoteTuple) result.get(0).syncedElements.get(0)).getElement()).getId());
	        initialValues.put(DBContentProvider.DBCOL_syncTimestamp, result.get(0).syncTimestamp);
	        initialValues.put(DBContentProvider.DBCOL_status, ((RemoteTuple) result.get(0).syncedElements.get(0)).isDeleted()?1:0);
	        initialValues.put(DBContentProvider.DBCOL_form,new JSONSerializer().include("*").serialize((Object)(((RemoteTuple)result.get(0).syncedElements.get(0)).getElement())));
	        getContentResolver().insert(DBContentProvider.CONTENT_URI, initialValues);
			Log.v("test", "test");

		}
	}
}