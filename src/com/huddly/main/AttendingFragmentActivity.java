package com.huddly.main;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

import com.huddly.adapter.SportEventQueryAdapter;
import com.huddly.model.SportEvent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class AttendingFragmentActivity extends Fragment implements
		OnClickListener, OnItemLongClickListener, OnItemClickListener {

	ImageButton btnCreate, btnUser;
	GridView gView;
	SportEventQueryAdapter queryAdapter;
	Menu menu;
	ParseUser mUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.activity_attending_fragment,
				container, false);
		gView = (GridView) view.findViewById(R.id.gridViewAttending);
		mUser = ParseUser.getCurrentUser();

		btnCreate = (ImageButton) view.findViewById(R.id.btnCreate);
		btnCreate.setOnClickListener(this);
		btnUser = (ImageButton) view.findViewById(R.id.btnUserProfile);
		btnUser.setOnClickListener(this);

		// SportEventQuery
		queryAdapter = new SportEventQueryAdapter(getActivity(), mUser);
		queryAdapter.loadObjects();

		gView.setAdapter(queryAdapter);
		gView.setOnItemLongClickListener(this);
		gView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btnCreate:
			changeFragment();
			break;

		case R.id.btnUserProfile:
			changeFragmentUserProfile();
			break;

		}
	}

	public void changeFragment() {

		Fragment fragment = new CreateSportEventFragmentActivity();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.attendingFragment, fragment);

		transaction.commit();
	}

	public void changeFragmentUserProfile() {
		Fragment fragment = new UserProfileFragmentActivity();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.attendingFragment, fragment);

		transaction.commit();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		// TODO Auto-generated method stub
		final SportEvent mEvent = queryAdapter.getItem(position);

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("objectId", mEvent.getUser().getObjectId());
		query.findInBackground(new FindCallback<ParseUser>() {
			public void done(List<ParseUser> objects, ParseException e) {
				if (e == null) {
					// The query was successful.
					if (mUser.getObjectId()
							.equals(objects.get(0).getObjectId())) {
						buildDialog(mEvent);
					} else {
						buildRelationDialog(mEvent);
					}
				} else {
					// Something went wrong.
				}
			}
		});
		return false;
	}

	public void buildDialog(final SportEvent currentEvent) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		View dialogView = li.inflate(R.layout.dialog_view, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(dialogView);
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				currentEvent.deleteInBackground();
				queryAdapter.notifyDataSetChanged();
				queryAdapter.loadObjects();
				dialog.cancel();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void buildRelationDialog(final SportEvent currentEvent) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		View dialogView = li.inflate(R.layout.relation_dialog_view, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(dialogView);
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				ParseRelation<SportEvent> relation = mUser
						.getRelation("events");
				relation.remove(currentEvent);

				// remove the relation from the sportevent
				ParseRelation<ParseUser> userRelation = currentEvent
						.getRelation("users");
				userRelation.remove(mUser);

				mUser.saveInBackground();
				currentEvent.saveInBackground();
				queryAdapter.notifyDataSetChanged();
				queryAdapter.loadObjects();
				dialog.cancel();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		SportEvent mEvent = queryAdapter.getItem(position);
		Bundle bundle = new Bundle();
		bundle.putSerializable("SPORTEVENT", mEvent);
		Fragment fragment = new AttendingDetailsFragmentActivity();
		fragment.setArguments(bundle);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.attendingFragment, fragment);
		transaction.commit();

	}
}
