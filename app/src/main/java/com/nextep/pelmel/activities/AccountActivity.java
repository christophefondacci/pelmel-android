package com.nextep.pelmel.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.DaySpinnerAdapter;
import com.nextep.pelmel.adapters.MonthSpinnerAdapter;
import com.nextep.pelmel.adapters.TagAdapter;
import com.nextep.pelmel.adapters.YearSpinnerAdapter;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.ImageService;
import com.nextep.pelmel.services.LocalizationService;
import com.nextep.pelmel.services.UserService;

public class AccountActivity extends ActionBarActivity implements
		OnSeekBarChangeListener, TextWatcher, UserListener, OnClickListener {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private ScrollView scrollView;
	private EditText pseudoLabel;
	// private DatePicker birthDatePicker;
	private Spinner birthDaySpinner;
	private Spinner birthMonthSpinner;
	private Spinner birthYearSpinner;
	private ListView tagsView;
	private SeekBar heightSlider;
	private TextView heightLabel;
	private SeekBar weightSlider;
	private TextView weightLabel;
	private EditText descText;
	private Button managePhotosButton;
	private ImageView userPhoto;
	private TagAdapter tagAdapter;
	private UserService userService;
	private User currentUser;
	private Button profileSaveButton;
	private Button logoutButton;

	private ProgressDialog progressDialog;
	private ImageService imageService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userService = PelMelApplication.getUserService();

		setContentView(R.layout.activity_user_profile);
		imageService = PelMelApplication.getImageService();
		// progressDialog = new ProgressDialog(getBaseContext());
		// progressDialog.setCancelable(false);
		// progressDialog.setMessage("Retrieving data...");
		// progressDialog.setTitle("Please wait");
		// progressDialog.setIndeterminate(true);
		// progressDialog.show();

		// Getting current user
		scrollView = (ScrollView) findViewById(R.id.scrollProfileView);
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				final View ll = AccountActivity.this.findViewById(R.id.layout);
				ll.requestFocus();
				// scrollView.requestFocus();
				// scrollView.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// getWindow()
				// .setSoftInputMode(
				// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				// // InputMethodManager inputManager =
				// // (InputMethodManager)
				// // getSystemService(Context.INPUT_METHOD_SERVICE);
				// // inputManager.hideSoftInputFromWindow(AccountActivity.this
				// // .getCurrentFocus().getWindowToken(),
				// // InputMethodManager.HIDE_NOT_ALWAYS);
				//
				// }
				// }, 100);

			}
		});
		PelMelApplication.getUserService().getCurrentUser(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			PelMelApplication.setCurrentTab(PelMelConstants.TAB_SETTINGS);
			PelMelApplication.getUserService().getCurrentUser(this);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar == heightSlider) {
			currentUser.setHeight(progress);
			final String heightValue = buildHeightLabel(progress);
			heightLabel.setText(heightValue);
		} else {
			currentUser.setWeight(progress);
			final String weightValue = buildWeightLabel(progress);
			weightLabel.setText(weightValue);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	private String buildHeightLabel(int heightInCm) {
		final double totalInches = heightInCm / 2.54f;
		final int feets = (int) (totalInches / 12);
		final int inches = ((int) totalInches % 12);
		return heightInCm + "cm / " + feets + "'" + inches + "''";
	}

	private String buildWeightLabel(int weightInKg) {
		final int pounds = (int) (weightInKg / 0.45359237f);
		return weightInKg + "kg / " + pounds + "lbs";
	}

	// @Override
	// public void onDateChanged(DatePicker view, int year, int monthOfYear,
	// int dayOfMonth) {
	// // Safety : we check that this is our picker
	// if (view == birthDatePicker) {
	// // Building new date
	// final Date newBirthDate = new Date(year - 1900, monthOfYear,
	// dayOfMonth);
	// // Injecting date into user bean
	// currentUser.setBirthDate(newBirthDate);
	// }
	// }

	@Override
	public void afterTextChanged(Editable s) {
		// Void implem
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// Void implem
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Just in case
		if (s != null) {
			currentUser.setDescription(s.toString());
		}
	}

	@Override
	public void userInfoAvailable(final User currentUser) {
		this.currentUser = currentUser;
		// Configuring profile name
		pseudoLabel = (EditText) findViewById(R.id.pseudoText);
		pseudoLabel.setText(currentUser.getName());

		// Configuring thumbnail
		// userPhoto = (ImageView) findViewById(R.id.userPhoto);
		// if (!currentUser.getImages().isEmpty()) {
		// imageService.displayImage(currentUser.getImages().get(0), true,
		// userPhoto);
		// userPhoto.setOnClickListener(this);
		// }

		final Date birthDate = currentUser.getBirthDate();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(birthDate);
		birthDaySpinner = (Spinner) findViewById(R.id.birthDaySpinner);
		birthMonthSpinner = (Spinner) findViewById(R.id.birthMonthSpinner);
		birthYearSpinner = (Spinner) findViewById(R.id.birthYearSpinner);

		birthDaySpinner.setAdapter(new DaySpinnerAdapter());
		birthMonthSpinner.setAdapter(new MonthSpinnerAdapter());
		final YearSpinnerAdapter yearAdapter = new YearSpinnerAdapter(this);
		birthYearSpinner.setAdapter(yearAdapter);

		birthDaySpinner.setSelection(cal.get(Calendar.DAY_OF_MONTH) - 1);
		birthMonthSpinner.setSelection(cal.get(Calendar.MONTH));
		birthYearSpinner.setSelection(cal.get(Calendar.YEAR)
				- yearAdapter.getBaseYear());

		// if (birthDate != null) {
		// birthDatePicker.init(birthDate.getYear() + 1900,
		// birthDate.getMonth(), birthDate.getDate(), this);
		// hideDateCalendar();
		// }

		// Configuring list of tags
		tagsView = (ListView) findViewById(R.id.tagsListView);
		tagAdapter = new TagAdapter(getBaseContext(),
				android.R.layout.simple_list_item_1, currentUser, null);
		tagsView.setAdapter(tagAdapter);
		tagsView.setOnItemClickListener(tagAdapter);

		// Configuring height slider
		heightLabel = (TextView) findViewById(R.id.heightValue);
		heightSlider = (SeekBar) findViewById(R.id.heightSlider);
		heightSlider.setMax(220);
		heightSlider.setProgress(currentUser.getHeight());
		heightSlider.setOnSeekBarChangeListener(this);

		// Configuring weight slider
		weightLabel = (TextView) findViewById(R.id.weightValue);
		weightSlider = (SeekBar) findViewById(R.id.weightSlider);
		weightSlider.setMax(180);
		weightSlider.setProgress(currentUser.getWeight());
		weightSlider.setOnSeekBarChangeListener(this);

		// Configuring the multi line description text
		descText = (EditText) findViewById(R.id.descText);
		descText.setText(currentUser.getDescription());
		descText.addTextChangedListener(this);

		logoutButton = (Button) findViewById(R.id.logout_button);
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final UserService userService = PelMelApplication
						.getUserService();
				userService.logout();
				final Intent intent = new Intent(AccountActivity.this,
						LoginActivity.class);
				startActivity(intent);
			}
		});
		// progressDialog.dismiss();
	}

	@Override
	public void userInfoUnavailable() {
		currentUser = null;
	}

	@Override
	public void onClick(View v) {
		final Intent intent = new Intent(getBaseContext(),
				PhotosManagerActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	protected LocalizationService getLocalizationService() {
		return PelMelApplication.getLocalizationService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.account_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_save:
		case R.id.menu_save_text:

			progressDialog = new ProgressDialog(AccountActivity.this);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(getString(R.string.save_wait_message));
			progressDialog.setTitle(getString(R.string.waitTitle));
			progressDialog.setIndeterminate(true);
			try {
				progressDialog.show();
			} catch (final Exception e) {
				Log.e("ACCOUNT",
						"Error while showing dialog : " + e.getMessage(), e);
			}
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					PelMelApplication.getDataService().saveProfile(
							currentUser,
							getLocalizationService().getLocation()
									.getLatitude(),
							getLocalizationService().getLocation()
									.getLongitude());
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}.execute();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}
}
