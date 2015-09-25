package com.nextep.pelmel.services;

import android.content.Context;

import com.nextep.json.model.IJsonLightEvent;
import com.nextep.json.model.IJsonLightPlace;
import com.nextep.json.model.IJsonLightUser;
import com.nextep.json.model.impl.JsonDeal;
import com.nextep.json.model.impl.JsonEvent;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.json.model.impl.JsonPlace;
import com.nextep.json.model.impl.JsonPlaceOverview;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.pelmel.listeners.LikeCallback;
import com.nextep.pelmel.listeners.OverviewListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Deal;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;

import java.util.List;

public interface DataService {

    Place getPlaceFromJsonOverview(JsonPlaceOverview json);

    Place getPlaceFromJson(JsonPlace json);

    Place getPlaceFromLightJson(IJsonLightPlace json);

    User getUserFromJson(JsonUser json);

    User getUserFromLightJson(IJsonLightUser json);

    Event getEventFromJson(JsonEvent json);

    Event getEventFromLightJson(IJsonLightEvent json);

    Image getImageFromJson(JsonMedia json);

    Deal getDealFromJson(JsonDeal json, CalObject place);

    /**
     * Provides the user for the specified key
     *
     * @param userKey the user for the given item key
     * @return the {@link User}
     */
    User getUser(String userKey);

    /**
     * Provides a list of nearby places which may come from a cache. If no
     * nearby place is available, it will automatically fetch places.
     *
     * @param currentUser  the currently connected user
     * @param latitude     current user latitude
     * @param longitude    current user longitude
     * @param parentKey    optionaly parent key to search places in (city)
     * @param forceRefresh when set to <code>true</code> data will always be retrieved
     *                     from server while on <code>false</code> data may come from
     *                     cache
     * @return the list of nearby places
     */
    List<Place> getNearbyPlaces(User currentUser, double latitude,
                                double longitude, String parentKey, String searchText,
                                Integer radius, boolean forceRefresh);

    /**
     * Provides a list of nearby places around the given point
     *
     * @param latitude  latitude of the point
     * @param longitude longitude of the point
     * @return the list of places near the provided point coordinates
     */
    List<Place> listNearbyPlaces(User currentUser, double latitude,
                                 double longitude, String parentKey, String searchText,
                                 Integer radius);

    /**
     * Lists all events nearby, returned data may come from a cache
     *
     * @param currentUser the currently connected {@link User}
     * @param latitude    the current latitude to search events near
     * @param longitude   the current longitude to search events near
     * @return the list of corresponding events
     */
    List<Event> getNearbyEvents(User currentUser, double latitude,
                                double longitude);

    /**
     * Lists all events nearby
     *
     * @param currentUser the currently connected {@link User}
     * @param latitude    the current latitude to search events near
     * @param longitude   the current longitude to search events near
     * @return the list of corresponding events
     */
    List<Event> listNearbyEvents(User currentUser, double latitude,
                                 double longitude);

    /**
     * Gets the overview data and notifies the listener as soon as it is ready
     *
     * @param object   the {@link CalObject} to get data for
     * @param listener the {@link OverviewListener} to notify
     */
    void getOverviewData(User currentUser, CalObject object,
                         OverviewListener listener);

    /**
     * Sends a like request to the server for the given object. The server will
     * automatically toggle the like (sets a like if not already liked or unset
     * the like if already liked)
     *
     * @param currentUser the current {@link User} which sends the like
     * @param likedObject the {@link CalObject} being liked
     * @param callback    a {@link LikeCallback} which will be notified on success
     */
    void like(User currentUser, CalObject likedObject, LikeCallback callback);

    /**
     * Save the current user profile
     *
     * @param currentUser the currently connected {@link User} to save
     */
    void saveProfile(User currentUser, double latitude, double longitude);

    /**
     * This method will perform 2 things:<br>
     * 1- Fast return an object of the proper instance type, either from cache if available or create a new empty one
     * 2- If needed, it triggers a call to the overview service to get full information on that object and will later
     * call the callback with the loaded information
     *
     * @param itemKey  the item key of the element to get
     * @param listener the listener that should be called when full version of object is available (may never be called if object is in cache)
     * @return the CalObject, might be a very light object with only the item key set
     */
    CalObject getCalObject(String itemKey, OverviewListener listener);

    /**
     * Returns the cached object or <code>null</code> if object not in cache
     *
     * @param clazz
     * @param itemKey
     * @param <T>
     * @return
     */
    <T extends CalObject> T getCachedObject(Class<T> clazz, String itemKey);
    void exportDatabase(Context context);

    void clearCache();

}
