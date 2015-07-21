package com.nextep.pelmel.model;

import android.graphics.Bitmap;

public interface Image extends Keyed {

	String getThumbUrl();

	void setThumbUrl(String url);

	String getUrl();

	void setUrl(String url);

	Bitmap getThumb();

	void setThumb(Bitmap bitmap);

	Bitmap getFullImage();

	void setFullImage(Bitmap bitmap);

	boolean isThumbLoaded();

	void setThumbLoaded(boolean loaded);
}
