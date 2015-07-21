package com.nextep.pelmel.services;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.nextep.pelmel.model.Localized;

import java.util.Locale;

public interface LocalizationService {

	int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	void init();

	void startLocationUpdates();

	void stopLocationUpdates();

	/**
	 * Provides the current user location
	 * 
	 * @return the user geolocation
	 */
	Location getLocation();

	void checkGoogleServicesAvailable(FragmentActivity fragmentActivity);

	/**
	 * Check whether google services are available or not
	 * 
	 * @return whether or not google services are available
	 */
	boolean isGoogleServicesAvailable();

	/**
	 * Have we got a localization already ?
	 * 
	 * @return <code>true</code> if we have a location, else <code>false</code>
	 */
	boolean isLocationAvailable();

	/**
	 * Provides the formatted distance string localized for the current device
	 * (miles / meters) between current location and given point.
	 * 
	 * @param l
	 *            the location to compute the distance to
	 * @param locale
	 *            the {@link Locale} to use for conversion
	 * 
	 * @return the formatted string
	 */
	String getLocalizedDistanceTo(Localized l, Locale locale);

	/**
	 *
	 * @return the error code raised by the google services check
	 */
	int getGoogleServicesErrorCode();
}
