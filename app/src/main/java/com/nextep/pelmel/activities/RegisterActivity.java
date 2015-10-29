package com.nextep.pelmel.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.listeners.UserRegisterListener;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.UserService;

public class RegisterActivity extends AppCompatActivity implements UserRegisterListener {

	private static final String FRAGMENT_TERMS = "terms";

	private EditText registerLogin;
	private EditText registerPassword;
	private EditText registerPasswordConfirm;
	private EditText registerPseudo;
	private Button registerButton;
	private UserService userService;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_row_register);

		// Getting user service
		userService = PelMelApplication.getUserService();

		// Getting controls
		registerLogin = (EditText) findViewById(R.id.registerEmail);
		registerPassword = (EditText) findViewById(R.id.registerPassword);
		registerPasswordConfirm = (EditText) findViewById(R.id.registerPasswordConfirm);
		registerPseudo = (EditText) findViewById(R.id.registerPseudo);
		registerButton = (Button) findViewById(R.id.registerButton);
		TextView registerIntroLabel = (TextView) findViewById(R.id.registerIntroHint);
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
		Strings.setFontFamily(registerLogin);
		Strings.setFontFamily(registerPassword);
		Strings.setFontFamily(registerPasswordConfirm);
		Strings.setFontFamily(registerPseudo);
		Strings.setFontFamily(registerButton);
		Strings.setFontFamily(registerIntroLabel);

		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				register();
			}
		});

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
				final LinearLayout termsContainer = (LinearLayout) findViewById(R.id.pelmelTermsContainer);
				final WebBrowserFragment fragment = new WebBrowserFragment();
				fragment.setUrl(PelMelConstants.URL_TERMS);
				FragmentManager fragmentManager = getSupportFragmentManager();

				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.replace(R.id.pelmelTermsContainer, fragment,FRAGMENT_TERMS );
				transaction.addToBackStack(null);
				transaction.commit();
				getSupportFragmentManager().executePendingTransactions();
			}
		});

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


	}
	private void register() {
		final String login = registerLogin.getText().toString();
		final String password = registerPassword.getText().toString();
		final String passwordConfirm = registerPasswordConfirm
				.getText().toString();
		final String pseudo = registerPseudo.getText().toString();
		progressDialog = new ProgressDialog(RegisterActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.loginWaitMsg));
		progressDialog.setTitle(getString(R.string.waitTitle));
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		PelMelApplication.getUserService().register(login, password,
				passwordConfirm, pseudo, RegisterActivity.this);
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
