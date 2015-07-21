package com.nextep.pelmel.providers.impl;

import java.util.Locale;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.dialogs.SelectImageDialogFragment;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.providers.ThumbsBoxProvider;
import com.nextep.pelmel.providers.base.AbstractCalOverviewProvider;

public class PlaceOverviewProvider extends AbstractCalOverviewProvider {

	private static final String LOG_TAG = PlaceOverviewProvider.class.getName();
	private final Place place;
	private final ThumbsBoxProvider likersThumbsProvider;
	private final ThumbsBoxProvider eventsThumbsProvider;
	private final ThumbsBoxProvider insidersThumbsProvider;

	public PlaceOverviewProvider(Place place) {
		super(place);
		this.place = place;
		final Resources res = PelMelApplication.getInstance().getResources();
		final Bitmap likeIcon = ((BitmapDrawable) res
				.getDrawable(R.drawable.like_button_2x)).getBitmap();
		final Bitmap eventIcon = ((BitmapDrawable) res
				.getDrawable(R.drawable.calendar_button)).getBitmap();
		final Bitmap userIcon = ((BitmapDrawable) res
				.getDrawable(R.drawable.user_button)).getBitmap();

		likersThumbsProvider = new CalThumbsBoxProvider(place,
				place.getLikers(), R.string.likers_title, likeIcon);
		eventsThumbsProvider = new CalThumbsBoxProvider(place,
				place.getEvents(), R.string.upcoming_events_title, eventIcon);
		insidersThumbsProvider = new CalThumbsBoxProvider(place,
				place.getInsiders(), R.string.insiders_title, userIcon);
	}

	@Override
	public String getSubtitle() {
		String distanceLabel = null;
		try {
			final Locale locale = PelMelApplication.getInstance()
					.getResources().getConfiguration().locale;
			distanceLabel = PelMelApplication.getLocalizationService()
					.getLocalizedDistanceTo(place, locale);
		} catch (final RuntimeException e) {
			Log.e(LOG_TAG,
					"Unable to compute distance to place "
							+ (place != null ? place.getKey() : "null") + ": "
							+ e.getMessage(), e);
		}
		final int placeTypeLabelResource = PelMelApplication.getUiService()
				.getLabelForPlaceType(place.getType());
		final String placeTypeLabel = PelMelApplication.getInstance()
				.getString(placeTypeLabelResource);
		if (distanceLabel != null) {
			return placeTypeLabel + " - " + distanceLabel;
		} else {
			return placeTypeLabel;
		}
	}

	@Override
	public String getLocationInfo() {
		return place.getAddress();
	}

	@Override
	public ThumbsBoxProvider getTopThumbsBoxProvider() {
		if (!place.getEvents().isEmpty()) {
			return eventsThumbsProvider;
		} else {
			return likersThumbsProvider;
		}
	}

	@Override
	public ThumbsBoxProvider getBottomThumbsBoxProvider() {
		if (!place.getEvents().isEmpty()) {
			if (!place.getInsiders().isEmpty()) {
				return insidersThumbsProvider;
			} else if (!place.getLikers().isEmpty()) {
				return likersThumbsProvider;
			}
		}
		return insidersThumbsProvider;
	}

	@Override
	public void prepareButton(Button button, final Activity activity) {
		button.setBackgroundDrawable(PelMelApplication.getInstance()
				.getResources().getDrawable(R.drawable.camera_button));
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = null;
				if (activity instanceof FragmentActivity) {
					fragmentManager = ((FragmentActivity) activity)
							.getSupportFragmentManager();
				}
				if (fragmentManager != null) {
					final SelectImageDialogFragment selectDialog = new SelectImageDialogFragment();
					selectDialog.show(fragmentManager, "PHOTO");
				}
			}
		});
	}

}
