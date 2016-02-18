package com.huddly.async;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class LocationAsyncTask extends
		AsyncTask<String, Void, ArrayList<String>> {

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String GEOCODING_API_BASE = "https://maps.googleapis.com/maps/api/geocode";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyACTjb2zden0UJZOq5hUxvjl9pw0vGv45M";

	ArrayList<String> mArray;

	Context mContext;

	public interface LocationAsyncTaskInterface {
		void getLocation(ArrayList<String> string_array);
	}

	LocationAsyncTaskInterface callback;

	public LocationAsyncTask(LocationAsyncTaskInterface context) {
		// TODO Auto-generated constructor stub
		this.callback = context;
	}

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		// TODO Auto-generated method stub
		mArray = new ArrayList<String>();
		StringBuilder sb = new StringBuilder(GEOCODING_API_BASE + OUT_JSON);
		try {
			sb.append("?address=")
					.append(URLEncoder.encode(params[0], "UTF-8"));
			sb.append("&sensor=false");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String uri = sb.toString();

		HttpGet httpGet = new HttpGet(uri);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());

			double lng = ((JSONArray) jsonObject.get("results"))
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lng");

			double lat = ((JSONArray) jsonObject.get("results"))
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lat");

			String longitude = Double.toString(lng);
			String latitude = Double.toString(lat);
			mArray.add(longitude);
			mArray.add(latitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return mArray;
	}

	@Override
	protected void onPostExecute(ArrayList<String> result) {
		super.onPostExecute(result);
		callback.getLocation(result);

	}

}
