package com.nextep.pelmel.services;

import com.nextep.pelmel.model.EventType;
import com.nextep.pelmel.model.Localized;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.RecurringEvent;

import java.util.List;
import java.util.Map;

/**
 * Created by cfondacci on 22/07/15.
 */
public interface ConversionService {

    /**
     * Indicates if current device prefers metric or imperial units
     *
     * @return <code>true</code> if metric should be preferred, else <code>false</code>
     */
    boolean isMetric();
    /**
     * Provides the user-readable distance string in the current user system from the given distance
     * expressed in miles.
     *
     * @param distance distance to convert, in miles
     * @return the user readable string for that distance
     */
    String getDistanceStringForMiles(double distance);

    /**
     * Returns the distance between current user and given place.
     * For now the current implementation returns server-computed distance so it will not be refreshed
     * as the user moves until another call is made to the server.
     *
     * @param localized the point to which we want to compute the distance to
     * @return the distance to this geo localized point
     */
    double getDistanceTo(Localized localized);

    /**
     * Builds a map of a list of hours hashed by their event type
     * @param place the place to compute the hours map for
     * @return a hashmap of lists of RecurringEvent hashed by their event type
     */
    Map<EventType, List<RecurringEvent>> buildTypedHoursMap(Place place);

    /**
     * Provides a human readable string of the recurring event times, localized for current locale.
     *
     * @param event the RecurringEvent to get the label for
     * @return a recurrency label like "Mon.,Tue.-Fri. 8am-8pm"
     */
    String getRecurringEventLabel(RecurringEvent event);
}
