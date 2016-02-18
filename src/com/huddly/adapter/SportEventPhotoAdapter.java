package com.huddly.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huddly.main.R;
import com.huddly.model.SportEvent;
import com.huddly.model.SportEventPhoto;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;

public class SportEventPhotoAdapter extends ParseQueryAdapter<SportEventPhoto> {

	public SportEventPhotoAdapter(Context context, final SportEvent sportEvent) {
		// Use the QueryFactory to construct a PQA that will only show
		// Todos marked as high-pri

		super(context, new ParseQueryAdapter.QueryFactory<SportEventPhoto>() {
			public ParseQuery<SportEventPhoto> create() {
				// ParseQuery<SportEvent> query = new ParseQuery<SportEvent>(
				// "SportEvent");
				ParseRelation relation = sportEvent.getRelation("event_photos");
				ParseQuery query = relation.getQuery();
				// query.whereEqualTo("user", ParseUser.getCurrentUser());
				query.orderByAscending("createdAt");
				return query;
			}
		});
	}

	@Override
	public View getItemView(SportEventPhoto photo, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.grid_view_picture, null);
		}

		super.getItemView(photo, v, parent);

		// Add and download the image
		ParseImageView todoImage = (ParseImageView) v
				.findViewById(R.id.ivPhoto);
		ParseFile imageFile = photo.getParseFile("photo");
		if (imageFile != null) {
			todoImage.setParseFile(imageFile);
			todoImage.loadInBackground();
		}

		return v;

	}

}
