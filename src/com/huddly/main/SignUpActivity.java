package com.huddly.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity implements OnClickListener {

	Button btn_back, btn_finish;
	EditText etUsername, etPass1, etPass2, etEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		btn_back = (Button) findViewById(R.id.btnBack);
		btn_back.setOnClickListener(this);

		btn_finish = (Button) findViewById(R.id.btnFinish);
		btn_finish.setOnClickListener(this);

		etUsername = (EditText) findViewById(R.id.etUsername);
		etPass1 = (EditText) findViewById(R.id.etPassword);
		etPass2 = (EditText) findViewById(R.id.etPassword1);
		etEmail = (EditText) findViewById(R.id.etEmail);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.btnBack:
			showMainActivity();
			break;

		case R.id.btnFinish:

			// Retrieve User Info
			// Sign Up with Parse
			// Switch to NavigationActivity
			String username = etUsername.getText().toString().trim();
			String email = etEmail.getText().toString().trim();
			String pass1 = etPass1.getText().toString().trim();
			String pass2 = etPass2.getText().toString().trim();
			ParseUser user = new ParseUser();

			try {
				if (username.isEmpty() || email.isEmpty() || pass1.isEmpty()
						|| pass2.isEmpty()) {
					if (username.isEmpty()) {
						etUsername.setHintTextColor(getResources().getColor(
								R.color.blue));
						etUsername.setHint("Enter Username");
					}
					if (email.isEmpty()) {
						etEmail.setHintTextColor(getResources().getColor(
								R.color.blue));
						etEmail.setHint("Enter Email");
					}
					if (pass1.isEmpty()) {
						etPass1.setHintTextColor(getResources().getColor(
								R.color.blue));
						etPass1.setHint("Enter Password");
					}
					if (pass2.isEmpty()) {
						etPass2.setHintTextColor(getResources().getColor(
								R.color.blue));
						etPass2.setHint("Re-Type Password");
					}
				}

			} finally {
				if (!username.isEmpty() && !email.isEmpty() && !pass2.isEmpty()
						&& isMatching(pass1, pass2)) {
					user.setUsername(username);
					user.setPassword(pass1);
					user.setEmail(email);
					user.put("radius", "10");

					user.signUpInBackground(new SignUpCallback() {
						public void done(ParseException e) {
							if (e == null) {
								// Hooray! Let them use the app now.
								showNavigationActivity();
								finish();

							} else {
								// Sign up didn't succeed. Look at the
								// ParseException
								// to figure out what went wrong
								etEmail.getText().clear();
								etEmail.setHintTextColor(getResources()
										.getColor(R.color.blue));
								etEmail.setHint("Invalid Email");

							}
						}
					});
				} else if (!isMatching(pass1, pass2)) {
					etPass2.getText().clear();
					etPass2.setHintTextColor(getResources().getColor(
							R.color.blue));
					etPass2.setHint("Passwords don't match");
				}
			}
			break;

		}

	}

	private void showMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		this.overridePendingTransition(R.anim.cell_left_in,
				R.anim.cell_right_out);
		finish();
	}

	private void showNavigationActivity() {
		Intent intent = new Intent(this, NaviActivity.class);
		startActivity(intent);
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}

	private boolean isMatching(String pass1, String pass2) {
		if (pass1.equals(pass2)) {
			return true;
		} else {
			return false;
		}
	}
}
