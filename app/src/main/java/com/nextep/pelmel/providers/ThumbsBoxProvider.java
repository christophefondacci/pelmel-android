package com.nextep.pelmel.providers;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nextep.pelmel.model.CalObject;

public interface ThumbsBoxProvider {

	Bitmap getImageAtIndex(int index, ImageView forImageView);

	List<CalObject> getElements();

	String getTitle();

	boolean shouldDisplayMoreButton();

	Bitmap getIcon();

	boolean shouldShow();

	Intent getIntent(Context context, int index);
}
