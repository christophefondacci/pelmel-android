package com.nextep.pelmel.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.nextep.json.model.IJsonLightEvent;
import com.nextep.json.model.impl.JsonDescription;
import com.nextep.json.model.impl.JsonEvent;
import com.nextep.json.model.impl.JsonLightEvent;
import com.nextep.json.model.impl.JsonLightPlace;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonLikeInfo;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.json.model.impl.JsonOverviewElement;
import com.nextep.json.model.impl.JsonPlace;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.cache.model.Cache;
import com.nextep.pelmel.cache.model.impl.MemorySizedTTLCacheImpl;
import com.nextep.pelmel.listeners.LikeCallback;
import com.nextep.pelmel.listeners.OverviewListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.impl.EventImpl;
import com.nextep.pelmel.model.impl.ImageImpl;
import com.nextep.pelmel.model.impl.PlaceImpl;
import com.nextep.pelmel.model.impl.UserImpl;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.TagService;
import com.nextep.pelmel.services.UserService;
import com.nextep.pelmel.services.WebService;

public class DataServiceImpl implements DataService {

	private final static String LOG_TAG = "DataService";
	private final Cache<Place> placeCache;
	private final Cache<User> userCache;
	private final Cache<Image> imageCache;
	private final Cache<Event> eventCache;
	private List<Place> nearbyPlaces = null;
	private List<Event> nearbyEvents = null;
	private WebService webService;
	private TagService tagService;
	private UserService userService;

	public DataServiceImpl() {
		placeCache = new MemorySizedTTLCacheImpl<Place>(600000, 300);
		userCache = new MemorySizedTTLCacheImpl<User>(600000, 300);
		eventCache = new MemorySizedTTLCacheImpl<Event>(600000, 300);
		imageCache = new MemorySizedTTLCacheImpl<Image>(600000, 300);
	}

	@Override
	public Place getPlaceFromJson(JsonPlace json) {
		Place place = placeCache.get(json.getItemKey());
		if (place == null) {
			place = new PlaceImpl();
			placeCache.put(json.getItemKey(), place);
		}
		place.setKey(json.getItemKey());
		place.setAddress(json.getAddress());
		place.setDescription(json.getDescription());
		place.setDistance(json.getRawDistance());
		place.setDistanceLabel(json.getDistance());
		place.setLatitude(json.getLat());
		place.setLongitude(json.getLng());
		place.setLikeCount(json.getLikesCount());
		place.setName(json.getName());
		place.setType(json.getType());

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

		return place;
	}

	@Override
	public Place getPlaceFromLightJson(JsonLightPlace json) {
		if (json != null) {
			final String key = json.getKey();
			Place place = placeCache.get(key);
			if (place == null) {
				place = new PlaceImpl();
				place.setKey(key);
				placeCache.put(key, place);
			}
			place.setName(json.getName());
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

		// Updating last localisation
		final Place lastLocation = getPlaceFromLightJson(json.getLastLocation());
		user.setLastLocation(lastLocation);
		final Long locTime = json.getLastLocationTime();
		if (locTime != null) {
			user.setLastLocationTime(new Date(json.getLastLocationTime() * 1000));
		}

		// Converting descriptions
		final StringBuilder buf = new StringBuilder();
		for (final JsonDescription d : json.getDescriptions()) {
			if (user.getDescriptionKey() == null) {
				user.setDescriptionKey(d.getKey());
			}
			buf.append(d.getDescription() + "\n");
		}
		user.setDescription(buf.toString());

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
	public User getUserFromLightJson(JsonLightUser json) {
		final String key = json.getKey();
		final User user = getUser(key);
		user.setName(json.getPseudo());
		user.setOnline(json.isOnline());

		// Getting thumb
		final JsonMedia jsonMedia = json.getThumb();
		if (user.getImages().isEmpty()) {
			final Image thumb = getImageFromJson(jsonMedia);
			user.addImage(thumb);
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
		final JsonLightPlace jsonPlace = json.getPlace();
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
	public Place getPlaceFromJsonOverview(JsonOverviewElement json) {
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

		return place;
	}

	@Override
	public List<Place> listNearbyPlaces(User currentUser, double latitude,
			double longitude, String parentKey, String searchText,
			Integer radius) {
		final List<JsonPlace> jsonPlaces = webService.getPlaces(latitude,
				longitude, currentUser.getToken(), parentKey, radius,
				searchText);
		if (jsonPlaces != null) {
			final List<Place> places = new ArrayList<Place>(jsonPlaces.size());
			for (final JsonPlace json : jsonPlaces) {
				final Place place = getPlaceFromJson(json);
				if (place.getLatitude() != null && place.getLatitude() != 0
						&& place.getLongitude() != null
						&& place.getLongitude() != 0) {
					places.add(place);
				}
			}
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
				final JsonOverviewElement json = webService.getOverviewData(
						JsonOverviewElement.class, object.getKey(),
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
				listener.getContext().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						listener.overviewDataAvailable(o);
					}
				});
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
			};

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
		params.put("birthMM", String.valueOf(month));
		params.put("birthDD", String.valueOf(day));

		params.put("description", user.getDescription());
		if (user.getDescriptionKey() != null) {
			params.put("descritionKey", user.getDescriptionKey());
		}
		params.put("descriptionLanguageCode", "en");
		params.put("pseudo", user.getName());
		params.put("height", String.valueOf(user.getHeight()));
		params.put("weight", String.valueOf(user.getWeight()));
		params.put("lat", String.valueOf(latitude));
		params.put("lng", String.valueOf(longitude));

		final StringBuilder buf = new StringBuilder();
		for (final Tag t : user.getTags()) {
			buf.append("&tags=" + t.getCode());
		}

		final StringBuilder paramsBuf = new StringBuilder();
		String sep = "?";
		for (final String key : params.keySet()) {
			final String value = params.get(key);
			paramsBuf.append(sep + key + "=" + value);
			sep = "&";
		}
		paramsBuf.append(buf);
		InputStream inputStream = null;
		try {
			Log.d(LOG_TAG, paramsBuf.toString());
			inputStream = webService.sendRequest(new URL(WebService.BASE_URL
					+ "/mobileRegister" + paramsBuf.toString()));
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
}
