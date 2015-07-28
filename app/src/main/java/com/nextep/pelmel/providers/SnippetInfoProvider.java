package com.nextep.pelmel.providers;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Image;

import java.util.List;

/**
 * Definition of the interface that can provide information which fills the Snippet list view.
 * <p/>
 * Created by cfondacci on 21/07/15.
 */
public interface SnippetInfoProvider {

    /**
     * The element being displayed
     *
     * @return the CalObject being displayed
     */
    CalObject getItem();

    /**
     * Title field
     *
     * @return the main title field
     */
    String getTitle();

    /**
     * The subtitle field
     *
     * @return the subtitle
     */
    String getSubtitle();

    /**
     * The icon displayed next to the subtitle (object type)
     *
     * @return the Bitmap to display
     */
    Bitmap getSubtitleIcon();

    /**
     * The main image for this element
     *
     * @return the Image of the element
     */
    Image getSnippetImage();

    /**
     * Number of likes
     *
     * @return the number of likes
     */
    int getLikesCount();

    /**
     * The number of reviews
     *
     * @return the number of reviews
     */
    int getReviewsCount();

    /**
     * The number of current checkins
     *
     * @return the checkins count
     */
    int getCheckinsCount();

    /**
     * The description for this element
     *
     * @return the description of this element
     */
    String getDescription();

    /**
     * Label for the type of element being displayed
     *
     * @return the item type
     */
    String getItemTypeLabel();

    /**
     * The localization city
     *
     * @return the city name
     */
    String getCity();

    /**
     * The title for the opening times right badge of the snippet (usually OPENED or CLOSED)
     *
     * @return the text for the hours title badge
     */
    String getHoursBadgeSubtitle();

    /**
     * The subtitle for the opening times right badge of the snippet (usually "opens in ..."  or "... hrs left")
     *
     * @return the text for the hours title badge
     */
    String getHoursBadgeTitle();

    /**
     * Provides the color of the hours badge information
     * @return the color for hours information
     */
    int getHoursColor();

    /**
     * Intro text for the distance information
     * @return the distance intro text
     */
    String getDistanceIntroText();

    /**
     * Distance text for the snippet
     * @return the distance text
     */
    String getDistanceText();

    /**
     * Provides the list of address components to display in the snippet. One entry will represent
     * one line
     *
     * @return the list of address lines to display in the snippet
     */
    List<String> getAddressComponents();

    /**
     * List of events to display in this snippet
     * @return the list of events
     */
    List<Event> getEvents();

    boolean hasCustomSnippetView();

    void createCustomSnippetView(Context context, View parent);

    void refreshCustomSnippetView(Context context, View parent);

    CountersProvider getCountersProvider();
}
