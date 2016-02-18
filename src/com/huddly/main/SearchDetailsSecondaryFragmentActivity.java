package com.huddly.main;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huddly.model.SportEvent;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SearchDetailsSecondaryFragmentActivity extends Fragment implements
		OnClickListener {
	View view;
	SportEvent mEvent;
	ImageButton btnCancel, btnFollow;
	TextView tvTitle, tvDesc, tvTime, tvDate, tvPeople, tvSportName;
	ParseImageView todoImage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(
				R.layout.activity_search_details_secondary_fragment, container,
				false);
		setFields();
		return view;
	}

	public void setFields() {
		mEvent = (SportEvent) getArguments().getSerializable("SPORTEVENT");
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnFollow = (ImageButton) view.findViewById(R.id.btnFollow);
		btnFollow.setOnClickListener(this);
		tvTitle = (TextView) view.findViewById(R.id.tvSportsTitle);
		tvDesc = (TextView) view.findViewById(R.id.tvSportsDescription);
		tvTime = (TextView) view.findViewById(R.id.tvTime);
		tvDate = (TextView) view.findViewById(R.id.tvDate);
		tvPeople = (TextView) view.findViewById(R.id.tvPeople);
		tvSportName = (TextView) view.findViewById(R.id.tvSportName);
		todoImage = (ParseImageView) view.findViewById(R.id.ivMap);
		todoImage.setOnClickListener(this);

		// Set fields
		tvTitle.setText(mEvent.getTitle());
		tvDesc.setText(mEvent.getDesc());
		tvTime.setText(mEvent.getTime());
		tvDate.setText(mEvent.getDate());
		tvSportName.setText(mEvent.getSport());

		ParseRelation relation = mEvent.getRelation("users");
		ParseQuery query = relation.getQuery();
		query.countInBackground(new CountCallback() {
			public void done(int count, ParseException e) {
				if (e == null) {
					tvPeople.setText(String.valueOf(count));
				} else {
					// The request failed
				}
			}
		});

		ParseFile imageFile = mEvent.getParseFile("photo");
		if (imageFile != null) {
			todoImage.setParseFile(imageFile);
			todoImage.loadInBackground();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btnCancel:
			changeFragment();
			break;
		case R.id.btnFollow:
			Drawable img = getResources().getDrawable(
					R.drawable.ic_notification_event_available_green);
			btnFollow.setImageDrawable(img);
			final ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
				{
					ParseRelation<SportEvent> relation = currentUser
							.getRelation("events");
					relation.add(mEvent);
					currentUser.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							ParseRelation<ParseUser> userRelation = mEvent
									.getRelation("users");
							userRelation.add(currentUser);
							mEvent.saveInBackground();
							changeFragment();
						}
					});

				}
			}
			break;

		case R.id.ivMap:
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?daddr="
							+ mEvent.getLatitude() + ","
							+ mEvent.getLongitude()));
			startActivity(intent);
			break;
		}

	}

	public void changeFragment() {
		Fragment fragment = new SearchFragmentActivity();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_left_in,
				R.anim.cell_right_out);
		transaction.replace(R.id.searchDetailsSecondaryFragment, fragment);
		transaction.commit();
	}

}
