package com.example.lastactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class WEBS_Board_NOTICE extends Activity {
	ArrayAdapter<?> adapter;
	ListView lv;
	Context mctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webs_board_notice);

		String strurl = "http://wpg.azurewebsites.net/webs_notice_List.jsp?";

		DownloadTask downloadTask = new DownloadTask();

		downloadTask.execute(strurl);

		lv = (ListView) findViewById(R.id.lv_webs_board_notice);
		mctx = this;
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String c = String.valueOf(position);

					Intent intent1 = new Intent(mctx,
							WEBS_BOARD_NOTICE_LIST.class);
					intent1.putExtra("id", c);

					startActivity(intent1);
					Log.i("success", "here1");
					
			}
		});

	}

	private String downloadUrl(String strurl) throws IOException {
		String data = "";
		InputStream iStream = null;
		try {
			URL url = new URL(strurl);

			// Creating an http connection to communicate with url
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
		}

		return data;
	}

	private class DownloadTask extends AsyncTask<String, Integer, String> {
		String data = null;

		@Override
		protected String doInBackground(String... url) {
			// TODO Auto-generated method stub
			try {
				data = downloadUrl(url[0]);
				Log.d("url", data);
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {

			// The parsing of the xml data is done in a non-ui thread
			ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();

			// Start parsing xml data
			listViewLoaderTask.execute(result);

		}

	}

	//
	private class ListViewLoaderTask extends
			AsyncTask<String, Void, SimpleAdapter> {
		JSONObject jObject;

		// Doing the parsing of xml data in a non-ui thread
		@Override
		protected SimpleAdapter doInBackground(String... strJson) {
			try {
				jObject = new JSONObject(strJson[0]);
				WEBS_Board_NOTICE_JSONPARSER testJsonParser = new WEBS_Board_NOTICE_JSONPARSER();
				testJsonParser.parse(jObject);
				Log.i("suss", "here");
			} catch (Exception e) {
				Log.d("JSON Exception1", e.toString());
			}

			// Instantiating json parser class
			WEBS_Board_NOTICE_JSONPARSER testJsonParser = new WEBS_Board_NOTICE_JSONPARSER();
			// A list object to store the parsed countries list
			List<HashMap<String, Object>> testjson = null;
			try {
				// Getting the parsed data as a List construct
				testjson = testJsonParser.parse(jObject);
				Log.i("here4", "success");
			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}

			// Keys used in Hashmap
			String[] from = { "notice", "id" };

			// Ids of views in listview_layout
			int[] to = { R.id.webs_board_notice_list };

			// Instantiating an adapter to store each items
			// R.layout.listview_layout defines the layout of each item
			Collections.reverse(testjson);
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(),
					testjson, R.layout.webs_board_notice_list_item, from, to);
			
			return adapter;
		}

		@Override
		protected void onPostExecute(SimpleAdapter adapter) {
			// TODO Auto-generated method stub
			lv.setAdapter(adapter);
			for (int i = 0; i < adapter.getCount(); i++) {
				HashMap<String, Object> hm = (HashMap<String, Object>) adapter
						.getItem(i);
				HashMap<String, Object> hmDownload = new HashMap<String, Object>();
			}
			
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

}
