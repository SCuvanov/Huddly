package com.huddly.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huddly.model.SportEvent;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CreateSportEventFragmentActivity extends Fragment implements
		OnItemClickListener, OnClickListener {

	private static final String LOG_TAG = "Huddly";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String GEOCODING_API_BASE = "https://maps.googleapis.com/maps/api/geocode";
	private static final String STATIC_MAPS_API_BASE = "https://maps.googleapis.com/maps/api/staticmap";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyACTjb2zden0UJZOq5hUxvjl9pw0vGv45M";

	// /////////////////////////////////////////////////////////////////////////////

	ImageButton btnArchery, btnBadminton, btnBaseball, btnBasketball,
			btnBoomerang, btnBowling, btnBoxing, btnCanoeing, btnCycling,
			btnDarts, btnFishing, btnFootball, btnGolf, btnHiking, btnHockey,
			btnHunting, btnIceSkating, btnKarate, btnPaintball,
			btnRockClimbing, btnRollerSkating, btnRunning, btnSkateboarding,
			btnSkiing, btnSnowboarding, btnSoccer, btnSurfing, btnSwimming,
			btnTennis, btnTugOfWar, btnVolleyball, btnWeightLifting, btnYoga;
	ImageButton btnAccept, btnCancel;
	AutoCompleteTextView tvAutoComplete;
	TextView tvSportName;
	EditText etLocation, etDesc, etTitle;
	Button btnTime, btnDate;
	ImageView ivLocation;
	Menu menu;
	View view;
	private Dialog progressDialog;

	public static String desc, loc, time, date, title, sport, locationString,
			longString, latString;
	double _latitude, _longitude;

	public int numOfTasks = 0;
	byte[] data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.activity_create_sport_event_fragment,
				container, false);
		ParseUser mUser = ParseUser.getCurrentUser();

		setFields();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		return view;

	}

	public void changeFragment() {
		Fragment fragment = new AttendingFragmentActivity();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.createEventFragment, fragment);
		transaction.commit();
	}

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		Log.e("LOCATION FROM ADAPTER", str);
		getAddressCoord(str);
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}

	public void getAddressCoord(String str) {

		Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocationName(str, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Address address = addresses.get(0);
		_latitude = address.getLatitude();
		_longitude = address.getLongitude();
		Log.e("LATITUDE----", "" + address.getLatitude());
		Log.e("LONGITUDE----", "" + address.getLongitude());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btnArchery:
			tvSportName.setText("Archery");
			break;
		case R.id.btnBadminton:
			tvSportName.setText("Badminton");
			break;
		case R.id.btnBaseball:
			tvSportName.setText("Baseball");
			break;
		case R.id.btnBasketball:
			tvSportName.setText("Basketball");
			break;
		case R.id.btnBoomerang:
			tvSportName.setText("Boomerang");
			break;
		case R.id.btnBowling:
			tvSportName.setText("Bowling");
			break;
		case R.id.btnBoxing:
			tvSportName.setText("Boxing");
			break;
		case R.id.btnCanoeing:
			tvSportName.setText("Canoeing");
			break;
		case R.id.btnCycling:
			tvSportName.setText("Cycling");
			break;
		case R.id.btnDarts:
			tvSportName.setText("Darts");
			break;
		case R.id.btnFishing:
			tvSportName.setText("Fishing");
			break;
		case R.id.btnFootball:
			tvSportName.setText("Football");
			break;
		case R.id.btnGolf:
			tvSportName.setText("Golf");
			break;
		case R.id.btnHiking:
			tvSportName.setText("Hiking");
			break;
		case R.id.btnHockey:
			tvSportName.setText("Hockey");
			break;
		case R.id.btnHunting:
			tvSportName.setText("Hunting");
			break;
		case R.id.btnIceSkating:
			tvSportName.setText("Ice Skating");
			break;
		case R.id.btnKarate:
			tvSportName.setText("Karate");
			break;
		case R.id.btnPaintball:
			tvSportName.setText("Paintball");
			break;
		case R.id.btnRockClimbing:
			tvSportName.setText("Rock Climbing");
			break;
		case R.id.btnRollerSkating:
			tvSportName.setText("Roller Skating");
			break;
		case R.id.btnRunning:
			tvSportName.setText("Running");
			break;
		case R.id.btnSkateboarding:
			tvSportName.setText("Skateboarding");
			break;
		case R.id.btnSkiing:
			tvSportName.setText("Skiing");
			break;
		case R.id.btnSnowboarding:
			tvSportName.setText("Snowboarding");
			break;
		case R.id.btnSoccer:
			tvSportName.setText("Soccer");
			break;
		case R.id.btnSurfing:
			tvSportName.setText("Surfing");
			break;
		case R.id.btnSwimming:
			tvSportName.setText("Swimming");
			break;
		case R.id.btnTennis:
			tvSportName.setText("Tennis");
			break;
		case R.id.btnTugOfWar:
			tvSportName.setText("Tug of War");
			break;
		case R.id.btnVolleyball:
			tvSportName.setText("Volleyball");
			break;
		case R.id.btnWeightLifting:
			tvSportName.setText("Weight Lifting");
			break;
		case R.id.btnYoga:
			tvSportName.setText("Yoga");
			break;

		case R.id.btnAccept:
			loc = _latitude + " " + _longitude;
			desc = etDesc.getText().toString().trim();
			time = btnTime.getText().toString().trim();
			date = btnDate.getText().toString().trim();
			sport = tvSportName.getText().toString().trim();
			title = etTitle.getText().toString().trim();

			if (!checkForEmpty(desc) || !checkForEmpty(time)
					|| !checkForEmpty(date) || !checkForEmpty(sport)
					|| !checkForEmpty(title) || !checkForEmpty(loc)) {
				Toast.makeText(getActivity(), "Enter all fields",
						Toast.LENGTH_SHORT).show();
				Log.e("CHECKFOREMPTY", "SOMETHINGFAILED");
			} else {
				new CreateStaticMapAsyncTask().execute(loc);
			}

			break;

		case R.id.btnCancel:
			changeFragment();
			break;

		case R.id.ivLocation:
			LocationManager locManager = (LocationManager) getActivity()
					.getSystemService(Context.LOCATION_SERVICE);
			boolean network_enabled = locManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			Location location;

			if (network_enabled) {
				location = locManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null) {

					_longitude = location.getLongitude();
					_latitude = location.getLatitude();
					Log.e("LATITUDE", "" + _latitude);
					Log.e("LONGITUDE", "" + _longitude);
					etLocation.setText(_latitude + "," + _longitude);
				}
			}
			break;
		}

	}

	public void setFields() {

		tvAutoComplete = (AutoCompleteTextView) view
				.findViewById(R.id.etLocation);
		tvAutoComplete.setAdapter(new PlacesAutoCompleteAdapter(getActivity(),
				R.layout.list_item));
		tvAutoComplete.setOnItemClickListener(this);

		tvSportName = (TextView) view.findViewById(R.id.textViewSport);

		btnArchery = (ImageButton) view.findViewById(R.id.btnArchery);
		btnArchery.setOnClickListener(this);
		btnBadminton = (ImageButton) view.findViewById(R.id.btnBadminton);
		btnBadminton.setOnClickListener(this);
		btnBaseball = (ImageButton) view.findViewById(R.id.btnBaseball);
		btnBaseball.setOnClickListener(this);
		btnBasketball = (ImageButton) view.findViewById(R.id.btnBasketball);
		btnBasketball.setOnClickListener(this);
		btnBoomerang = (ImageButton) view.findViewById(R.id.btnBoomerang);
		btnBoomerang.setOnClickListener(this);
		btnBowling = (ImageButton) view.findViewById(R.id.btnBowling);
		btnBowling.setOnClickListener(this);
		btnBoxing = (ImageButton) view.findViewById(R.id.btnBoxing);
		btnBoxing.setOnClickListener(this);
		btnCanoeing = (ImageButton) view.findViewById(R.id.btnCanoeing);
		btnCanoeing.setOnClickListener(this);
		btnCycling = (ImageButton) view.findViewById(R.id.btnCycling);
		btnCycling.setOnClickListener(this);
		btnDarts = (ImageButton) view.findViewById(R.id.btnDarts);
		btnDarts.setOnClickListener(this);
		btnFishing = (ImageButton) view.findViewById(R.id.btnFishing);
		btnFishing.setOnClickListener(this);
		btnFootball = (ImageButton) view.findViewById(R.id.btnFootball);
		btnFootball.setOnClickListener(this);
		btnGolf = (ImageButton) view.findViewById(R.id.btnGolf);
		btnGolf.setOnClickListener(this);
		btnHiking = (ImageButton) view.findViewById(R.id.btnHiking);
		btnHiking.setOnClickListener(this);
		btnHockey = (ImageButton) view.findViewById(R.id.btnHockey);
		btnHockey.setOnClickListener(this);
		btnHunting = (ImageButton) view.findViewById(R.id.btnHunting);
		btnHunting.setOnClickListener(this);
		btnIceSkating = (ImageButton) view.findViewById(R.id.btnIceSkating);
		btnIceSkating.setOnClickListener(this);
		btnKarate = (ImageButton) view.findViewById(R.id.btnKarate);
		btnKarate.setOnClickListener(this);
		btnPaintball = (ImageButton) view.findViewById(R.id.btnPaintball);
		btnPaintball.setOnClickListener(this);
		btnRockClimbing = (ImageButton) view.findViewById(R.id.btnRockClimbing);
		btnRockClimbing.setOnClickListener(this);
		btnRollerSkating = (ImageButton) view
				.findViewById(R.id.btnRollerSkating);
		btnRollerSkating.setOnClickListener(this);
		btnRunning = (ImageButton) view.findViewById(R.id.btnRunning);
		btnRunning.setOnClickListener(this);
		btnSkateboarding = (ImageButton) view
				.findViewById(R.id.btnSkateboarding);
		btnSkateboarding.setOnClickListener(this);
		btnSkiing = (ImageButton) view.findViewById(R.id.btnSkiing);
		btnSkiing.setOnClickListener(this);
		btnSnowboarding = (ImageButton) view.findViewById(R.id.btnSnowboarding);
		btnSnowboarding.setOnClickListener(this);
		btnSoccer = (ImageButton) view.findViewById(R.id.btnSoccer);
		btnSoccer.setOnClickListener(this);
		btnSurfing = (ImageButton) view.findViewById(R.id.btnSurfing);
		btnSurfing.setOnClickListener(this);
		btnSwimming = (ImageButton) view.findViewById(R.id.btnSwimming);
		btnSwimming.setOnClickListener(this);
		btnTennis = (ImageButton) view.findViewById(R.id.btnTennis);
		btnTennis.setOnClickListener(this);
		btnTugOfWar = (ImageButton) view.findViewById(R.id.btnTugOfWar);
		btnTugOfWar.setOnClickListener(this);
		btnVolleyball = (ImageButton) view.findViewById(R.id.btnVolleyball);
		btnVolleyball.setOnClickListener(this);
		btnWeightLifting = (ImageButton) view
				.findViewById(R.id.btnWeightLifting);
		btnWeightLifting.setOnClickListener(this);
		btnYoga = (ImageButton) view.findViewById(R.id.btnYoga);
		btnYoga.setOnClickListener(this);

		btnAccept = (ImageButton) view.findViewById(R.id.btnAccept);
		btnAccept.setOnClickListener(this);
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		etDesc = (EditText) view.findViewById(R.id.etDescription);
		etLocation = (EditText) view.findViewById(R.id.etLocation);
		etTitle = (EditText) view.findViewById(R.id.etTitle);
		btnTime = (Button) view.findViewById(R.id.btnTime);
		btnDate = (Button) view.findViewById(R.id.btnDate);

		ivLocation = (ImageView) view.findViewById(R.id.ivLocation);
		ivLocation.setOnClickListener(this);
	}

	private void showNavigationActivity() {
		Log.e("BUTTON", "SIGN UP PRESSED");
		Intent intent = new Intent(getActivity(), NaviActivity.class);
		startActivity(intent);
	}

	public void addTask() {
		numOfTasks++;
	}

	public void removeTask() {
		numOfTasks--;
	}

	public void allTasksComplete() {
		if (numOfTasks == 0) {

			final ParseUser currentUser = ParseUser.getCurrentUser();
			ParseFile photo = new ParseFile("photo", data);
			ParseGeoPoint geopoint = new ParseGeoPoint(_latitude, _longitude);

			if (!checkForEmpty(date)) {
				Log.e("DATE IS", "WRONG WRONG WRONG");
			}

			final SportEvent newSportEvent = new SportEvent();
			newSportEvent.setSport(sport);
			newSportEvent.setTime(time);
			newSportEvent.setDate(date);
			newSportEvent.setTitle(title);
			newSportEvent.setDesc(desc);
			newSportEvent.setLatitude("" + _latitude);
			newSportEvent.setLongitude("" + _longitude);
			newSportEvent.setUser(currentUser);
			newSportEvent.setPhotoFile(photo);
			newSportEvent.setGeoPoint(geopoint);
			ParseRelation<ParseUser> userRelation = newSportEvent
					.getRelation("users");
			userRelation.add(currentUser);

			// newSportEvent.setUser(ParseUser.getCurrentUser());
			ParseACL acl = new ParseACL();
			acl.setPublicReadAccess(true);
			acl.setPublicWriteAccess(true); // objects created are writable
			newSportEvent.setACL(acl);

			// create dialog
			this.progressDialog = ProgressDialog.show(getActivity(), "",
					"Almost there, Hold your horses...", true);
			// publish to ParseDB
			newSportEvent.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					// Update the display
					ParseRelation<SportEvent> relation = currentUser
							.getRelation("events");
					relation.add(newSportEvent);

					currentUser.saveInBackground();
					progressDialog.dismiss();
					changeFragment();
				}
			});
		}

	}

	public boolean checkForEmpty(String mString) {
		if (mString.equals("") || mString.equals("Date")
				|| mString.equals("Choose Sport") || mString.equals("Time")) {
			return false;
		} else {
			return true;
		}
	}

	// class LocationAsyncTask extends AsyncTask<String, Void,
	// ArrayList<String>> {
	//
	// private static final String GEOCODING_API_BASE =
	// "https://maps.googleapis.com/maps/api/geocode";
	// private static final String OUT_JSON = "/json";
	//
	// ArrayList<String> mArray;
	//
	// Context mContext;
	//
	// @Override
	// protected void onPreExecute() {
	// addTask(); // adds one to task
	// super.onPreExecute();
	//
	// }
	//
	// @Override
	// protected ArrayList<String> doInBackground(String... params) {
	// // TODO Auto-generated method stub mArray = new ArrayList<String>();
	// StringBuilder sb = new StringBuilder(GEOCODING_API_BASE + OUT_JSON);
	// try {
	// sb.append("?address=").append(
	// URLEncoder.encode(params[0], "UTF-8"));
	// sb.append("&sensor=false");
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated
	// e.printStackTrace();
	// }
	//
	// String uri = sb.toString();
	//
	// HttpGet httpGet = new HttpGet(uri);
	// HttpClient client = new DefaultHttpClient();
	// HttpResponse response;
	// StringBuilder stringBuilder = new StringBuilder();
	//
	// try {
	// response = client.execute(httpGet);
	// HttpEntity entity = response.getEntity();
	// InputStream stream = entity.getContent();
	// int b;
	// while ((b = stream.read()) != -1) {
	// stringBuilder.append((char) b);
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// JSONObject jsonObject = new JSONObject();
	// try {
	// jsonObject = new JSONObject(stringBuilder.toString());
	//
	// double lng = ((JSONArray) jsonObject.get("results"))
	// .getJSONObject(0).getJSONObject("geometry")
	// .getJSONObject("location").getDouble("lng");
	//
	// double lat = ((JSONArray) jsonObject.get("results"))
	// .getJSONObject(0).getJSONObject("geometry")
	// .getJSONObject("location").getDouble("lat");
	//
	// Log.e("latitude", "" + lat);
	// Log.e("longitude", "" + lng);
	// String longitude = Double.toString(lng);
	// String latitude = Double.toString(lat);
	// mArray.add(longitude);
	// mArray.add(latitude);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// return mArray;
	// }
	//
	// @Override
	// protected void onPostExecute(ArrayList<String> result) {
	// super.onPostExecute(result);
	// _longitude = Double.valueOf(result.get(0));
	// _latitude = Double.valueOf(result.get(1));
	// String locationString = _latitude + " " + _longitude;
	// Log.e("LOCATION STRING", locationString);
	// removeTask();
	// new CreateStaticMapAsyncTask().execute(locationString);
	// }
	//
	// }

	class CreateStaticMapAsyncTask extends AsyncTask<String, Void, Bitmap> {

		private static final String STATIC_MAPS_API_BASE = "https://maps.googleapis.com/maps/api/staticmap";
		private static final String STATIC_MAPS_API_SIZE = "500x500";

		@Override
		protected void onPreExecute() {
			addTask(); // adds one to task count.
			super.onPreExecute();

		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			locationString = params[0];
			Bitmap bmp = null;
			StringBuilder sb = new StringBuilder(STATIC_MAPS_API_BASE);
			try {
				sb.append("?center=").append(
						URLEncoder.encode(locationString, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			sb.append("&size=" + STATIC_MAPS_API_SIZE);
			sb.append("&key=" + API_KEY);
			String url = new String(sb.toString());
			Log.e("URL", sb.toString());

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			InputStream in = null;
			try {
				in = httpclient.execute(request).getEntity().getContent();
				bmp = BitmapFactory.decodeStream(in);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bmp;

		}

		protected void onPostExecute(Bitmap bmp) {
			super.onPostExecute(bmp);
			if (bmp != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				data = stream.toByteArray();
				removeTask();
				allTasksComplete();

			}
		}
	}

}
