package com.nextep.pelmel.services.impl;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nextep.json.model.IJsonLightEvent;
import com.nextep.json.model.IJsonLightPlace;
import com.nextep.json.model.IJsonLightUser;
import com.nextep.json.model.impl.JsonDescription;
import com.nextep.json.model.impl.JsonEvent;
import com.nextep.json.model.impl.JsonHour;
import com.nextep.json.model.impl.JsonLightEvent;
import com.nextep.json.model.impl.JsonLightPlace;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonLikeInfo;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.json.model.impl.JsonNearbyPlacesResponse;
import com.nextep.json.model.impl.JsonPlace;
import com.nextep.json.model.impl.JsonPlaceOverview;
import com.nextep.json.model.impl.JsonSpecialEvent;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.cache.model.Cache;
import com.nextep.pelmel.cache.model.impl.MemorySizedTTLCacheImpl;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.listeners.LikeCallback;
import com.nextep.pelmel.listeners.OverviewListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.EventType;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.RecurrencyType;
import com.nextep.pelmel.model.RecurringEvent;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.impl.EventImpl;
import com.nextep.pelmel.model.impl.ImageImpl;
import com.nextep.pelmel.model.impl.PlaceImpl;
import com.nextep.pelmel.model.impl.RecurringEventImpl;
import com.nextep.pelmel.model.impl.UserImpl;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.TagService;
import com.nextep.pelmel.services.UserService;
import com.nextep.pelmel.services.WebService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;

public class DataServiceImpl implements DataService {

    private final static String LOG_TAG = "DataService";
    private Cache<Place> placeCache;
    private Cache<User> userCache;
    private Cache<Image> imageCache;
    private Cache<Event> eventCache;
    private List<Place> nearbyPlaces = null;
    private List<Event> nearbyEvents = null;
    private WebService webService;
    private TagService tagService;
    private UserService userService;

    public DataServiceImpl() {
        createCache();
    }

    private void createCache() {
        placeCache = new MemorySizedTTLCacheImpl<Place>(600000, 300);
        userCache = new MemorySizedTTLCacheImpl<User>(600000, 300);
        eventCache = new MemorySizedTTLCacheImpl<Event>(600000, 300);
        imageCache = new MemorySizedTTLCacheImpl<Image>(600000, 300);
    }
    @Override
    public Place getPlaceFromJson(JsonPlace json) {
        Place place = placeCache.get(json.getKey());
        if (place == null) {
            place = new PlaceImpl();
            placeCache.put(json.getKey(), place);
        }
        place.setKey(json.getKey());
        place.setAddress(json.getAddress());
        place.setDescription(json.getDescription());
        place.setDistance(json.getRawDistance());
        place.setDistanceLabel(json.getDistance());
        place.setLatitude(json.getLat());
        place.setLongitude(json.getLng());
        place.setLikeCount(json.getLikesCount());
        place.setInsidersCount(json.getUsersCount());
        place.setName(json.getName());
        place.setType(json.getType());
        place.setCityName(json.getCity());
        // Building tag list
        final List<Tag> tags = new ArrayList<Tag>();
        for (final String tagCode : json.getTags()) {
            final Tag tag = tagService.getTag(tagCode);
            if (tag != null) {
                tags.add(tag);
            }
        }
        place.setTags(tags);

        // Building images
        final List<Image> images = new ArrayList<Image>();
        final JsonMedia mainJsonImage = json.getThumb();
        if (mainJsonImage != null) {
            // Processing main image if one defined
            final Image mainImage = getImageFromJson(mainJsonImage);
            images.add(mainImage);
            // Processing other images
            for (final JsonMedia media : json.getOtherImages()) {
                final Image image = getImageFromJson(media);
                images.add(image);
            }
            // Adding to object images list
            place.setImages(images);
        }

        // Building recurring events
        for (JsonSpecialEvent jsonSpecial : json.getSpecials()) {
            final RecurringEvent event = getRecurringEventFromJsonSpecial(jsonSpecial);
            event.setPlace(place);
            place.addRecurringEvent(event);
        }
        return place;
    }

    private RecurringEvent getRecurringEventFromJsonSpecial(JsonSpecialEvent special) {

        // Getting instance from cache or creating it
        RecurringEvent re = (RecurringEvent) eventCache.get(special.getKey());
        if (re == null) {
            re = new RecurringEventImpl();
            eventCache.put(special.getKey(), re);
        }

        // Filling object from JSON info
        re.setKey(special.getKey());
        re.setName(special.getName());
        re.setDescription(special.getDescription());
        re.setStartDate(new Date(special.getNextStart() * 1000));
        re.setEndDate(new Date(special.getNextEnd() * 1000));
        re.setLikeCount(special.getParticipants());
        try {
            re.setEventType(EventType.valueOf(special.getType()));
        } catch (IllegalArgumentException | NullPointerException e) {
            re.setEventType(EventType.HAPPY_HOUR);
            Log.w(LOG_TAG, "Invalid event type for recurring event " + special.getKey() + ": " + e.getMessage(), e);
        }
        Image calImage = getImageFromJson(special.getThumb());
        if (calImage != null) {
            re.setThumb(calImage);
        }
        return re;
    }

    @Override
    public Place getPlaceFromLightJson(IJsonLightPlace json) {
        if (json != null) {
            final String key = json.getKey();
            Place place = placeCache.get(key);
            if (place == null) {
                place = new PlaceImpl();
                place.setKey(key);
                placeCache.put(key, place);
            }
            place.setName(json.getName());
            place.setType(json.getType());
            if (place.getImages().isEmpty()) {
                final Image thumb = getImageFromJson(json.getThumb());
                place.addImage(thumb);
            }
            return place;
        }
        return null;
    }

    @Override
    public User getUser(String key) {
        User user = userCache.get(key);
        if (user == null) {
            user = new UserImpl();
            user.setKey(key);
            userCache.put(key, user);
        }
        return user;
    }

    @Override
    public User getUserFromJson(JsonUser json) {
        final String key = json.getKey();
        final User user = getUser(key);
        // Updating birth date
        final Long birthDate = json.getBirthDate();
        if (birthDate != null) {
            user.setBirthDate(new Date(json.getBirthDate() * 1000));
        }
        // Updating details
        if (json.getNxtpUserToken() != null
                && !json.getNxtpUserToken().trim().isEmpty()) {
            user.setToken(json.getNxtpUserToken());
        }
        user.setName(json.getPseudo());
        user.setOnline(json.isOnline());
        user.setLikeCount(json.getLikes());
        user.setCityName(json.getCity());
        user.setRawDistanceMiles(json.getRawDistanceMeters() / 1609.34f);
        user.setLiked(json.isLiked());

        // Updating last localisation
        final Place lastLocation = getPlaceFromLightJson(json.getLastLocation());
        user.setLastLocation(lastLocation);
        final Long locTime = json.getLastLocationTime();
        if (locTime != null) {
            user.setLastLocationTime(new Date(json.getLastLocationTime() * 1000));
        }

        // Converting descriptions
        final StringBuilder buf = new StringBuilder();
        JsonDescription localizedDescription = null;
        JsonDescription lastDescription = null;

        // Iterating over all description, selecting the most appropriate description
        for (final JsonDescription d : json.getDescriptions()) {
            if(d.getLanguage().equals(Locale.getDefault().getLanguage()) || (d.getLanguage().equals("en") && localizedDescription == null)) {
                localizedDescription = d;
            }
            if(lastDescription == null) {
                lastDescription = d;
            }
        }
        JsonDescription selectedDesc = localizedDescription !=null ? localizedDescription : lastDescription;
        if(selectedDesc != null) {
            user.setDescriptionKey(selectedDesc.getKey());
            user.setDescription(selectedDesc.getDescription());
            user.setDescriptionLanguage(selectedDesc.getLanguage());
        }

        // Converting images
        final List<Image> images = new ArrayList<Image>();
        for (final JsonMedia media : json.getMedias()) {
            final Image image = getImageFromJson(media);
            images.add(image);
        }
        user.setImages(images);

        // Converting liked places
        final List<Place> likedPlaces = new ArrayList<Place>();
        for (final JsonLightPlace jsonPlace : json.getLikedPlaces()) {
            final Place place = getPlaceFromLightJson(jsonPlace);
            likedPlaces.add(place);
        }
        user.setLikedPlaces(likedPlaces);

        // Converting liked users
        final List<User> likedUsers = new ArrayList<User>();
        for (final JsonLightUser jsonUser : json.getLikeUsers()) {
            final User likedUser = getUserFromLightJson(jsonUser);
            likedUsers.add(likedUser);
        }
        user.setLikedUsers(likedUsers);

        // Converting tags
        final List<Tag> tags = new ArrayList<Tag>();
        for (final String tagCode : json.getTags()) {
            final Tag tag = tagService.getTag(tagCode);
            tags.add(tag);
        }
        user.setTags(tags);

        return user;
    }

    @Override
    public User getUserFromLightJson(IJsonLightUser json) {
        final String key = json.getKey();
        final User user = getUser(key);
        user.setName(json.getPseudo());
        user.setOnline(json.isOnline());

        // Getting thumb
        if (json instanceof JsonLightUser) {
            final JsonMedia jsonMedia = ((JsonLightUser) json).getThumb();
            if (user.getImages().isEmpty()) {
                final Image thumb = getImageFromJson(jsonMedia);
                user.addImage(thumb);
            }
        }

        // Returning our user
        return user;
    }

    @Override
    public Event getEventFromJson(JsonEvent json) {
        final Event event = getEventFromLightJson(json);

        // Now injecting additional info
        event.setLikeCount(json.getLikes());
        final List<JsonLightUser> likers = json.getLikeUsers();
        final List<User> comers = new ArrayList<User>();
        for (final JsonLightUser liker : likers) {
            final User user = getUserFromLightJson(liker);
            comers.add(user);
        }
        event.setComers(comers);

        return event;
    }

    @Override
    public Event getEventFromLightJson(IJsonLightEvent json) {
        final String key = json.getKey();
        Event event = eventCache.get(key);
        if (event == null) {
            event = new EventImpl();
            event.setKey(key);
            eventCache.put(key, event);
        }
        event.setName(json.getName());

        // Getting thumb
        final List<Image> images = new ArrayList<Image>();
        for (final JsonMedia jsonMedia : json.getMedia()) {
            final Image thumb = getImageFromJson(jsonMedia);
            images.add(thumb);
        }
        event.setImages(images);

        // Start time
        final Long startTime = json.getStartTime();
        if (startTime != null) {
            event.setStartDate(new Date(startTime * 1000));
        }
        // End time
        final Long endTime = json.getEndTime();
        if (endTime != null) {
            event.setEndDate(new Date(endTime * 1000));
        }

        if (json.getDistance() != null && !json.getDistance().isEmpty()) {
            event.setDistanceLabel(json.getDistance());
        }
        if (json.getRawDistance() > 0) {
            event.setDistance(json.getRawDistance());
        }
        final IJsonLightPlace jsonPlace = json.getPlace();
        if (jsonPlace != null) {
            final Place p = getPlaceFromLightJson(jsonPlace);
            event.setPlace(p);
        }
        // event.setLikeCount(json.getParticipants());
        // Returning our event
        return event;
    }

    @Override
    public Image getImageFromJson(JsonMedia json) {
        if (json != null) {
            final String key = json.getKey();
            Image img = imageCache.get(key);
            if (img == null) {
                img = new ImageImpl();
                img.setKey(key);
                img.setThumbUrl(json.getThumbUrl());
                img.setUrl(json.getUrl());
                imageCache.put(key, img);
            }
            return img;
        } else {
            return null;
        }
    }

    @Override
    public Place getPlaceFromJsonOverview(JsonPlaceOverview json) {
        if (json == null) {
            return null;
        }
        Place place = placeCache.get(json.getKey());
        if (place == null) {
            place = new PlaceImpl();
            place.setKey(json.getKey());
            placeCache.put(json.getKey(), place);
        }
        place.setAddress(json.getAddress());
        place.setDescription(json.getDescription());
        place.setName(json.getName());
        place.setType(json.getType());
        place.setLikeCount(json.getLikes());
        place.setLiked(json.isLiked());
        place.setReviewsCount(json.getCommentsCount());
        place.setLatitude(json.getLat());
        place.setLongitude(json.getLng());
        place.setCityName(json.getCity());

        // Building media
        final List<Image> images = new ArrayList<>();
        for(JsonMedia jsonMedia : json.getOtherImages()) {
            final Image img = getImageFromJson(jsonMedia);
            images.add(img);
        }
        final JsonMedia jsonMedia = json.getThumb();
        if(jsonMedia != null) {
            final Image img = getImageFromJson(jsonMedia);
            images.add(0,img);
        }
        place.setImages(images);

        // Building tag list
        final List<Tag> tags = new ArrayList<Tag>();
        for (final String tagCode : json.getTags()) {
            final Tag tag = tagService.getTag(tagCode);
            tags.add(tag);
        }
        place.setTags(tags);

        // Building likers
        final List<User> likers = new ArrayList<User>();
        for (final JsonLightUser jsonUser : json.getLikeUsers()) {
            final User user = getUserFromLightJson(jsonUser);
            likers.add(user);
        }
        place.setLikers(likers);

        // Building insiders
        final List<User> insiders = new ArrayList<User>();
        for (final JsonLightUser jsonUser : json.getInUsers()) {
            final User user = getUserFromLightJson(jsonUser);
            insiders.add(user);
        }
        place.setInsiders(insiders);

        // Building events
        final List<Event> events = new ArrayList<Event>();
        for (final JsonLightEvent jsonEvent : json.getEvents()) {
            final Event event = getEventFromLightJson(jsonEvent);
            events.add(event);
        }
        place.setEvents(events);

        // Building hours
        final List<RecurringEvent> hours = new ArrayList<>();
        for (final JsonHour jsonHour : json.getHours()) {
            final RecurringEvent hour = getRecurringEventFromJsonHour(jsonHour);
            hour.setPlace(place);
            hours.add(hour);
        }
        place.setRecurringEvents(hours);
        return place;
    }

    private RecurringEvent getRecurringEventFromJsonHour(JsonHour jsonHour) {
        // Getting instance from cache or creating it
        RecurringEvent event = (RecurringEvent) eventCache.get(jsonHour.getKey());
        if (event == null) {
            event = new RecurringEventImpl();
            eventCache.put(jsonHour.getKey(), event);
        }
        try {
            event.setEventType(EventType.valueOf(jsonHour.getType()));
        } catch (IllegalArgumentException | NullPointerException e) {
            Log.e(LOG_TAG, "Unknown event type '" + jsonHour.getType() + "'", e);
            event.setEventType(EventType.THEME);
        }
        event.setKey(jsonHour.getKey());
        event.setStartDate(new Date(jsonHour.getNextStart() * 1000));
        event.setEndDate(new Date(jsonHour.getNextEnd() * 1000));
        event.setStartHour(jsonHour.getStartHour());
        event.setStartMinute(jsonHour.getStartMinute());
        event.setEndMinute(jsonHour.getEndMinute());
        event.setEndHour(jsonHour.getEndHour());
        event.setMonday(jsonHour.isMonday());
        event.setTuesday(jsonHour.isTuesday());
        event.setWednesday(jsonHour.isWednesday());
        event.setThursday(jsonHour.isThursday());
        event.setFriday(jsonHour.isFriday());
        event.setSaturday(jsonHour.isSaturday());
        event.setSunday(jsonHour.isSunday());
        RecurrencyType type = RecurrencyType.EVERY;
        if (jsonHour.getRecurrency() != null) {
            switch (jsonHour.getRecurrency()) {
                default:
                case 0:
                    type = RecurrencyType.EVERY;
                    break;
                case 1:
                    type = RecurrencyType.FIRST;
                    break;
                case 2:
                    type = RecurrencyType.SECOND;
                    break;
                case 3:
                    type = RecurrencyType.THIRD;
                    break;
                case 4:
                    type = RecurrencyType.FOURTH;
                    break;

            }
            event.setRecurrencyType(type);
        }
        event.setDescription(jsonHour.getDescription());
        final JsonMedia media = jsonHour.getThumb();
        if (media != null) {
            final Image image = getImageFromJson(media);
            event.setThumb(image);
        }
        return event;
    }

    @Override
    public List<Place> listNearbyPlaces(User currentUser, double latitude,
                                        double longitude, String parentKey, String searchText,
                                        Integer radius) {
        final JsonNearbyPlacesResponse jsonResponse = webService.getPlaces(latitude,
                longitude, currentUser.getToken(), parentKey, radius,
                searchText);
        final List<JsonPlace> jsonPlaces = jsonResponse.getPlaces();
        if (jsonPlaces != null) {
            final List<Place> places = new ArrayList<Place>(jsonPlaces.size());
            final List<Event> deals = new ArrayList<Event>();
            final List<Event> themes = new ArrayList<Event>();

            // Processing places
            for (final JsonPlace json : jsonPlaces) {
                final Place place = getPlaceFromJson(json);
                if (place.getLatitude() != null && place.getLatitude() != 0
                        && place.getLongitude() != null
                        && place.getLongitude() != 0) {
                    places.add(place);

                    // Filling recurring events structures
                    for (RecurringEvent e : place.getRecurringEvents()) {
                        switch (e.getEventType()) {
                            case THEME:
                                themes.add(e);
                                break;
                            case HAPPY_HOUR:
                                deals.add(e);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            // Browsing users
            final List<JsonLightUser> jsonUsers = jsonResponse.getNearbyUsers();
            final List<User> users = new ArrayList<>(jsonUsers.size());
            for (final JsonLightUser jsonUser : jsonUsers) {
                final User user = getUserFromLightJson(jsonUser);
                users.add(user);
            }

            // Browsing events
            final List<JsonLightEvent> jsonEvents = jsonResponse.getNearbyEvents();
            final List<Event> events = new ArrayList<>(jsonEvents.size());
            for (JsonLightEvent jsonEvent : jsonEvents) {
                final Event event = getEventFromLightJson(jsonEvent);
                events.add(event);
            }
            events.addAll(themes);

            // Sorting events
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return lhs.getStartDate().compareTo(rhs.getStartDate());
                }
            });
            Collections.sort(deals, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return lhs.getStartDate().compareTo(rhs.getStartDate());
                }
            });
            // Assigning to our global context
            ContextHolder.places = places;
            ContextHolder.users = users;
            ContextHolder.events = events;
            ContextHolder.deals = deals;
            return places;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Event> listNearbyEvents(User currentUser, double latitude,
                                        double longitude) {
        final List<JsonLightEvent> jsonEvents = webService.getEvents(latitude,
                longitude, currentUser.getToken());
        final List<Event> events = new ArrayList<Event>();
        if (jsonEvents != null) {
            for (final JsonLightEvent jsonEvent : jsonEvents) {
                final Event event = getEventFromLightJson(jsonEvent);
                events.add(event);
            }
        }
        return events;
    }

    @Override
    public List<Event> getNearbyEvents(User currentUser, double latitude,
                                       double longitude) {
        if (nearbyEvents == null) {
            nearbyEvents = listNearbyEvents(currentUser, latitude, longitude);
        }
        return nearbyEvents;
    }

    @Override
    public void getOverviewData(User currentUser, CalObject object,
                                final OverviewListener listener) {
        if (!object.isOverviewDataLoaded()) {
            final Location loc = PelMelApplication.getLocalizationService()
                    .getLocation();
            Log.d("data", "getOverviewData for " + object.getKey());
            CalObject loadedObj = null;

            if (object instanceof Place) {
                // Loading a place
                final JsonPlaceOverview json = webService.getOverviewData(
                        JsonPlaceOverview.class, object.getKey(),
                        loc.getLatitude(), loc.getLongitude(),
                        currentUser.getToken());
                if (json != null) {
                    loadedObj = getPlaceFromJsonOverview(json);
                }
            } else if (object instanceof User) {
                // Loading a user
                final JsonUser json = webService.getOverviewData(
                        JsonUser.class, object.getKey(), loc.getLatitude(),
                        loc.getLongitude(), currentUser.getToken());
                if (json != null) {
                    loadedObj = getUserFromJson(json);
                }
            } else if (object instanceof Event) {
                // Loading the event
                final JsonEvent json = webService.getOverviewData(
                        JsonEvent.class, object.getKey(), loc.getLatitude(),
                        loc.getLongitude(), currentUser.getToken());
                if (json != null) {
                    loadedObj = getEventFromJson(json);
                }
            }

            // We flag that overview data is ready
            if (loadedObj != null) {
                loadedObj.setOverviewDataLoaded(true);

                // Notifying callback on UI thread
                final CalObject o = loadedObj;
                if(listener != null) {
                    listener.getContext().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            listener.overviewDataAvailable(o);
                        }
                    });
                }
            }
        }
    }

    public void setWebService(WebService webService) {
        this.webService = webService;
    }

    @Override
    public List<Place> getNearbyPlaces(User currentUser, double latitude,
                                       double longitude, String parentKey, String searchText,
                                       Integer radius, boolean forceRefresh) {
        if (parentKey != null) {
            return listNearbyPlaces(currentUser, latitude, longitude,
                    parentKey, searchText, radius);
        } else if (nearbyPlaces == null || forceRefresh) {
            nearbyPlaces = listNearbyPlaces(currentUser, latitude, longitude,
                    null, null, radius);
        }
        return nearbyPlaces;
    }

    @Override
    public void clearCache() {
        nearbyPlaces = null;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public void like(final User currentUser, final CalObject likedObject,
                     final LikeCallback callback) {
        new AsyncTask<Void, Void, JsonLikeInfo>() {
            @Override
            protected JsonLikeInfo doInBackground(Void... params) {
                final JsonLikeInfo likeInfo = webService.like(currentUser,
                        likedObject.getKey());
                return likeInfo;
            }

            ;

            @Override
            protected void onPostExecute(JsonLikeInfo result) {
                if (result != null) {
                    callback.liked(likedObject, result.getLikeCount(),
                            result.getDislikeCount());
                }
            }
        }.execute();

    }

    @Override
    public void saveProfile(User user, double latitude, double longitude) {

        final Map<String, String> params = new HashMap<String, String>();

        params.put("nxtpUserToken", user.getToken());
        params.put("userKey", user.getKey());
        params.put("name", user.getName());

        // Transforming birth date into params
        final Calendar cal = Calendar.getInstance();
        cal.setTime(user.getBirthDate());
        final int month = cal.get(Calendar.MONTH);
        final int year = cal.get(Calendar.YEAR);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        params.put("birthYYYY", String.valueOf(year));
        params.put("birthMM", String.valueOf(month+1));
        params.put("birthDD", String.valueOf(day+1));

        params.put("description", user.getDescription());
        if (user.getDescriptionKey() != null) {
            params.put("descriptionKey", user.getDescriptionKey());
        }
        params.put("descriptionLanguageCode", "en");
//        params.put("pseudo", user.getName());
        params.put("height", String.valueOf(user.getHeight()));
        params.put("weight", String.valueOf(user.getWeight()));
        params.put("lat", String.valueOf(latitude));
        params.put("lng", String.valueOf(longitude));


        // Building a param list because we have repeating keys (arrays) so we cannot use a map
        final List<String> paramsList = new ArrayList<>();

        for (final Tag t : user.getTags()) {
            paramsList.add("tags");
            paramsList.add(t.getCode());
        }

        for (final String key : params.keySet()) {
            final String value = params.get(key);
            paramsList.add(key);
            paramsList.add(value);
        }
        InputStream inputStream = null;
        try {
            Log.d(LOG_TAG, paramsList.toString());
            inputStream = webService.postRequest(new URL(WebService.BASE_URL
                    + "/mobileRegister"),paramsList.toArray(new String[paramsList.size()]));
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Exception while saving profile: " + e.getMessage(),
                    e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {

                }
            }
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CalObject getCalObject(String itemKey, final OverviewListener listener) {
        CalObject obj = null;
        boolean loadRequested = false;
        // Checking every cache and instantiating proper object based on key type
        if (itemKey.startsWith(Place.CAL_TYPE)) {
            // Place ?
            obj = placeCache.get(itemKey);
            if (obj == null) {
                obj = new PlaceImpl();
                obj.setKey(itemKey);
                placeCache.put(itemKey, (Place) obj);
                loadRequested = true;
            }
        } else if (itemKey.startsWith(User.CAL_TYPE)) {
            // User ?
            obj = userCache.get(itemKey);
            if (obj == null) {
                obj = new UserImpl();
                obj.setKey(itemKey);
                userCache.put(itemKey, (User) obj);
                loadRequested = true;
            }
        } else if (itemKey.startsWith(Event.CAL_TYPE)) {
            // Event ?
            obj = eventCache.get(itemKey);
            if (obj == null) {
                obj = new EventImpl();
                obj.setKey(itemKey);
                eventCache.put(itemKey, (Event) obj);
                loadRequested = true;
            }
        }
        if (loadRequested) {
            final CalObject finalObj = obj;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    final User user = userService.getLoggedUser();
                    getOverviewData(user, finalObj, listener);
                    return null;
                }
            }.execute();
        }

        return obj;
    }


    public void exportDatabase(Context context) {

        // init realm
        Realm realm = Realm.getInstance(context, PelMelApplication.getUserService().getLoggedUser().getKey());

        File exportRealmFile = null;
        try {
            // get or create an "export.realm" file
            exportRealmFile = new File(context.getExternalCacheDir(), "export.realm");

            // if "export.realm" already exists, delete
            exportRealmFile.delete();

            // copy current realm to "export.realm"
            realm.writeCopyTo(exportRealmFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        realm.close();

        // init email intent and add export.realm as attachment
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, "cfondacci@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Realm File");
        intent.putExtra(Intent.EXTRA_TEXT, "Realm file");
        Uri u = Uri.fromFile(exportRealmFile);
        intent.putExtra(Intent.EXTRA_STREAM, u);

        // start email intent
        context.startActivity(Intent.createChooser(intent, "YOUR CHOOSER TITLE"));
    }
}
