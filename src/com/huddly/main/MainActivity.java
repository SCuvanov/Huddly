package com.huddly.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends Activity implements OnClickListener {

	Button btn_signup, btn_signin;
	EditText etUsername, etPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		btn_signup = (Button) findViewById(R.id.btnSignUp);
		btn_signup.setOnClickListener(this);

		btn_signin = (Button) findViewById(R.id.btnSignIn);
		btn_signin.setOnClickListener(this);

		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		final ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null)) {

			// you can add this line &&
			// ParseFacebookUtils.isLinked(currentUser)) to if statement
			// if you want to make sure the user is also linked to facebook
			// account
			// Go to the user info activity
			showNavigationActivity();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.btnSignUp:
			showSignUpActivity();
			break;

		case R.id.btnSignIn:

			// Retrieve User Info
			// Log In via Parse
			// Switch to NavigationActivity
			String username = etUsername.getText().toString().trim();
			String password = etPassword.getText().toString().trim();

			try {
				if (username.equals("") || password.equals("")) {
					if (username.isEmpty()) {
						etUsername.setHintTextColor(getResources().getColor(
								R.color.blue));
						etUsername.setHint("Enter Username");
					}
					if (password.isEmpty()) {
						etPassword.setHintTextColor(getResources().getColor(
								R.color.blue));
						etPassword.setHint("Enter Password");
					}

				}
			} finally {
				if (!username.isEmpty() || !password.isEmpty()) {
					ParseUser.logInInBackground(username, password,
							new LogInCallback() {
								public void done(ParseUser user,
										ParseException e) {
									if (user != null) {
										showNavigationActivity();
										// Hooray! The user is logged in.
									} else {

										etUsername.getText().clear();
										etUsername
												.setHintTextColor(getResources()
														.getColor(R.color.blue));
										etUsername
												.setHint("Invalid Username or Pass");

										etPassword.getText().clear();
										etPassword
												.setHintTextColor(getResources()
														.getColor(R.color.blue));
										etPassword
												.setHint("Invalid Username or Pass");
										// Signup failed. Look at the
										// ParseException to see what happened.
									}
								}
							});
				}

			}

			break;
		}

	}

	private void showSignUpActivity() {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
		this.overridePendingTransition(R.anim.cell_right_in,
				R.anim.cell_left_out);
		finish();
	}

	private void showNavigationActivity() {
		Intent intent = new Intent(this, NaviActivity.class);
		startActivity(intent);
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}

}
