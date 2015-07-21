package com.nextep.pelmel.providers;

import java.util.List;

import android.app.Activity;
import android.widget.Button;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Tag;

/**
 * Provider of information displayed on an overview page
 * 
 * @author cfondacci
 * 
 */
public interface OverviewProvider {

	/**
	 * Overview page title
	 * 
	 * @return the overview title
	 */
	String getTitle();

	/**
	 * Overview subtitle
	 * 
	 * @return the subtitle of the overview
	 */
	String getSubtitle();

	/**
	 * The location information
	 * 
	 * @return the location information displayed on the overview page
	 */
	String getLocationInfo();

	/**
	 * The description of the element displayed on the overview page
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * The number of likes
	 * 
	 * @return the number of likes
	 */
	int getLikes();

	/**
	 * The first (top most) box of thumbs to display on the overview page
	 * 
	 * @return the topmost box of thumbs on the overview
	 */
	ThumbsBoxProvider getTopThumbsBoxProvider();

	/**
	 * The second (bottom) box of thumbs to display on the overview page
	 * 
	 * @return the bottom box of thumbs on the overview
	 */
	ThumbsBoxProvider getBottomThumbsBoxProvider();

	CalObject getOverviewObject();

	/**
	 * Adjusts the display and action of the customizable button on overview
	 * 
	 * @param button
	 *            the button to setup
	 * @param parentActivity
	 *            the parent activity
	 */
	void prepareButton(Button button, Activity parentActivity);

	/**
	 * Provides the list of tags for the overviews item
	 * 
	 * @return a list of {@link Tag}
	 */
	List<Tag> getTags();
}
