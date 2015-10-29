package com.nextep.pelmel.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.ServiceCallback;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.UserService;

public class LoginActivity extends AppCompatActivity implements OnClickListener,
		UserListener {

	private static final String FRAGMENT_TERMS = "terms";

	private EditText loginEmail;
	private EditText loginPassword;
	private Button loginButton;
	private TextView loggingHint;
	private TextView forgotPassword;
	private UserService userService;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_row_login);

		// Getting user service
		userService = PelMelApplication.getUserService();

		// Getting controls
		loginEmail = (EditText) findViewById(R.id.loginEmail);
		loginPassword = (EditText) findViewById(R.id.loginPassword);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
		forgotPassword = (TextView)findViewById(R.id.forgotPassword);
		TextView loginIntroLabel = (TextView) findViewById(R.id.loginIntroLabel);
//		TextView loginLabel = (TextView) findViewById(R.id.loginLabel);
		Strings.setFontFamily(loginEmail);
		Strings.setFontFamily(loginPassword);
		Strings.setFontFamily(loginButton);
		Strings.setFontFamily(forgotPassword);
		Strings.setFontFamily(loginIntroLabel);
//		Strings.setFontFamily(loginLabel);

		forgotPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PelMelApplication.getUserService().resetPassword(loginEmail.getText().toString(), new ServiceCallback() {
					@Override
					public void success(Object... successObjects) {
						Toast t = Toast.makeText(LoginActivity.this,"Reset password email has been sent",Toast.LENGTH_LONG);
						t.show();
					}

					@Override
					public void failure(Object... failureObjects) {
						Toast t = Toast.makeText(LoginActivity.this,"Unable to send password reset email",Toast.LENGTH_LONG);
						t.show();
					}
				});
			}
		});
		loggingHint = (TextView) findViewById(R.id.loggingHint);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Retrieving last values
		final SharedPreferences prefs = getSharedPreferences(
				PelMelConstants.PREFS_NAME, 0);
		final String lastUsername = prefs.getString(
				PelMelConstants.PREF_USERNAME, null);
		final String lastPassword = prefs.getString(
				PelMelConstants.PREF_PASSWORD, null);
		if (lastUsername != null && lastPassword != null) {
			loginEmail.setText(lastUsername);
			loginPassword.setText(lastPassword);
			login(lastUsername, lastPassword);
		}

	}

	private void login(String username, String password) {
		PelMelApplication.setSearchParentKey(null);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.loginWaitMsg));
		progressDialog.setTitle(getString(R.string.waitTitle));
		progressDialog.setIndeterminate(true);
		progressDialog.show();

		userService.login(username, password, this);
	}

	@Override
	public void onClick(View v) {
		login(loginEmail.getText().toString(), loginPassword.getText()
				.toString());
	}

	@Override
	public void userInfoAvailable(User user) {
		loggingHint.setText("Logged in!");
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		PelMelApplication.getUserService().saveLastLoginInfo();

		// Starting the tab
		final Intent mapIntent = new Intent(this, MainActivity.class); // TabBarActivity.class);
		mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
		startActivity(mapIntent);
		finish();
	}

	@Override
	public void userInfoUnavailable() {
		loggingHint.setText("FAILED");
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onStop() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		super.onStop();
	}

}
