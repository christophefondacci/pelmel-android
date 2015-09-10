package com.nextep.pelmel.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.listeners.UserRegisterListener;
import com.nextep.pelmel.model.ServiceCallback;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.UserService;

public class LoginActivity extends Activity implements OnClickListener,
		UserListener, UserRegisterListener {

	private EditText loginEmail;
	private EditText loginPassword;
	private Button loginButton;
	private TextView loggingHint;
	private EditText registerLogin;
	private EditText registerPassword;
	private EditText registerPasswordConfirm;
	private EditText registerPseudo;
	private TextView forgotPassword;
	private Button registerButton;
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
		registerLogin = (EditText) findViewById(R.id.registerEmail);
		registerPassword = (EditText) findViewById(R.id.registerPassword);
		registerPasswordConfirm = (EditText) findViewById(R.id.registerPasswordConfirm);
		registerPseudo = (EditText) findViewById(R.id.registerPseudo);
		registerButton = (Button) findViewById(R.id.registerButton);
		forgotPassword = (TextView)findViewById(R.id.forgotPassword);
		TextView loginIntroLabel = (TextView) findViewById(R.id.loginIntroLabel);
		TextView loginLabel = (TextView) findViewById(R.id.loginLabel);
		TextView registerIntroLabel = (TextView) findViewById(R.id.registerIntroHint);
		TextView registerLabel = (TextView) findViewById(R.id.registerLabel);
		registerPseudo.setSingleLine(true);
		registerPseudo.setImeOptions(EditorInfo.IME_ACTION_SEND);
		registerPseudo.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					register();
					return true;
				}
				return false;
			}
		});
		Strings.setFontFamily(loginEmail);
		Strings.setFontFamily(loginPassword);
		Strings.setFontFamily(loginButton);
		Strings.setFontFamily(registerLogin);
		Strings.setFontFamily(registerPassword);
		Strings.setFontFamily(registerPasswordConfirm);
		Strings.setFontFamily(registerPseudo);
		Strings.setFontFamily(registerButton);
		Strings.setFontFamily(forgotPassword);
		Strings.setFontFamily(loginIntroLabel);
		Strings.setFontFamily(loginLabel);
		Strings.setFontFamily(registerIntroLabel);
		Strings.setFontFamily(registerLabel);

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
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				register();
			}
		});
		loggingHint = (TextView) findViewById(R.id.loggingHint);

		final TextView whyLoginText = (TextView) findViewById(R.id.registerIntroHint);
		whyLoginText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(getBaseContext(),
						TextActivity.class);
				intent.putExtra(TextActivity.KEY_INTENT_TEXT_ID,
						String.valueOf(R.string.whyText));
				startActivity(intent);
			}
		});

		final TextView termsText = (TextView) findViewById(R.id.registerTerms);
		termsText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(getBaseContext(),
						TextActivity.class);
				intent.putExtra(TextActivity.KEY_INTENT_TEXT_ID,
						String.valueOf(R.string.termsText));
				startActivity(intent);
			}
		});

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
	private void register() {
		final String login = registerLogin.getText().toString();
		final String password = registerPassword.getText().toString();
		final String passwordConfirm = registerPasswordConfirm
				.getText().toString();
		final String pseudo = registerPseudo.getText().toString();
		progressDialog = new ProgressDialog(LoginActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.loginWaitMsg));
		progressDialog.setTitle(getString(R.string.waitTitle));
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		PelMelApplication.getUserService().register(login, password,
				passwordConfirm, pseudo, LoginActivity.this);
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

	@Override
	public void userRegistered(User user) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		PelMelApplication.getUserService().saveLastLoginInfo();

		// Starting the tab
		final Intent mapIntent = new Intent(this, MainActivity.class); // TabBarActivity.class);
		mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
		startActivity(mapIntent);
	}

	@Override
	public void registrationFailed(String message) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		final Toast t = Toast.makeText(getBaseContext(), message,
				Toast.LENGTH_SHORT);
		t.show();
	}
}
