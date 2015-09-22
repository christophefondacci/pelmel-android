package com.nextep.pelmel.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.views.BadgeView;

import java.util.Date;

public interface UIService {

	/**
	 * Defines the number of unread messages
	 * 
	 * @param msgCount
	 *            the new count of unread messages
	 */
	void setUnreadMessagesCount(int msgCount);

	/**
	 * Registers the badge view that shows the count of unread messages
	 * 
	 * @param badgeView
	 *            the {@link BadgeView}
	 */
	void registerUnreadMsgBadgeView(TextView badgeView);

	/**
	 * Provides the color resource id corresponding to the given place type
	 * 
	 * @param placeType
	 * @return
	 */
	int getColorForPlaceType(String placeType);

	/**
	 * Provides the string resource for the given place type
	 * 
	 * @param placeType
	 *            the place type code
	 * @return the label resource
	 */
	int getLabelForPlaceType(String placeType);

	/**
	 * Provides the resource id of the icon for this place type
	 *
	 * @param placeType the place type code
	 * @return the drawable resource ID
	 */
	int getIconForPlaceType(String placeType);

	/**
	 * Builds a new info provider object for the given CAL Object
	 *
	 * @param object the CalObject to build an info provider for
	 * @return the provider of snippet information
	 */
	SnippetInfoProvider buildInfoProviderFor(CalObject object);

	/**
	 * Displays a simple info message with a OK button
	 * @param context the Context
	 * @param resTitle the resource for the message title
	 * @param resMessage the resource for the message contents
	 */
	void showInfoMessage(Context context, int resTitle, int resMessage);
	void showInfoMessage(Context context, int resTitle, int resMessage, String argument);

	void executeOnUiThread(Runnable task);

	/**
	 * Provides a no photo bitmap for the given object
	 * @param obj the object to get a no photo placeholder image for
	 * @param thumb whether it's a placeholder for a thumb size
	 * @return the no photo bitmap, which by default will allow additions (if possible)
	 */
	Bitmap getNoPhotoFor(CalObject obj, boolean thumb);

	/**
	 * Provides a no photo bitmap for the given object
	 * @param obj the object to get a no photo placeholder image for
	 * @param thumb whether it's a placeholder for a thumb size
	 * @param allowAdditions whether or not additions are allowed (may or may not result in a different image)
	 * @return the no photo bitmap
	 */
	Bitmap getNoPhotoFor(CalObject obj, boolean thumb, boolean allowAdditions);

	/**
	 * Builds a string expressing the delay between now and the given date
	 * @param fromDate the date to compute the delay with
	 * @return the delay string between current date and given date
	 */
	String getDelayString(Date fromDate);

}
