package com.nextep.pelmel.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;

public class MapActivity extends MainActionBarActivity implements UserListener,
		OnInfoWindowClickListener {

	private static final String TAG_MAP = "MAP";
	private Map<Marker, Place> placeMarkersMap;
	private GoogleMap map;
	private List<Place> cachedPlaces;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		placeMarkersMap = new HashMap<Marker, Place>();
		setContentView(R.layout.activity_map);

		// Initializing map and zooming to current location
		final Location loc = getLocalizationService().getLocation();
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.setOnInfoWindowClickListener(this);
		// Setting bounds on user location
		final LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
		final CameraUpdate upd = CameraUpdateFactory.newLatLngZoom(latlng, 14);
		map.animateCamera(upd);
		PelMelApplication.getUserService().getCurrentUser(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			PelMelApplication.setCurrentTab(PelMelConstants.TAB_MAP);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (map != null) {

			Log.d(TAG_MAP, "Refreshing markers");
			PelMelApplication.getUserService().getCurrentUser(this);
		}
	}

	@Override
	public void userInfoAvailable(User user) {
		final Location loc = getLocalizationService().getLocation();
		final List<Place> places = PelMelApplication.getDataService()
				.getNearbyPlaces(user, loc.getLatitude(), loc.getLongitude(),
						null, null, null, false);

		if (places != cachedPlaces) {
			map.clear();
			placeMarkersMap.clear();
			cachedPlaces = places;

			final Resources res = PelMelApplication.getInstance()
					.getResources();
			for (final Place p : places) {
				int markerCode;
				if (p != null) {
					if (Place.PLACE_TYPE_BAR.equals(p.getType())) {
						markerCode = R.drawable.marker_bar;
					} else if (Place.PLACE_TYPE_ASSOCIATION.equals(p.getType())) {
						markerCode = R.drawable.marker_asso;
					} else if (Place.PLACE_TYPE_CLUB.equals(p.getType())) {
						markerCode = R.drawable.marker_club;
					} else if (Place.PLACE_TYPE_RESTAURANT.equals(p.getType())) {
						markerCode = R.drawable.marker_restaurant;
					} else if (Place.PLACE_TYPE_SAUNA.equals(p.getType())) {
						markerCode = R.drawable.marker_sauna;
					} else if (Place.PLACE_TYPE_SEXCLUB.equals(p.getType())) {
						markerCode = R.drawable.marker_sexclub;
					} else if (Place.PLACE_TYPE_SHOP.equals(p.getType())) {
						markerCode = R.drawable.marker_sexshop;
					} else {
						markerCode = R.drawable.marker_bar;
					}
					final BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
							.fromResource(markerCode);
					final LatLng markerPos = new LatLng(p.getLatitude(),
							p.getLongitude());
					final Marker marker = map.addMarker(new MarkerOptions()
							.position(markerPos).icon(bitmapDesc)
							.title(p.getName()).snippet(p.getDistanceLabel()));
					placeMarkersMap.put(marker, p);
				}
			}
		}
	}

	@Override
	public void userInfoUnavailable() {
		// Nothing to do
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		final Place p = placeMarkersMap.get(marker);
		final Intent intent = new Intent(this, OverviewActivity.class);
		PelMelApplication.setOverviewObject(p);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}
