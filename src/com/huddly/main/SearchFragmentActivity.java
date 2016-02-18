package com.huddly.main;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.huddly.adapter.SportEventSearchQueryAdapter;
import com.huddly.adapter.SportSpinnerAdapter;
import com.huddly.model.Sport;
import com.huddly.model.SportEvent;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class SearchFragmentActivity extends Fragment implements
		OnClickListener, OnItemSelectedListener, OnItemClickListener {

	Menu menu;
	View view;
	Spinner spinnerSport;
	SportSpinnerAdapter sportSpinnerAdapter;
	GridView gView;
	TextView tvRadius;
	String sportsName;
	double _latitude, _longitude;
	ParseGeoPoint geopoint;
	SportEventSearchQueryAdapter queryAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.activity_search_fragment, container,
				false);
		setFields();
		getLocation();

		return view;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		SportEvent mEvent = queryAdapter.getItem(position);
		Bundle bundle = new Bundle();
		bundle.putSerializable("SPORTEVENT", mEvent);
		Fragment fragment = new SearchDetailsSecondaryFragmentActivity();
		fragment.setArguments(bundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_left_in,
				R.anim.cell_right_out);
		transaction.replace(R.id.searchFragment, fragment);
		transaction.commit();

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// An item was selected. You can retrieve the selected item using
		Sport sport = (Sport) spinnerSport.getItemAtPosition(pos);
		sportsName = sport.getSportName();
		Log.e("SPORT NAME", sport.getSportName());
		startQuery(sport.getSportName());

	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	public void setFields() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		tvRadius = (TextView) view.findViewById(R.id.tvRadius);
		gView = (GridView) view.findViewById(R.id.gridViewSearch);
		gView.setOnItemClickListener(this);
		spinnerSport = (Spinner) view.findViewById(R.id.spinnerSport);
		sportSpinnerAdapter = new SportSpinnerAdapter(getActivity(),
				R.layout.sport_list_item, populateSportArray());
		spinnerSport.setAdapter(sportSpinnerAdapter);
		spinnerSport.setOnItemSelectedListener(this);

		tvRadius = (TextView) view.findViewById(R.id.tvRadius);
		tvRadius.setText("within " + currentUser.getString("radius") + " miles");
		spinnerSport.setSelection(0);
	}

	public ArrayList<Sport> populateSportArray() {
		final ArrayList<Sport> sportArray = new ArrayList<Sport>();
		sportArray.add(new Sport("Archery", R.drawable.archery));
		sportArray.add(new Sport("Badminton", R.drawable.badminton));
		sportArray.add(new Sport("Baseball", R.drawable.baseball));
		sportArray.add(new Sport("Basketball", R.drawable.basketball));
		sportArray.add(new Sport("Boomerang", R.drawable.boomerang));
		sportArray.add(new Sport("Bowling", R.drawable.bowling));
		sportArray.add(new Sport("Boxing", R.drawable.boxing));
		sportArray.add(new Sport("Canoeing", R.drawable.canoeing));
		sportArray.add(new Sport("Cycling", R.drawable.cycling));
		sportArray.add(new Sport("Darts", R.drawable.darts));
		sportArray.add(new Sport("Fishing", R.drawable.fishing));
		sportArray.add(new Sport("Football", R.drawable.football));
		sportArray.add(new Sport("Golf", R.drawable.golf));
		sportArray.add(new Sport("Hiking", R.drawable.hiking));
		sportArray.add(new Sport("Hockey", R.drawable.hockey));
		sportArray.add(new Sport("Hunting", R.drawable.hunting));
		sportArray.add(new Sport("Ice Skating", R.drawable.iceskating));
		sportArray.add(new Sport("Karate", R.drawable.karate));
		sportArray.add(new Sport("Paintball", R.drawable.paintball));
		sportArray.add(new Sport("Rock Climbing", R.drawable.rockclimbing));
		sportArray.add(new Sport("Roller Skating", R.drawable.rollerskating));
		sportArray.add(new Sport("Running", R.drawable.running));
		sportArray.add(new Sport("Skateboarding", R.drawable.skateboarding));
		sportArray.add(new Sport("Skiing", R.drawable.skiing));
		sportArray.add(new Sport("Snowboarding", R.drawable.snowboarding));
		sportArray.add(new Sport("Soccer", R.drawable.soccer));
		sportArray.add(new Sport("Surfing", R.drawable.surfing));
		sportArray.add(new Sport("Swimming", R.drawable.swimming));
		sportArray.add(new Sport("Tennis", R.drawable.tennis));
		sportArray.add(new Sport("Tug of War", R.drawable.tugofwar));
		sportArray.add(new Sport("Volleyball", R.drawable.volleyball));
		sportArray.add(new Sport("Weight Lifting", R.drawable.weightlifting));
		sportArray.add(new Sport("Yoga", R.drawable.yoga));

		return sportArray;

	}

	public void getLocation() {
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
			}
		}

	}

	public void startQuery(String sportsName) {
		ParseUser currentUser = ParseUser.getCurrentUser();
		geopoint = new ParseGeoPoint(_latitude, _longitude);
		queryAdapter = new SportEventSearchQueryAdapter(getActivity(),
				geopoint, sportsName, Integer.valueOf(currentUser
						.getString("radius")));
		queryAdapter.loadObjects();
		gView.setAdapter(queryAdapter);
	}

}
