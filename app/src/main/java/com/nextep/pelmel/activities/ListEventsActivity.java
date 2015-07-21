package com.nextep.pelmel.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.EventAdapter;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.UserService;

public class ListEventsActivity extends MainActionBarActivity implements
		OnItemClickListener, UserListener {

	private ListView listViewData;
	private UserService userService;
	private EventAdapter eventAdapter;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_places);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.retrievingData));
		progressDialog.setTitle(getString(R.string.waitTitle));
		progressDialog.setIndeterminate(true);
		progressDialog.show();

		listViewData = (ListView) findViewById(R.id.listViewData);
		listViewData.setOnItemClickListener(this);

		userService = PelMelApplication.getUserService();
		// Getting user, notifying us when ready
		userService.getCurrentUser(this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			PelMelApplication.setCurrentTab(PelMelConstants.TAB_EVENTS);
		}
	}

	@Override
	public void userInfoAvailable(final User user) {
		new AsyncTask<Void, Void, List<Event>>() {

			@Override
			protected List<Event> doInBackground(Void... params) {
				Location loc = null;
				while (loc == null) {
					loc = getLocalizationService().getLocation();
					try {
						Thread.sleep(500);
					} catch (final InterruptedException e) {
						Log.e("DATA", "Async interruption : " + e.getMessage(),
								e);
					}
				}
				final DataService dataService = PelMelApplication
						.getDataService();

				final List<Event> eventsList = dataService.listNearbyEvents(
						user, loc.getLatitude(), loc.getLongitude());

				if (eventsList != null) {
					return eventsList;
				}

				return new ArrayList<Event>();
			};

			@Override
			protected void onPostExecute(java.util.List<Event> result) {

				eventAdapter = new EventAdapter(getBaseContext(),
						android.R.layout.simple_list_item_2, result);
				listViewData.setAdapter(eventAdapter);
				if (progressDialog.isShowing()) {
					try {
						progressDialog.dismiss();
					} catch (final IllegalArgumentException e) {
						Log.e("ListEvents",
								"Error while dismissing progress dialog: "
										+ e.getMessage(), e);
					}
				}
			};

		}.execute();
	}

	@Override
	public void userInfoUnavailable() {
		// Nothing to do
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.list_places, menu);
	// return true;
	// }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Event place = eventAdapter.getItem(position);
		if (place != null) {
			final Intent intent = new Intent(this, OverviewActivity.class);
			PelMelApplication.setOverviewObject(place);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
	}

}
