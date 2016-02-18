package com.huddly.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.huddly.adapter.SportEventCommentAdapter;
import com.huddly.model.Comment;
import com.huddly.model.SportEvent;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CommentFragmentActivity extends Fragment implements
		OnClickListener {

	View view;
	ImageButton btnCancel, btnComment;
	EditText etComment;
	SportEvent mEvent;
	ParseUser currentUser;
	SportEventCommentAdapter queryAdapter;
	ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.activity_comment_fragment, container,
				false);

		setFields();

		return view;
	}

	public void setFields() {
		currentUser = ParseUser.getCurrentUser();
		mEvent = (SportEvent) getArguments().getSerializable("SPORTEVENT");
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnComment = (ImageButton) view.findViewById(R.id.btnComment);
		btnComment.setOnClickListener(this);
		listView = (ListView) view.findViewById(R.id.listViewComment);

		queryAdapter = new SportEventCommentAdapter(getActivity(), mEvent);
		queryAdapter.loadObjects();
		listView.setAdapter(queryAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btnCancel:
			changeFragment();
			break;

		case R.id.btnComment:
			final ParseUser currentUser = ParseUser.getCurrentUser();
			LayoutInflater li = LayoutInflater.from(getActivity());
			View dialogView = li.inflate(R.layout.comment_dialog_view, null);
			etComment = (EditText) dialogView.findViewById(R.id.etComment);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(dialogView);
			builder.setCancelable(true);
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							String commentString = etComment.getText()
									.toString().trim();
							final Comment comment = new Comment();
							comment.setComment(commentString);
							comment.setUser(currentUser);
							comment.setUsername(currentUser.getUsername());
							comment.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException arg0) {
									// TODO Auto-generated method stub

									ParseRelation<Comment> relation = mEvent
											.getRelation("comments");
									relation.add(comment);
									mEvent.saveInBackground(new SaveCallback() {

										@Override
										public void done(ParseException arg0) {
											// TODO Auto-generated method stub
											queryAdapter.notifyDataSetChanged();
											queryAdapter.loadObjects();
										}
									});

								}
							});
							dialog.cancel();
						}
					});
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			break;

		}

	}

	public void changeFragment() {

		Fragment fragment = new AttendingFragmentActivity();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.commentFragment, fragment);
		transaction.commit();
	}

}
