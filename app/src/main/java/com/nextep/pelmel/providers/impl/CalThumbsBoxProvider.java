package com.nextep.pelmel.providers.impl;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.ListPlacesActivity;
import com.nextep.pelmel.activities.OverviewActivity;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.providers.ThumbsBoxProvider;
import com.nextep.pelmel.services.ImageService;

public class CalThumbsBoxProvider implements ThumbsBoxProvider {

	private final CalObject parent;
	private final List<? extends CalObject> items;
	private final int titleKey;
	private final Bitmap icon;
	private final ImageService imageService;

	public CalThumbsBoxProvider(CalObject parent,
			List<? extends CalObject> items, int titleKey, Bitmap icon) {
		this.parent = parent;
		this.items = items;
		this.titleKey = titleKey;
		this.icon = icon;
		imageService = PelMelApplication.getImageService();
	}

	@Override
	public Bitmap getImageAtIndex(int index, ImageView forImageView) {
		if (index < items.size()) {
			final CalObject item = items.get(index);
			final Image thumb = item.getThumb();
			// If we got a thumb
			if (thumb != null) {
				// We display it if loaded
				if (thumb.getThumb() != null) {
					return thumb.getThumb();
				} else {
					// If not loaded we load it asynchronously
					imageService.displayImage(thumb, true, forImageView);
				}
			}
			// And we return the default thumb
			return ((BitmapDrawable) PelMelApplication.getInstance()
					.getResources().getDrawable(R.drawable.no_photo))
					.getBitmap();
		}
		return null;
	}

	@Override
	public Intent getIntent(Context context, int index) {
		if (index < items.size()) {
			final CalObject item = items.get(index);
			if (item != null) {
				if (!item.getKey().startsWith("CITY")) {
					final Intent intent = new Intent(context,
							OverviewActivity.class);
					PelMelApplication.setOverviewObject(item);
					return intent;
				} else {
					final Intent intent = new Intent(context,
							ListPlacesActivity.class);
					PelMelApplication.setSearchParentKey(item.getKey());
					return intent;
				}
			}
		}
		return null;
	}

	@Override
	public List<CalObject> getElements() {
		return (List) items;
	}

	@Override
	public String getTitle() {
		return PelMelApplication.getInstance().getResources()
				.getString(titleKey);
	}

	@Override
	public boolean shouldDisplayMoreButton() {
		return items.size() > 5;
	}

	@Override
	public Bitmap getIcon() {
		return icon;
	}

	@Override
	public boolean shouldShow() {
		return !items.isEmpty();
	}

}
