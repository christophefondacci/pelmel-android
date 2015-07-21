package com.nextep.pelmel.providers.impl;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.ChatActivity;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.providers.ThumbsBoxProvider;
import com.nextep.pelmel.providers.base.AbstractCalOverviewProvider;

public class UserOverviewProvider extends AbstractCalOverviewProvider {

	private final User user;
	private final ThumbsBoxProvider likedPlacesThumbsProvider;
	private final ThumbsBoxProvider likedUsersThumbsProvider;
	private ThumbsBoxProvider insidersThumbsProvider;

	public UserOverviewProvider(User user) {
		super(user);
		this.user = user;
		final Resources res = PelMelApplication.getInstance().getResources();
		final Bitmap likeIcon = ((BitmapDrawable) res
				.getDrawable(R.drawable.like_button_2x)).getBitmap();
		final Bitmap markerIcon = ((BitmapDrawable) res
				.getDrawable(R.drawable.like_button_2x)).getBitmap();
		likedPlacesThumbsProvider = new CalThumbsBoxProvider(user,
				user.getLikedPlaces(), R.string.liked_places_title, markerIcon);
		likedUsersThumbsProvider = new CalThumbsBoxProvider(user,
				user.getLikedUsers(), R.string.liked_users_title, likeIcon);
	}

	@Override
	public String getSubtitle() {
		if (user != null && user.getBirthDate() != null) {
			long birthTime = user.getBirthDate().getTime();
			long ageTime = System.currentTimeMillis() - birthTime;
			int age = (int) (ageTime / (1000 * 60 * 60 * 24 * 365.25));
			return String.valueOf(age) + " years old";
		}
		return null;
	}

	@Override
	public String getLocationInfo() {
		final Date lastLocTime = user.getLastLocationTime();
		final Place lastLoc = user.getLastLocation();
		if (lastLocTime != null && lastLoc != null) {
			final Resources res = PelMelApplication.getInstance()
					.getResources();
			long delta = System.currentTimeMillis() - lastLocTime.getTime();
			if (delta < 60000) {
				delta = 60000;
			}
			long value;
			String timeScale;
			if (delta < 3600000) {
				value = delta / 60000;
				timeScale = res.getString(R.string.age_minutes);
			} else if (delta < 86400000) {
				value = delta / 3600000;
				timeScale = res.getString(R.string.age_hours);
			} else {
				value = delta / 86400000;
				timeScale = res.getString(R.string.age_days);
			}
			final String template = res.getString(R.string.user_last_seen);
			return String.format(template, lastLoc.getName(),
					String.valueOf(value), timeScale);
		}
		return null;
	}

	@Override
	public ThumbsBoxProvider getTopThumbsBoxProvider() {
		return likedUsersThumbsProvider;
	}

	@Override
	public ThumbsBoxProvider getBottomThumbsBoxProvider() {
		return likedPlacesThumbsProvider;
	}

	@Override
	public void prepareButton(Button button, final Activity parentActivity) {
		button.setBackgroundDrawable(PelMelApplication.getInstance()
				.getResources().getDrawable(R.drawable.chat_button));
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Starting intent showing the chat dialog with this user
				final Intent intent = new Intent(parentActivity,
						ChatActivity.class);
				intent.putExtra(ChatActivity.CHAT_WITH_USER_KEY, user.getKey());
				parentActivity.startActivity(intent);
			}
		});
	}
}
