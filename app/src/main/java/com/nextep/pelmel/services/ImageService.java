package com.nextep.pelmel.services;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.nextep.pelmel.listeners.ImageRemovalCallback;
import com.nextep.pelmel.listeners.ImageUploadCallback;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;

/**
 * A service in charge of loading images in background
 * 
 * @author cfondacci
 * 
 */
public interface ImageService {

	interface ImageReorderCallback {
		/**
		 * Method called when image has been successfully reordered server side
		 * @param image
		 * @param oldIndex
		 * @param newIndex
		 */
		void imageReordered(Image image, int oldIndex, int newIndex);
		void imageReorderingFailed(Image image, String reason);
	}
	/**
	 * Fetches the image referenced by the given URL and displays it in the
	 * specified image view
	 * 
	 * @param url
	 *            image URL
	 * @param imageView
	 *            the {@link ImageView} in which the image should be rendered
	 */
	void displayImage(Image image, boolean isThumb, ImageView imageView);
	void cancelDisplay(ImageView imageView);

	/**
	 * Clears any cache, if cache is supported by the underlying implementation
	 */
	void clearCache();

	/**
	 * Builds an image from the provided source
	 * 
	 * @param imageFile
	 *            the image file to upload
	 * @param parent
	 *            the parent element for which this image needs to be uploaded
	 * @param user
	 *            the currently logged user
	 * @param callback
	 *            the {@link ImageUploadCallback} to notify once our upload is
	 *            done
	 */
	void uploadImage(File imageFile, CalObject parent, User user,
			ImageUploadCallback callback);

	/**
	 * Removes the image from the parent object on the server and calls the
	 * callback on success
	 * 
	 * @param image
	 *            the {@link Image} to remove
	 * @param parent
	 *            the parent {@link CalObject} of this image
	 * @param user
	 *            the currently logged {@link User}
	 * @param callback
	 *            the {@link ImageRemovalCallback} to notify of the server
	 *            response
	 */
	void removeImage(Image image, CalObject parent, User user,
			ImageRemovalCallback callback);

	/**
	 * Re-orders an image to set it to the given new index in the parent's set of
	 * images
	 * @param image the image to change order for
	 * @param newIndex the new index of this image in the parent set of images
	 * @param callback the callback to call when reordering has been made on the server
	 */
	void reorderImage(Image image, CalObject parent, int newIndex, ImageReorderCallback callback);

	/**
	 * Reorients the image referenced by its {@link Uri} and dumps it to a local
	 * file (to save memory).
	 * 
	 * @param context
	 *            the {@link Context} to use for resolving content
	 * @param imageUri
	 *            the {@link Uri} of the image to process
	 * @return the generate {@link File}, reoriented properly
	 */
	File getOrientedImageFileFromUri(Context context, Uri imageUri);

}