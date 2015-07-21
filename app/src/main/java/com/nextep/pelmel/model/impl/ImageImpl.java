package com.nextep.pelmel.model.impl;

import android.graphics.Bitmap;

import com.nextep.pelmel.model.Image;

public class ImageImpl implements Image {

	private String key;
	private String thumbUrl;
	private String url;
	private Bitmap thumb;
	private Bitmap fullImage;
	private boolean thumbLoaded;

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getThumbUrl() {
		return thumbUrl;
	}

	@Override
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}

	@Override
	public Bitmap getThumb() {
		return thumb;
	}

	@Override
	public void setFullImage(Bitmap fullImage) {
		this.fullImage = fullImage;
	}

	@Override
	public Bitmap getFullImage() {
		return fullImage;
	}

	@Override
	public boolean isThumbLoaded() {
		return thumbLoaded;
	}

	@Override
	public void setThumbLoaded(boolean thumbLoaded) {
		this.thumbLoaded = thumbLoaded;
	}
}
