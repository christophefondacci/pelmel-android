package com.nextep.pelmel.model;

/**
 * Created by cfondacci on 13/08/15.
 */
public interface ServiceCallback {

    void success(Object... successObjects);
    void failure(Object... failureObjects);
}
