package com.nextep.pelmel.helpers;

import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;

import java.util.Collections;
import java.util.List;

/**
 * Created by cfondacci on 21/07/15.
 */
public final class ContextHolder {
    public static List<Place> places = Collections.emptyList();
    public static List<User> users = Collections.emptyList();
    public static List<Event> events = Collections.emptyList();
    public static int radius = 50;

    private ContextHolder() {}


}
