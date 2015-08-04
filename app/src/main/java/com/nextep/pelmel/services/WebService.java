package com.nextep.pelmel.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextep.json.model.impl.JsonCheckinResponse;
import com.nextep.json.model.impl.JsonLightEvent;
import com.nextep.json.model.impl.JsonLikeInfo;
import com.nextep.json.model.impl.JsonManyToOneMessageList;
import com.nextep.json.model.impl.JsonMessagingStatistic;
import com.nextep.json.model.impl.JsonNearbyPlacesResponse;
import com.nextep.json.model.impl.JsonOneToOneMessageList;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.gson.GsonHelper;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class WebService {

    public static final String LOG_TAG = "WebService";
    public static final String BASE_URL = "http://www.pelmelguide.com";
    //	public static final String BASE_URL = "http://127.0.0.1";
    //private static final String BASE_URL = "http://www.pelmelguide.com";
    private static final String LOGIN_ACTION = "/mobileLogin";
    private static final String PLACES_LIST_ACTION = "/mapPlaces";
    private static final String EVENTS_LIST_ACTION = "/mobileEvents";
    private static final String MESSAGES_LIST_ACTION = "/mobileMyMessages";
    private static final String CONVERSATION_LIST_ACTION = "/mobileMyMessagesReply";
    private static final String PLACES_OVERVIEW_ACTION = "/api/place";
    private static final String USERS_OVERVIEW_ACTION = "/api/user";
    private static final String EVENTS_OVERVIEW_ACTION = "/api/event";
    private static final String LIKE_ACTION = "/mobileIlike";
    private static final String CHECKIN_ACTION = "/mobileCheckin";

    Gson gson;

    public WebService() {
        gson = GsonHelper.getGson();
    }

    public InputStream sendRequest(URL url) throws Exception {
        InputStream is = null;
        try {
            // Opening connection
            final HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.connect();

            // Checking if we're OK status
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = urlConnection.getInputStream();
            }
        } catch (final Exception e) {
            throw new Exception("Cannot connect to PelMel server : "
                    + e.getMessage(), e);
        }

        return is;

    }

    public JsonUser connect(String login, String password) {
        JsonUser user = null;
        try {
            final InputStream is = sendRequest(new URL(BASE_URL + LOGIN_ACTION
                    + "?email=" + login + "&password=" + password));
            if (is != null) {
                final InputStreamReader reader = new InputStreamReader(is);
                user = gson.fromJson(reader, new TypeToken<JsonUser>() {
                }.getType());
                PelMelApplication.getUiService().setUnreadMessagesCount(
                        user.getUnreadMsgCount());
            }
        } catch (final MalformedURLException e) {
            Log.e("Login", "Unable to login: " + e.getMessage());
        } catch (final Exception e) {
            Log.e("Login", "Exception during login : " + e.getMessage());
        }
        return user;
    }

    public JsonNearbyPlacesResponse getPlaces(double latitude, double longitude,
                                              String token, String parentKey, Integer radius, String searchText) {
        try {
            // querying places
            final InputStream inputStream = sendRequest(new URL(BASE_URL
                    + PLACES_LIST_ACTION + "?lat=" + latitude + "&lng="
                    + longitude + "&nxtpUserToken=" + token
                    + (parentKey == null ? "" : "&parentKey=" + parentKey)
                    + (radius == null ? "" : "&radius=" + radius)
                    + (searchText == null ? "" : "&searchText=" + searchText)));

            // If we got something
            if (inputStream != null) {
                // Reading stream
                final InputStreamReader reader = new InputStreamReader(
                        inputStream);

                // Unwrapping JSON
                final JsonNearbyPlacesResponse response = gson.fromJson(reader,
                        new TypeToken<JsonNearbyPlacesResponse>() {
                        }.getType());
                if (response != null) {
                    return response;
                }
            }

        } catch (final Exception e) {
            Log.e("Web.getPlaces",
                    "Failed to get a list of places : " + e.getMessage(), e);
        }
        return null;
    }

    public List<JsonLightEvent> getEvents(double latitude, double longitude,
                                          String token) {
        try {
            // querying places
            final InputStream inputStream = sendRequest(new URL(BASE_URL
                    + EVENTS_LIST_ACTION + "?lat=" + latitude + "&lng="
                    + longitude + "&nxtpUserToken=" + token));

            // If we got something
            if (inputStream != null) {
                // Reading stream
                final InputStreamReader reader = new InputStreamReader(
                        inputStream);

                // Unwrapping JSON
                final List<JsonLightEvent> response = gson.fromJson(reader,
                        new TypeToken<List<JsonLightEvent>>() {
                        }.getType());
                if (response != null) {
                    return response;
                }
            }

        } catch (final Exception e) {
            Log.e("Web.getEvents",
                    "Failed to get a list of events: " + e.getMessage(), e);
        }
        return null;
    }

    public <T> T getOverviewData(Class<T> returnType, String key,
                                 double latitude, double longitude, String token) {
        try {
            String actionUrl = null;
            if (key.startsWith(Place.CAL_TYPE)) {
                actionUrl = PLACES_OVERVIEW_ACTION;
            } else if (key.startsWith(User.CAL_TYPE)) {
                actionUrl = USERS_OVERVIEW_ACTION;
            } else if (key.startsWith(Event.CAL_TYPE)) {
                actionUrl = EVENTS_OVERVIEW_ACTION;
            }
            // querying places
            final InputStream inputStream = sendRequest(new URL(BASE_URL
                    + actionUrl + "?id=" + key + "&lat="
                    + latitude + "&lng=" + longitude + "&nxtpUserToken="
                    + token));

            // If we got something
            if (inputStream != null) {
                // Reading stream
                final InputStreamReader reader = new InputStreamReader(
                        inputStream);

                // Unwrapping JSON
                final T response = gson.fromJson(reader, returnType);
                if (response instanceof JsonMessagingStatistic) {
                    PelMelApplication.getUiService().setUnreadMessagesCount(
                            ((JsonMessagingStatistic) response)
                                    .getUnreadMsgCount());
                }
                return response;
            }

        } catch (final Exception e) {
            Log.e("Web.getOverviewData",
                    "Failed to get overview data: " + e.getMessage(), e);
        }
        return null;
    }

    public JsonLikeInfo like(User user, String parentKey) {
        return like(user, parentKey, true);
    }

    public JsonLikeInfo unlike(User user, String parentKey) {
        return like(user, parentKey, false);
    }

    public JsonLikeInfo like(User user, String parentKey, boolean isLiked) {
        try {
            // querying places
            final InputStream inputStream = sendRequest(new URL(BASE_URL
                    + LIKE_ACTION + "?id=" + parentKey + "&nxtpUserToken="
                    + user.getToken() + "&type=" + (isLiked ? "1" : "-1")));

            // If we got something
            if (inputStream != null) {
                // Reading stream
                final InputStreamReader reader = new InputStreamReader(
                        inputStream);

                // Unwrapping JSON
                final JsonLikeInfo response = gson.fromJson(reader,
                        JsonLikeInfo.class);
                return response;
            }

        } catch (final Exception e) {
            Log.e(LOG_TAG,
                    "Failed to like '" + parentKey + "': " + e.getMessage(), e);
        }
        return null;
    }

    public JsonManyToOneMessageList getMessages(User user, double latitude,
                                                double longitude, int maxMessageId) {
        try {
            // querying places
            final InputStream inputStream = sendRequest(new URL(BASE_URL
                    + MESSAGES_LIST_ACTION + "?nxtpUserToken="
                    + user.getToken() + "&lat=" + latitude + "&lng="
                    + longitude + "&fromMessageId=" + maxMessageId));

            // If we got something
            if (inputStream != null) {
                // Reading stream
                final InputStreamReader reader = new InputStreamReader(
                        inputStream);

                // Unwrapping JSON
                final JsonManyToOneMessageList response = gson.fromJson(reader,
                        JsonManyToOneMessageList.class);
                PelMelApplication.getUiService().setUnreadMessagesCount(
                        response.getUnreadMsgCount());
                return response;
            }

        } catch (final Exception e) {
            Log.e(LOG_TAG, "Failed to get messages : " + e.getMessage(), e);
        }
        return null;
    }

    public JsonOneToOneMessageList getMessages(User user, String otherUserKey,
                                               double latitude, double longitude) {
        try {
            // querying places
            final InputStream inputStream = sendRequest(new URL(BASE_URL
                    + CONVERSATION_LIST_ACTION + "?nxtpUserToken="
                    + user.getToken() + "&from=" + otherUserKey + "&lat="
                    + latitude + "&lng=" + longitude));

            // If we got something
            if (inputStream != null) {
                // Reading stream
                final InputStreamReader reader = new InputStreamReader(
                        inputStream);

                // Unwrapping JSON
                final JsonOneToOneMessageList response = gson.fromJson(reader,
                        JsonOneToOneMessageList.class);
                PelMelApplication.getUiService().setUnreadMessagesCount(
                        response.getUnreadMsgCount());
                return response;
            }

        } catch (final Exception e) {
            Log.e(LOG_TAG, "Failed to get conversation : " + e.getMessage(), e);
        }
        return null;
    }

    public JsonCheckinResponse checkInOrOut(User user, Place place, double latitude, double longitude, boolean isCheckout) {
        try {
            final InputStream inputStream = sendRequest(new URL(BASE_URL + CHECKIN_ACTION
                    + "?nxtpUserToken=" + user.getToken()
                    + "&checkInKey=" + place.getKey()
                    + "&lat=" + latitude
                    + "&lng=" + longitude
                    + (isCheckout ? "&checkout=true" : "")));
            if(inputStream != null) {
                final InputStreamReader reader = new InputStreamReader(inputStream);

                final JsonCheckinResponse checkinResponse = gson.fromJson(reader,JsonCheckinResponse.class);
                return checkinResponse;
            }
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Failed to checkin to " + place.getKey() + ": " + e.getMessage(), e);
        }
        return null;
    }
}
