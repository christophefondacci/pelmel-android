package com.nextep.pelmel.services;

import com.nextep.pelmel.model.Localized;

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
}
