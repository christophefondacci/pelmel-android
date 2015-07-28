package com.nextep.pelmel.model;

/**
 * Represents the start state of an event, indicating if the event is already finished, if it's
 * currently happening, or if it starts in the future
 *
 * Created by cfondacci on 28/07/15.
 */
public enum EventStartState {
    PAST,CURRENT,SOON, UNAVAILABLE
}
