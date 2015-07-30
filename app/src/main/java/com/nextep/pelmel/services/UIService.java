package com.nextep.pelmel.services;

import android.content.Context;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.views.BadgeView;

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
	void registerUnreadMsgBadgeView(BadgeView badgeView);

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

}
