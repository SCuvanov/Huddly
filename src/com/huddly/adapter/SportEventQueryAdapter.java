package com.huddly.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huddly.main.R;
import com.huddly.model.SportEvent;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class SportEventQueryAdapter extends ParseQueryAdapter<SportEvent> {

	// modify to show results based on relation. User should only see what
	// events he is associated with.

	public SportEventQueryAdapter(Context context, final ParseUser currentUser) {
		// Use the QueryFactory to construct a PQA that will only show
		// Todos marked as high-pri

		super(context, new ParseQueryAdapter.QueryFactory<SportEvent>() {
			public ParseQuery<SportEvent> create() {
//				ParseQuery<SportEvent> query = new ParseQuery<SportEvent>(
//						"SportEvent");
				ParseRelation relation = currentUser.getRelation("events");
				ParseQuery query = relation.getQuery();
				//query.whereEqualTo("user", ParseUser.getCurrentUser());
				query.orderByAscending("createdAt");
				return query;
			}
		});
	}

	@Override
	public View getItemView(SportEvent event, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.gridview_detail, null);
		}

		super.getItemView(event, v, parent);

		// Add and download the image
		ParseImageView todoImage = (ParseImageView) v
				.findViewById(R.id.ivMap);
		ParseFile imageFile = event.getParseFile("photo");
		if (imageFile != null) {
			todoImage.setParseFile(imageFile);
			todoImage.loadInBackground();
		}

		// Add the title view
		TextView typeTextView = (TextView) v.findViewById(R.id.tvSportType);
		typeTextView.setText(event.getSport());
		TextView titleTextView = (TextView) v.findViewById(R.id.tvTitle);
		titleTextView.setText(event.getTitle());
		// Add the desc view
		TextView dateTextView = (TextView) v.findViewById(R.id.tvDate);
		dateTextView.setText(event.getDate());

		return v;

	}

}
