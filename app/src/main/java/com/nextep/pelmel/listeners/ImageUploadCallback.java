package com.nextep.pelmel.listeners;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;

public interface ImageUploadCallback {

	void imageUploaded(Image image, CalObject parent);

	void imageUploadFailed();
}
