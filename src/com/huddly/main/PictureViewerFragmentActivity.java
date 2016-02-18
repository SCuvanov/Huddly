package com.huddly.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import com.huddly.adapter.SportEventPhotoAdapter;
import com.huddly.model.SportEvent;

public class PictureViewerFragmentActivity extends Fragment implements
		OnClickListener {
	View view;
	GridView gView;
	SportEvent mEvent;
	SportEventPhotoAdapter photoAdapter;
	ImageButton btnCancel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.activity_picture_viewer_fragment,
				container, false);
		setFields();
		return view;
	}

	public void setFields() {
		mEvent = (SportEvent) getArguments().getSerializable("SPORTEVENT");
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		photoAdapter = new SportEventPhotoAdapter(getActivity(), mEvent);
		photoAdapter.loadObjects();
		gView = (GridView) view.findViewById(R.id.gridViewPictures);
		gView.setAdapter(photoAdapter);
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

	public void changeFragment() {
		Fragment fragment = new AttendingFragmentActivity();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.pictureViewerFragment, fragment);
		transaction.commit();
	}

}
