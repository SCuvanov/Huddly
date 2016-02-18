package com.huddly.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huddly.main.R;
import com.huddly.model.Comment;
import com.huddly.model.SportEvent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class SportEventCommentAdapter extends ParseQueryAdapter<Comment> {

	public SportEventCommentAdapter(Context context,
			final SportEvent currentEvent) {
		// Use the QueryFactory to construct a PQA that will only show
		// Todos marked as high-pri

		super(context, new ParseQueryAdapter.QueryFactory<Comment>() {
			public ParseQuery<Comment> create() {
				// ParseQuery<SportEvent> query = new ParseQuery<SportEvent>(
				// "SportEvent");
				ParseRelation relation = currentEvent.getRelation("comments");
				ParseQuery query = relation.getQuery();
				query.orderByAscending("createdAt");
				return query;
			}
		});
	}

	@Override
	public View getItemView(final Comment comment, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.comment_list_item, null);
		}

		super.getItemView(comment, v, parent);

		final ParseImageView todoImage = (ParseImageView) v
				.findViewById(R.id.ivProfilePic);
		final TextView tvUsername = (TextView) v.findViewById(R.id.tvUsername);
		final TextView tvComment = (TextView) v.findViewById(R.id.tvComment);

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("objectId", comment.getUser().getObjectId());
		query.findInBackground(new FindCallback<ParseUser>() {
			public void done(List<ParseUser> objects, ParseException e) {
				if (e == null) {
					// The query was successful.
					ParseUser mUser = objects.get(0);

					ParseFile imageFile = mUser.getParseFile("photo");
					if (imageFile != null) {
						todoImage.setParseFile(imageFile);
						todoImage.loadInBackground();
					}

					tvUsername.setText(comment.getUsername());
					tvComment.setText(comment.getComment());

				} else {
					// Something went wrong.
				}
			}
		});

		return v;

	}

}
