package com.nextep.pelmel.providers.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.dialogs.SelectImageDialogFragment;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.providers.ThumbsBoxProvider;
import com.nextep.pelmel.providers.base.AbstractCalOverviewProvider;

public class EventOverviewProvider extends AbstractCalOverviewProvider {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private final Event event;
	private final ThumbsBoxProvider likersThumbsProvider;
	private final ThumbsBoxProvider placeThumbsProvider;

	public EventOverviewProvider(Event event) {
		super(event);
		this.event = event;
		final Resources res = PelMelApplication.getInstance().getResources();
		final Bitmap userIcon = ((BitmapDrawable) res
				.getDrawable(R.drawable.user_button)).getBitmap();
		final Bitmap markerIcon = ((BitmapDrawable) res
				.getDrawable(R.drawable.marker_button)).getBitmap();

		likersThumbsProvider = new CalThumbsBoxProvider(event,
				event.getComers(), R.string.likers_title, userIcon);
		if (event.getPlace() != null) {
			final List<Place> eventPlace = new ArrayList<Place>();
			if (event.getPlace() != null) {
				eventPlace.add(event.getPlace());
			}
			placeThumbsProvider = new CalThumbsBoxProvider(event, eventPlace,
					R.string.event_place_title, markerIcon);
		} else {
			placeThumbsProvider = null;
		}
	}

	@Override
	public String getSubtitle() {
		if (event.getStartDate() != null) {
			final String startDateStr = DATE_FORMAT
					.format(event.getStartDate());
			return startDateStr;
		}
		return "";
	}

	@Override
	public String getLocationInfo() {
		final Place place = event.getPlace();
		return (place != null ? place.getName() : "")
				+ event.getDistanceLabel();
	}

	@Override
	public ThumbsBoxProvider getTopThumbsBoxProvider() {
		return placeThumbsProvider;
	}

	@Override
	public ThumbsBoxProvider getBottomThumbsBoxProvider() {
		return likersThumbsProvider;
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
