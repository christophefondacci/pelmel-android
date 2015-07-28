package com.nextep.pelmel.providers;

import android.graphics.Bitmap;

import com.nextep.pelmel.model.CalObject;

/**
 * Created by cfondacci on 27/07/15.
 */
public interface CountersProvider {

    int COUNTER_LIKE = 0;
    int COUNTER_CHECKIN = 1;
    int COUNTER_CHAT = 2;

    /**
     * Provides label for the counter
     */
    String getCounterLabelAtIndex(int index);

    /**
     * Provides the label of the counter action, if any
     * @param index index of the counter
     * @return the action label
     */
    String getCounterActionLabelAtIndex(int index);

    /**
     * Informs whether the counter is selected
     * @param index the index of the counter
     * @return <code>true</code> when selected, else <code>false</code>
     */
    boolean isCounterSelectedAtIndex(int index);

    /**
     * Executes the action for the given counter
     * @param index the index of the counter
     */
    void executeCounterActionAtIndex( int index);

    /**
     * Provides the object that the counters relates to
     * @return the counter CalObject
     */
    CalObject getCounterObject();

    /**
     * Provides the image for the counter
     * @param index the index of the counter
     * @return the counter image as a Bitmap
     */
    Bitmap getCounterImageAtIndex(int index);

    /**
     * Provides the
     * @param index
     * @return
     */
//    String getCounterImageNameAtIndex(int index);

    /**
     * Gets the color of the counter
     * @param index
     * @param selected
     * @return
     */
//    Color getCounterColorAtIndex(int index, boolean selected);
}
