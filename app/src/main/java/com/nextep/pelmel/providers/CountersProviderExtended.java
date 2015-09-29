package com.nextep.pelmel.providers;

/**
 * Created by cfondacci on 29/09/15.
 */
public interface CountersProviderExtended extends CountersProvider {

    /**
     * Provides the background resource for the counter index
     * @param index index of the counter to get resource of
     * @return the background drawable id
     */
    int getCounterBackgroundResource(int index);
}
