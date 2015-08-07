package com.nextep.pelmel.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.PlaceAdapter;
import com.nextep.pelmel.listeners.SortChangeListener;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.CellType;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.Refreshable;
import com.nextep.pelmel.model.SortType;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListPlacesActivity extends MainActionBarActivity implements
		OnItemClickListener, UserListener, SortChangeListener, Refreshable,
		OnScrollListener {

	private ListView listViewData;
	private UserService userService;
	private PlaceAdapter placesAdapter;
	private String searchText;
	private Integer radius = null;
	private ProgressDialog progressDialog;
	private List<Object> places = Collections.emptyList();
	private boolean forceRefresh = false;

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
		listViewData.setOnScrollListener(this);

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
			PelMelApplication.setCurrentTab(PelMelConstants.TAB_PLACES);
		}
	}

	@Override
	public void userInfoAvailable(final User user) {
		new AsyncTask<Void, Void, List<Place>>() {

			@Override
			protected List<Place> doInBackground(Void... params) {
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

				final List<Place> placesList = dataService.getNearbyPlaces(
						user, loc.getLatitude(), loc.getLongitude(),
						PelMelApplication.getSearchParentKey(), searchText,
						radius, forceRefresh);
				// Resetting radius after use
				radius = null;
				forceRefresh = false;
				if (placesList != null) {
					return new ArrayList<Place>(placesList);
				}

				return new ArrayList<Place>();
			};

			@Override
			protected void onPostExecute(java.util.List<Place> result) {
				if (result.size() > 0) {
					ListPlacesActivity.this.places = new ArrayList<Object>(
							result);
					sortChanged(SortType.NEARBY);
					listViewData.setSelection(result.size() > 1 ? 1 : 0);
				} else {
					ListPlacesActivity.this.places = new ArrayList<Object>();
					if (!getLocalizationService().isLocationAvailable()) {
						ListPlacesActivity.this.places
								.add(CellType.NO_LOCALIZATION_CELL);
					} else if (!getLocalizationService()
							.isGoogleServicesAvailable()) {
						ListPlacesActivity.this.places
								.add(CellType.NO_GOOGLE_SERVICES_CELL);

					} else {
						ListPlacesActivity.this.places
								.add(CellType.RADIUS_CELL);
					}

					// Displaying google play services upgrade dialog if needed
					getLocalizationService().checkGoogleServicesAvailable(ListPlacesActivity.this);
					placesAdapter = new PlaceAdapter(getBaseContext(),
							android.R.layout.simple_list_item_2,
							ListPlacesActivity.this.places,
							ListPlacesActivity.this, ListPlacesActivity.this);
					listViewData.setAdapter(placesAdapter);
					loadVisibleImages();
				}
				if (progressDialog.isShowing()) {
					try {
						progressDialog.dismiss();
					} catch (final RuntimeException e) {
						Log.d("LIST_PLACES",
								"Error dismissing progress: " + e.getMessage(),
								e);
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
		final Object place = placesAdapter.getItem(position);
		if (place instanceof Place) {
			final Intent intent = new Intent(this, OverviewActivity.class);
			PelMelApplication.setOverviewObject((Place)place);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else {
			// Refreshing
			progressDialog.show();
			userService.getCurrentUser(this);
		}
	}

	@Override
	public void sortChanged(final SortType sortType) {
		final List<Object> placesCopy = new ArrayList<Object>(places);
		Collections.sort(placesCopy, new Comparator<Object>() {
			@Override
			public int compare(Object o0, Object o1) {
				final Place p0 = (Place) o0;
				final Place p1 = (Place) o1;
				switch (sortType) {
				case ALPHABETICAL:
					return p0.getName().compareTo(p1.getName());
				case NEARBY:
					return (int) ((p0.getDistance() - p1.getDistance()) * 10000.0f);
				default:
					return places.indexOf(p0) - places.indexOf(p1);
				}
			}
		});
		placesCopy.add(0, CellType.SORT_CELL);
		// placesCopy.add(1, CellType.SEARCH_CELL);
		// placesCopy.add(CellType.RADIUS_CELL);
		placesAdapter = new PlaceAdapter(getBaseContext(),
				android.R.layout.simple_list_item_2, placesCopy, this, this);
		listViewData.setAdapter(placesAdapter);
		loadVisibleImages();
	}

	@Override
	public void refresh(Object... args) {
		// Forces refresh
		progressDialog.show();
		forceRefresh = true;
		if (args.length == 1) {
			if (args[0] instanceof CharSequence) {
				searchText = (String) args[0];
			} else if (args[0] instanceof Integer) {
				radius = (Integer) args[0];
			}
		}
		userService.getCurrentUser(this);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			loadVisibleImages();
		}
	}

	private void loadVisibleImages() {
		listViewData.post(new Runnable() {

			@Override
			public void run() {
				final int firstIndex = listViewData.getFirstVisiblePosition();
				final int lastIndex = listViewData.getLastVisiblePosition();
				if (placesAdapter != null) {
					placesAdapter.loadImages(firstIndex, lastIndex);
				}
			}
		});
	}
}
