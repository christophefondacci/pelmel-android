package com.nextep.pelmel.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.handlers.MyLifecycleHandler;
import com.nextep.pelmel.helpers.GeoUtils;
import com.nextep.pelmel.helpers.Utils;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.Action;
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
	private static final String BUNDLE_STATE_LOADED = "isLoaded";

	private Map<Marker, Place> placeMarkersMap;
	private Map<String,Marker> markersKeyMap;
	private Map<String,BitmapDescriptor> markersBitmapMap;
	private GoogleMap map;
	private List<Place> cachedPlaces;
	private SnippetContainerSupport snippetContainerSupport;
	private boolean forceRefresh;
	private boolean isLoaded = false;
	private LayoutInflater layoutInflater;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_map,container);

		layoutInflater = inflater;
		placeMarkersMap = new HashMap<Marker, Place>();
		markersKeyMap = new HashMap<>();
		markersBitmapMap = new HashMap<>();

		if(savedInstanceState != null) {
			isLoaded = savedInstanceState.getBoolean(BUNDLE_STATE_LOADED);
		}

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

		// Wiring action buttons
		final ImageView refreshAction = (ImageView)view.findViewById(R.id.refreshButton);
		refreshAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				forceRefresh = true;
				isLoaded = false;
				showProgress();
				PelMelApplication.getUserService().reconnect(MapActivity.this);
			}
		});

		final ImageView checkinAction = (ImageView)view.findViewById(R.id.checkinButton);
		checkinAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PelMelApplication.getActionManager().executeAction(Action.CHECKIN,null);
			}
		});
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

	private void showProgress() {
		snippetContainerSupport.showMessage(R.string.loadingWaitMsg,R.color.bannerInfo, 0);

//		progressDialog = new ProgressDialog(this.getActivity());
//		progressDialog.setCancelable(false);
//		progressDialog.setMessage(getString(R.string.loadingWaitMsg));
//		progressDialog.setTitle(getString(R.string.waitTitle));
//		progressDialog.setIndeterminate(true);
//		progressDialog.show();
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
			showProgress();
			PelMelApplication.getUserService().getCurrentUser(this);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if(!MyLifecycleHandler.isApplicationInForeground()) {
			Log.d(TAG_MAP, "Clearing cache for next wake up");
			PelMelApplication.getDataService().clearCache();
		}
	}

	@Override
	public void userInfoAvailable(final User user) {
		final Location loc = PelMelApplication.getLocalizationService().getLocation();
		final LatLng myPos = new LatLng(loc.getLatitude(), loc.getLongitude()); //map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());
		final VisibleRegion region = map.getProjection().getVisibleRegion();
		final CameraPosition cameraPosition = map.getCameraPosition();
		AsyncTask<Void, Void, List<Place>> asyncTask = new AsyncTask<Void, Void, List<Place>>() {
			@Override
			protected List<Place> doInBackground(Void... params) {

//				final ConversionService
				double lat = 0;
				double lng = 0;
				Integer radius = null;
				if(region.latLngBounds.contains(myPos) || (! isLoaded && !forceRefresh)) {
					lat = loc.getLatitude();
					lng = loc.getLongitude();
					radius = null;
				} else {
					lat = cameraPosition.target.latitude;
					lng = cameraPosition.target.longitude;

					double boundLat = region.latLngBounds.northeast.latitude;
					double boundLng = region.latLngBounds.northeast.longitude;
					radius = (int)GeoUtils.distance(lat,lng,boundLat,boundLng);
				}
				final List<Place> places = PelMelApplication.getDataService()
						.getNearbyPlaces(user, lat, lng,
								null, null, radius, forceRefresh);

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

					for (final Place p : places) {
						if (p != null) {
							final Marker marker = buildMarkerFor(p);
							final LatLng markerPos = marker.getPosition();
							// Zoom management
							if(userZoomBounds.contains(markerPos)) {
								placesInUserZoom++;
							}
							zoomFitBoundsBuilder.include(markerPos);
						}
					}
					if(!isLoaded) {
						if (placesInUserZoom >= PelMelConstants.MAP_MINIMUM_PLACES_FOR_ZOOM) {
							final CameraUpdate upd = CameraUpdateFactory.newLatLngBounds(userZoomBounds, 0);
							map.animateCamera(upd);
						} else {
							LatLngBounds bounds = zoomFitBoundsBuilder.build();
							if (places.size() > 2) {
								try {
									final CameraUpdate upd = CameraUpdateFactory.newLatLngBounds(bounds, 30);
									map.animateCamera(upd);
								} catch (IllegalStateException e) {
									Log.e(TAG_MAP, "Unable to adjust camera: " + e.getMessage(), e);
								}
							}
						}
					}
				}
				if(!isLoaded) {
					snippetContainerSupport.showSnippetFor(new ContextSnippetInfoProvider(), false, false);
				}
				isLoaded = true;
				snippetContainerSupport.hideMessages();
			}

		}.execute();
	}

	private Marker buildMarkerFor(Place p) {
		final Resources res = PelMelApplication.getInstance()
				.getResources();

		int markerCode;
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

		// Inflating marker


//							final BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
//									.fromResource(markerCode);
		final LatLng markerPos = new LatLng(p.getLatitude(),
				p.getLongitude());
		final BitmapDescriptor bitmap = getMarkerBitmap(markerCode,p.getInsidersCount());
		final double distance = PelMelApplication.getConversionService().getDistanceTo(p);
		final String distStr = PelMelApplication.getConversionService().getDistanceStringForMiles(distance);
		final Marker marker = map.addMarker(new MarkerOptions()
				.position(markerPos).icon(bitmap)
				.title(p.getName()).snippet(distStr));
		placeMarkersMap.put(marker, p);
		markersKeyMap.put(p.getKey(),marker);
		return marker;
	}

	private String buildMarkerKey(int markerCode, int count) {
		return String.valueOf(markerCode) + "_" + String.valueOf(count);
	}
	private BitmapDescriptor getMarkerBitmap(int markerCode, int count) {
		final Resources res = PelMelApplication.getInstance().getResources();
		final String markerKey = buildMarkerKey(markerCode, count);
		BitmapDescriptor markerBitmap = markersBitmapMap.get(markerKey);
		if(markerBitmap == null) {
			final View markerView = layoutInflater.inflate(R.layout.map_marker,null);
			final ImageView markerImage = (ImageView)markerView.findViewById(R.id.markerImage);
			final TextView markerBadge = (TextView)markerView.findViewById(R.id.badgeLabel);
			markerImage.setImageBitmap(BitmapFactory.decodeResource(res,markerCode));
			markerBadge.setText(String.valueOf(count));
			if(count==0) {
				markerBadge.setVisibility(View.INVISIBLE);
			}

			// Building bitmap descriptor
			markerBitmap = BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(getActivity(),markerView));
			markersBitmapMap.put(markerKey,markerBitmap);
		}
		return markerBitmap;
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

	public void refreshMarkerFor(Place place) {
		final Marker m = markersKeyMap.get(place.getKey());
		m.remove();
		final Marker marker = buildMarkerFor(place);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(BUNDLE_STATE_LOADED, isLoaded);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if(savedInstanceState != null) {
			isLoaded = savedInstanceState.getBoolean(BUNDLE_STATE_LOADED);
		}
	}
}
