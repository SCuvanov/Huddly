package com.huddly.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huddly.adapter.SportEventPhotoAdapter;
import com.huddly.model.SportEvent;
import com.huddly.model.SportEventPhoto;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AttendingDetailsFragmentActivity extends Fragment implements
		OnClickListener {
	View view;
	SportEvent mEvent;
	ImageButton btnCancel, btnCamera, btnChat, btnPhoto;
	TextView tvTitle, tvDesc, tvTime, tvDate, tvPeople, tvSportName;
	final int PHOTO_WIDTH = 300;
	final int PHOTO_HEIGHT = 300;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	SportEventPhotoAdapter photoAdapter;
	ParseImageView todoImage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.activity_attending_details_fragment,
				container, false);
		setFields();
		return view;
	}

	public void setFields() {
		mEvent = (SportEvent) getArguments().getSerializable("SPORTEVENT");
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnCamera = (ImageButton) view.findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(this);
		btnChat = (ImageButton) view.findViewById(R.id.btnChat);
		btnChat.setOnClickListener(this);
		btnPhoto = (ImageButton) view.findViewById(R.id.btnPictures);
		btnPhoto.setOnClickListener(this);
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

		ParseFile imageFile = mEvent.getParseFile("photo");
		if (imageFile != null) {
			todoImage.setParseFile(imageFile);
			todoImage.loadInBackground();
		}

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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btnCancel:
			changeFragment();
			break;

		case R.id.btnCamera:
			takePicture();
			break;

		case R.id.btnPictures:
			changePictureViewerFragment();
			break;
		case R.id.btnChat:
			changeCommentFragment();
			break;

		case R.id.ivMap:
			Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?daddr="
							+ mEvent.getLatitude() + ","
							+ mEvent.getLongitude()));
			startActivity(intent1);
			break;

		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void takePicture() {

		// create Intent to take a picture and return control to the calling
		// application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

			if (resultCode == getActivity().RESULT_OK) {
				cropCapturedImage(fileUri);
				if (fileUri != null) {
				}

			} else if (resultCode == getActivity().RESULT_CANCELED) {
			}
		}

		if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == getActivity().RESULT_OK) {
				final ParseUser currentUser = ParseUser.getCurrentUser();
				Bundle extras = data.getExtras();
				Bitmap bitmap = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] bmp_data = stream.toByteArray();

				final SportEventPhoto newSportEventPhoto = new SportEventPhoto();
				ParseFile photo = new ParseFile("photo", bmp_data);
				newSportEventPhoto.setPhotoFile(photo);
				newSportEventPhoto.setUser(currentUser);
				newSportEventPhoto.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						// Update the display
						ParseRelation<SportEventPhoto> relation = mEvent
								.getRelation("event_photos");
						relation.add(newSportEventPhoto);
						mEvent.saveInBackground();
						// photoAdapter.notifyDataSetChanged();
					}
				});

			} else if (resultCode == getActivity().RESULT_CANCELED) {

			}

		}
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Huddly");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	public void cropCapturedImage(Uri picUri) {

		// call the standard crop action intent
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		// indicate image type and Uri of image
		cropIntent.setDataAndType(picUri, "image/*");
		// set crop properties
		cropIntent.putExtra("crop", "true");
		// indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		// indicate output X and Y
		cropIntent.putExtra("outputX", PHOTO_WIDTH);
		cropIntent.putExtra("outputY", PHOTO_HEIGHT);
		// retrieve data on return
		cropIntent.putExtra("return-data", true);
		// start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, CROP_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	public void changeFragment() {
		Fragment fragment = new AttendingFragmentActivity();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.attendingDetailsFragment, fragment);
		transaction.commit();
	}

	public void changePictureViewerFragment() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("SPORTEVENT", mEvent);
		Fragment fragment = new PictureViewerFragmentActivity();
		FragmentManager fm = getFragmentManager();
		fragment.setArguments(bundle);
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.attendingDetailsFragment, fragment);
		transaction.commit();

	}

	public void changeCommentFragment() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("SPORTEVENT", mEvent);
		Fragment fragment = new CommentFragmentActivity();
		FragmentManager fm = getFragmentManager();
		fragment.setArguments(bundle);
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.attendingDetailsFragment, fragment);
		transaction.commit();
	}

}