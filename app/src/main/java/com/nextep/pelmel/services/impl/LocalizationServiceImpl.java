package com.nextep.pelmel.services.impl;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Localized;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.services.ConversionService;
import com.nextep.pelmel.services.LocalizationService;

import java.text.MessageFormat;
import java.util.Locale;

public class LocalizationServiceImpl implements LocalizationService,
		LocationListener {

	/**
	 * Any errorDialog stored during initialization of Google Play Services
	 */
	private int googleServicesErrorCode = 0;
	private boolean googleServicesLoaded = false;
	private LocationManager locationManager;
	private Location location = new Location(LocationManager.GPS_PROVIDER);;
	private boolean locationAvailable = false;

	/**
	 * Define a DialogFragment that displays the error dialog
	 */
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@Override
	public void init() {
		if (servicesConnected()) {
			locationManager = (LocationManager) PelMelApplication.getInstance()
					.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager != null) {
				final Location gpsLoc = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				final Location netLoc = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (netLoc != null) {
					location = netLoc;
				} else if (gpsLoc != null) {
					location = gpsLoc;
				}
				startLocationUpdates();
			}
		}
	}

	@Override
	public void startLocationUpdates() {
		if (!googleServicesLoaded) {
			googleServicesLoaded = servicesConnected();
		}
		if (googleServicesLoaded && locationManager != null) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 2000, 50, this);
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 1000, 50, this);
		}

	}

	public int getGoogleServicesErrorCode() {
		return googleServicesErrorCode;
	}

	@Override
	public void stopLocationUpdates() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public Location getLocation() {
		// DEBUG
		// if (location == null) {
		// location = new Location(LocationManager.GPS_PROVIDER);
		// }
		// location.setLatitude(48.83);
		// location.setLongitude(2.33);
		return location;

	}

	@Override
	public void checkGoogleServicesAvailable(FragmentActivity fragmentActivity) {
		if (!googleServicesLoaded) {
			if (googleServicesErrorCode != 0) {
				// Get the error dialog from Google Play services
				final Dialog errorDialog = GooglePlayServicesUtil
						.getErrorDialog(googleServicesErrorCode,
								fragmentActivity,
								CONNECTION_FAILURE_RESOLUTION_REQUEST);
				// If Google Play services can provide an error dialog
				if (errorDialog != null) {
					// Create a new DialogFragment for the error dialog
					final ErrorDialogFragment errorFragment = new ErrorDialogFragment();
					// Set the dialog in the DialogFragment
					errorFragment.setDialog(errorDialog);
					// Show the error dialog in the DialogFragment
					errorFragment.show(
							fragmentActivity.getSupportFragmentManager(),
							"Location Updates");
				}
			}
		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		final int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(PelMelApplication.getInstance());
		// If Google Play services is available
		int alertMessageId = 0;
		switch(resultCode) {
			case ConnectionResult.SUCCESS:
				// In debug mode, log the status
				Log.d("Location Updates", "Google Play services is available.");
				googleServicesLoaded = true;
				// Continue
				return true;
			// Google Play services was not available for some reason
			default:
				break;
		}

		// Get the error code
		googleServicesErrorCode = resultCode;
		googleServicesLoaded = false;
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("geoloc", "Location update to lat=" + location.getLatitude()
				+ " - lng=" + location.getLongitude());
		this.location = location;
		locationAvailable = true;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("geoloc", "GEOLOC status changed");

	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i("geoloc", "GEOLOC is enabled " + provider);
		locationAvailable = true;
		this.location = locationManager.getLastKnownLocation(provider);
	}

	@Override
	public boolean isGoogleServicesAvailable() {
		return googleServicesLoaded;
	}

	@Override
	public boolean isLocationAvailable() {
		return locationAvailable;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i("geoloc", "GEOLOC is disabled " + provider);
		final String msg = PelMelApplication.getInstance().getString(
				R.string.locationProviderUnavailable);
		final Toast t = Toast.makeText(PelMelApplication.getInstance(), msg,
				Toast.LENGTH_LONG);
		t.show();
		locationAvailable = false;
	}

	@Override
	public String getLocalizedDistanceTo(Localized l, Locale locale) {
		// Computing distance dynamically
		final Location placeLocation = new Location("");
		placeLocation.setLatitude(l.getLatitude());
		placeLocation.setLongitude(l.getLongitude());

		final float distanceInMeters = location.distanceTo(placeLocation);
		int currentTemplate;
		double currentValue;
		if (Locale.ENGLISH.getLanguage().equals(locale.getLanguage())) {
			final float miles = distanceInMeters / 1609.34f;
			if (miles < 0.5) {
				final float feet = distanceInMeters / 0.3048f;
				currentTemplate = R.string.distance_feet;
				currentValue = (int) feet;
			} else {
				currentTemplate = R.string.distance_miles;
				currentValue = (int) (miles * 10.0f);
				currentValue = Math.round(currentValue);
				currentValue = currentValue / 10.0f;
			}
		} else {
			if (distanceInMeters > 1000) {
				currentTemplate = R.string.distance_kilometers;
				currentValue = distanceInMeters / 100;
				currentValue = (int) currentValue;
				currentValue = currentValue / 10;
			} else {
				currentTemplate = R.string.distance_meters;
				currentValue = (int) distanceInMeters;
			}
		}
		final String templateString = PelMelApplication.getInstance()
				.getString(currentTemplate);
		return MessageFormat.format(templateString,
				String.valueOf(currentValue));
	}

	@Override
	public boolean isCheckinEnabled(CalObject object) {
		final ConversionService conversionService = PelMelApplication.getConversionService();

		if(object instanceof Place) {
			return conversionService.getDistanceTo(object) <= PelMelConstants.CHECKIN_DISTANCE;
		} else if(object instanceof Event) {
			// If it is an event, checkin is enabled if the event is started and not yet over
			Event event = (Event)object;
			if(event.getStartDate()!= null && event.getStartDate().getTime()<System.currentTimeMillis() && event.getEndDate()!=null && event.getEndDate().getTime()>System.currentTimeMillis()) {
				// And if we are close enough to the place of this event
				return isCheckinEnabled(event.getPlace());
			}
		}
		return false;

	}
}
