package com.huddly.main;

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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huddly.adapter.SportEventSearchQueryAdapter;
import com.huddly.model.SportEvent;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class SearchDetailsFragmentActivity extends Fragment implements
		OnClickListener, OnItemClickListener {

	TextView tvSportsName, tvRadius;
	GridView gView;
	Menu menu;
	View view;
	ImageButton btnCancel;
	double _latitude, _longitude;
	String sportsName;
	ParseGeoPoint geopoint;
	SportEventSearchQueryAdapter queryAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.activity_search_details_fragment,
				container, false);
		setFields();
		getLocation();
		return view;

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
				Log.e("LATITUDE", "" + _latitude);
				Log.e("LONGITUDE", "" + _longitude);
				startQuery();
			}
		}

	}

	public void setFields() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		sportsName = getArguments().getString("SPORTNAME");
		tvSportsName = (TextView) view.findViewById(R.id.tvSportsName);
		tvSportsName.setText(sportsName);
		tvRadius = (TextView) view.findViewById(R.id.tvRadius);
		tvRadius.setText("within " + currentUser.getString("radius") + " miles");
		gView = (GridView) view.findViewById(R.id.gridViewSearch);
		gView.setOnItemClickListener(this);
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

	}

	public void startQuery() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		geopoint = new ParseGeoPoint(_latitude, _longitude);
		queryAdapter = new SportEventSearchQueryAdapter(getActivity(),
				geopoint, sportsName, Integer.valueOf(currentUser
						.getString("radius")));
		queryAdapter.loadObjects();
		gView.setAdapter(queryAdapter);
	}

	public void changeFragment() {
		Fragment fragment = new SearchFragmentActivity();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_left_in,
				R.anim.cell_right_out);
		transaction.replace(R.id.searchDetailsFragment, fragment);
		transaction.commit();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnCancel:
			changeFragment();
			break;

		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SportEvent mEvent = queryAdapter.getItem(position);
		Bundle bundle = new Bundle();
		bundle.putSerializable("SPORTEVENT", mEvent);
		Fragment fragment = new SearchDetailsSecondaryFragmentActivity();
		fragment.setArguments(bundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_left_in,
				R.anim.cell_right_out);
		transaction.replace(R.id.searchDetailsFragment, fragment);
		transaction.commit();
	}

}
