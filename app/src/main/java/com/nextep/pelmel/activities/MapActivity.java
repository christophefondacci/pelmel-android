package com.nextep.pelmel.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.GeoUtils;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.providers.impl.ContextSnippetInfoProvider;
import com.nextep.pelmel.services.ConversionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends Fragment implements UserListener,
		OnInfoWindowClickListener,GoogleMap.OnMarkerClickListener {

	private static final String TAG_MAP = "MAP";
	private Map<Marker, Place> placeMarkersMap;
	private GoogleMap map;
	private List<Place> cachedPlaces;
	private SnippetContainerSupport snippetContainerSupport;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_map,container);

		placeMarkersMap = new HashMap<Marker, Place>();

		// Initializing map and zooming to current location
		final Location loc = PelMelApplication.getLocalizationService().getLocation();
		SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
		if(mapFragment == null) {
			mapFragment = new SupportMapFragment();
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.add(R.id.mapContainer,mapFragment).addToBackStack(null).commit();
		}
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerClickListener(this);
		// Setting bounds on user location
//		final LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
//		final CameraUpdate upd = CameraUpdateFactory.newLatLngZoom(latlng, 14);
//		map.animateCamera(upd);
//		PelMelApplication.getUserService().getCurrentUser(this);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			snippetContainerSupport = (SnippetContainerSupport)activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SnippetContainerSupport");
		}
	}


	//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//		if (hasFocus) {
//			PelMelApplication.setCurrentTab(PelMelConstants.TAB_MAP);
//		}
//	}

	@Override
	public void onResume() {
		super.onResume();
		if (map != null) {

			Log.d(TAG_MAP, "Refreshing markers");
			PelMelApplication.getUserService().getCurrentUser(this);
		}
	}

	@Override
	public void userInfoAvailable(final User user) {
		final Location loc = PelMelApplication.getLocalizationService().getLocation();
		AsyncTask<Void, Void, List<Place>> asyncTask = new AsyncTask<Void, Void, List<Place>>() {
			@Override
			protected List<Place> doInBackground(Void... params) {
				final List<Place> places = PelMelApplication.getDataService()
						.getNearbyPlaces(user, loc.getLatitude(), loc.getLongitude(),
								null, null, null, false);
				return places;
			}

			@Override
			protected void onPostExecute(List<Place> places) {
				if (places != cachedPlaces) {
					map.clear();
					placeMarkersMap.clear();
					cachedPlaces = places;

					// Creating bounds around user loc to check number of places inside
					// Will be our initial zoom if enough points in there
					final LatLng userLoc = new LatLng(loc.getLatitude(),loc.getLongitude());
					final LatLngBounds userZoomBounds = GeoUtils.createBoundsFromPointWithDistance(userLoc,PelMelConstants.MAP_USERLOCATION_RADIUS);
					int placesInUserZoom = 0;

					// Preparing a bounds builder for zoom fit (will be our default zoom if not
					// enough points in user zoom bounds
					final LatLngBounds.Builder zoomFitBoundsBuilder = new LatLngBounds.Builder();

					// Processing places
					final ConversionService conversionService = PelMelApplication.getConversionService();
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
							} else if (Place.PLACE_TYPE_HOTEL.equals(p.getType())) {
								markerCode = R.drawable.marker_hotel;
							} else if (Place.PLACE_TYPE_OUTDOORS.equals(p.getType())) {
								markerCode = R.drawable.marker_outdoor;
							} else {
								markerCode = R.drawable.marker_bar;
							}
							final BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
									.fromResource(markerCode);
							final LatLng markerPos = new LatLng(p.getLatitude(),
									p.getLongitude());

							final double distance = conversionService.getDistanceTo(p);
							final String distStr = conversionService.getDistanceStringForMiles(distance);
							final Marker marker = map.addMarker(new MarkerOptions()
									.position(markerPos).icon(bitmapDesc)
									.title(p.getName()).snippet(distStr));
							placeMarkersMap.put(marker, p);

							// Zoom management
							if(userZoomBounds.contains(markerPos)) {
								placesInUserZoom++;
							}
							zoomFitBoundsBuilder.include(markerPos);
						}
					}
					if(placesInUserZoom>=PelMelConstants.MAP_MINIMUM_PLACES_FOR_ZOOM) {
						final CameraUpdate upd = CameraUpdateFactory.newLatLngBounds(userZoomBounds,0);
						map.animateCamera(upd);
					} else {
						LatLngBounds bounds = zoomFitBoundsBuilder.build();
						if(places.size()>2) {
							final CameraUpdate upd = CameraUpdateFactory.newLatLngBounds(bounds, 30);
							map.animateCamera(upd);
						}
					}
				}
				snippetContainerSupport.showSnippetFor(new ContextSnippetInfoProvider(),false,false);
			}

		}.execute();
	}

	@Override
	public void userInfoUnavailable() {
		// Nothing to do
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		snippetContainerSupport.openSnippet();
//		final Intent intent = new Intent(this, OverviewActivity.class);
//		PelMelApplication.setOverviewObject(p);
//		startActivity(intent);
//		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		final Place p = placeMarkersMap.get(marker);
		final SnippetInfoProvider infoProvider = PelMelApplication.getUiService().buildInfoProviderFor(p);
		snippetContainerSupport.showSnippetFor(infoProvider,false,false);
		return false;
	}
}
