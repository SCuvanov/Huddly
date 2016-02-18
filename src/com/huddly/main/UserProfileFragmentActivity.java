package com.huddly.main;

import java.io.ByteArrayOutputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huddly.model.SportEventPhoto;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class UserProfileFragmentActivity extends Fragment implements
		OnClickListener {
	View view;
	Button btnLogOut, btnChangePassword;
	ImageButton btnAccept, btnCancel;
	EditText etMiles, etPass1, etPass2;
	ParseImageView ivProfilePic;
	TextView tvUsername;

	final int PHOTO_WIDTH = 300;
	final int PHOTO_HEIGHT = 300;
	private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 200;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.activity_user_profile_fragment,
				container, false);

		setFields();
		return view;
	}

	public void setFields() {
		ParseUser currentUser = ParseUser.getCurrentUser();

		tvUsername = (TextView) view.findViewById(R.id.tvUsername);
		tvUsername.setText(currentUser.getUsername());
		etMiles = (EditText) view.findViewById(R.id.etMiles);
		etMiles.setText(currentUser.getString("radius"));
		etPass1 = (EditText) view.findViewById(R.id.etPassword);
		etPass2 = (EditText) view.findViewById(R.id.etPassword1);
		btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
		btnLogOut.setOnClickListener(this);
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnAccept = (ImageButton) view.findViewById(R.id.btnAccept);
		btnAccept.setOnClickListener(this);
		btnChangePassword = (Button) view.findViewById(R.id.btnChangePassword);
		btnChangePassword.setOnClickListener(this);

		ivProfilePic = (ParseImageView) view.findViewById(R.id.ivProfilePic);
		ivProfilePic.setOnClickListener(this);

		ParseFile imageFile = currentUser.getParseFile("photo");
		if (imageFile != null) {
			ivProfilePic.setParseFile(imageFile);
			ivProfilePic.loadInBackground();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnLogOut:
			ParseUser.logOut();
			showLoginActivity();
			break;

		case R.id.btnCancel:
			changeFragment();
			break;
		case R.id.btnAccept:
			ParseUser currentUser = ParseUser.getCurrentUser();
			String miles = etMiles.getText().toString().trim();
			currentUser.put("radius", miles);
			currentUser.saveInBackground(new SaveCallback() {
				public void done(ParseException e) {
					if (e != null) {

					} else {
						Toast.makeText(getActivity(), "Profile Updated!",
								Toast.LENGTH_SHORT).show();
						changeFragment();
					}
				}
			});
			break;
		case R.id.btnChangePassword:
			buildDialog();
			break;

		case R.id.ivProfilePic:
			choosePicture();
			break;
		}

	}

	public void choosePicture() {
		Intent cropIntent = new Intent();
		cropIntent.setType("image/*");
		cropIntent.setAction(Intent.ACTION_PICK);
		cropIntent.putExtra("crop", "true");
		cropIntent.putExtra("scale", true);
		cropIntent.putExtra("outputX", PHOTO_WIDTH);
		cropIntent.putExtra("outputY", PHOTO_HEIGHT);
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		cropIntent.putExtra("return-data", true);
		startActivityForResult(cropIntent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {

			if (resultCode == getActivity().RESULT_OK) {
				Log.e("SELECTIMAGE", "OK");
				final ParseUser currentUser = ParseUser.getCurrentUser();
				Bundle extras = data.getExtras();
				Bitmap bitmap = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] bmp_data = stream.toByteArray();

				final ParseFile profilePicture = new ParseFile(
						"profilePicture", bmp_data);

				profilePicture.saveInBackground(new SaveCallback() {
					public void done(ParseException e) {
						if (e != null) {

						} else {

							// check to see if ParseFile exists, if it does
							// delete it and set new ParseFile
							currentUser.remove("photo");
							currentUser.put("photo", profilePicture);
							currentUser.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException arg0) {
									// TODO Auto-generated method stub
									ParseFile imageFile = currentUser
											.getParseFile("photo");
									if (imageFile != null) {
										ivProfilePic.setParseFile(imageFile);
										ivProfilePic.loadInBackground();
									}

								}
							});

						}
					}
				});

			} else if (resultCode == getActivity().RESULT_CANCELED) {
				Log.e("SELECTIMAGE", "CANCELED");
			}
		}
	}

	private void showLoginActivity() {
		Intent intent = new Intent(getActivity(), MainActivity.class);
		startActivity(intent);
	}

	public void changeFragment() {
		Fragment fragment = new AttendingFragmentActivity();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.cell_right_in,
				R.anim.cell_left_out);
		transaction.replace(R.id.userFragment, fragment);
		transaction.commit();
	}

	public void buildDialog() {
		final ParseUser currentUser = ParseUser.getCurrentUser();

		LayoutInflater li = LayoutInflater.from(getActivity());
		View dialogView = li
				.inflate(R.layout.change_password_dialog_view, null);
		etPass1 = (EditText) dialogView.findViewById(R.id.etPassword);
		etPass2 = (EditText) dialogView.findViewById(R.id.etPassword1);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(dialogView);
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String pass1 = etPass1.getText().toString().trim();
				String pass2 = etPass2.getText().toString().trim();

				if (!pass1.equals("") && !pass2.equals("")
						&& isMatching(pass1, pass2)) {
					currentUser.put("password", pass1);
					currentUser.saveInBackground(new SaveCallback() {
						public void done(ParseException e) {
							if (e != null) {
								Toast.makeText(getActivity(),
										"Password Changed!", Toast.LENGTH_SHORT)
										.show();
							}
						}
					});
				} else {
					Toast.makeText(getActivity(), "Passwords do not match!",
							Toast.LENGTH_SHORT).show();
				}
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

	private boolean isMatching(String pass1, String pass2) {
		if (pass1.equals(pass2)) {
			return true;
		} else {
			return false;
		}
	}
}
