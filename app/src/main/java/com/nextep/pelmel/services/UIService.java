package com.nextep.pelmel.services;

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
}
