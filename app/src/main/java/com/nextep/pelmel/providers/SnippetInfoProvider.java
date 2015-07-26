package com.nextep.pelmel.providers;

import android.graphics.Bitmap;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;

import java.util.List;

/**
 * Definition of the interface that can provide information which fills the Snippet list view.
 *
 * Created by cfondacci on 21/07/15.
 */
public interface SnippetInfoProvider {

    /**
     * The element being displayed
     * @return the CalObject being displayed
     */
    CalObject getItem();

    /**
     * Title field
     * @return the main title field
     */
    String getTitle();

    /**
     * The subtitle field
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
     * @return the Image of the element
     */
    Image getSnippetImage();

    /**
     * Number of likes
     * @return the number of likes
     */
    int getLikesCount();

    /**
     * The number of reviews
     * @return the number of reviews
     */
    int getReviewsCount();

    /**
     * The number of current checkins
     * @return the checkins count
     */
    int getCheckinsCount();

    /**
     * The description for this element
     * @return the description of this element
     */
    String getDescription();

    /**
     * Label for the type of element being displayed
     * @return the item type
     */
    String getItemTypeLabel();

    /**
     * The localization city
     * @return the city name
     */
    String getCity();

    String getHoursBadgeTitle();
    String getHoursBadgeSubtitle();
    String getDistanceIntroText();
    String getDistanceText();

    List<String> getAddressComponents();

}
