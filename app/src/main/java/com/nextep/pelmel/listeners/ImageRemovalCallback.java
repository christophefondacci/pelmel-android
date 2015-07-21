package com.nextep.pelmel.listeners;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;

/**
 * The callback of a remove operation.
 * 
 * @author cfondacci
 * 
 */
public interface ImageRemovalCallback {

	/**
	 * Informs that the given image has been successfully removed from the
	 * object on the server. The application could apply corresponding changes.
	 * 
	 * @param image
	 *            the image that has been removed
	 * @param fromObject
	 *            the object which was holding this image
	 */
	void imageRemoved(Image image, CalObject fromObject);
}
