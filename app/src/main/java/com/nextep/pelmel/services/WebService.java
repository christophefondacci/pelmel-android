package com.nextep.pelmel.services;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
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
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.exception.PelmelException;
import com.nextep.pelmel.gson.GsonHelper;
import com.nextep.pelmel.helpers.Devices;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.RecurringEvent;
import com.nextep.pelmel.model.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebService {

    public static final String LOG_TAG = "WebService";
    public static final String BASE_URL = "http://www.pelmelguide.com";
//    public static final String BASE_URL = "http://10.0.0.2";
//    public static final String BASE_URL = "http://www.pelmelguide.com";
    private static final String LOGIN_ACTION = "/mobileLogin";
    private static final String PLACES_LIST_ACTION = "/mapPlaces";
    private static final String EVENTS_LIST_ACTION = "/mobileEvents";
    private static final String MESSAGES_LIST_ACTION = "/mobileMyMessages";
    private static final String REVIEWS_LIST_ACTION = "/mobileComments";
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

    public InputStream postRequest(URL url, String... keyValueParams) throws PelmelException {
        return postRequest(url, Arrays.asList(keyValueParams));
    }
    public InputStream postRequest(URL url, Map<String, String> params) throws PelmelException {
        final List<String> keyValueParams = new ArrayList<>();
        for(String key : params.keySet()) {
            final String value = params.get(key);
            keyValueParams.add(key);
            keyValueParams.add(value);
        }
        return postRequest(url,keyValueParams);
    }

    public InputStream postRequest(URL url, List<String> params) throws PelmelException {
        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000);
//            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            // Checking if we're OK status
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
            }
        } catch(IOException e) {
            throw new PelmelException("Cannot connect to PelMel server : "
                    + e.getMessage(), e);
        }
        return is;
    }

    private String getQuery(List<String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (int i = 0 ; i < params.size(); i = i+2)
        {
            if (first)
                first = false;
            else
                result.append("&");

            final String key = params.get(i);
            final String value = params.get(i+1);
            if(value!=null) {
                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value, "UTF-8"));
            }

        }

        return result.toString();
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
            // Preparing parameters
            final Map<String,String> params = new HashMap<>();
            params.put("email", login);
            params.put("password", password);

            // Getting push token if any
            final SharedPreferences preferences = PelMelApplication.getInstance()
                    .getSharedPreferences(PelMelConstants.PREFS_NAME, 0);
            final String pushToken = preferences.getString(PelMelConstants.PREF_KEY_PUSH_TOKEN, null);
            if(pushToken != null) {
                params.put("pushDeviceId",pushToken);
                params.put("pushProvider","ANDROID");
            }

            String deviceInfo = null;
            try {
                final PackageInfo pInfo = PelMelApplication.getInstance().getPackageManager().getPackageInfo(PelMelApplication.getInstance().getPackageName(), 0);
                final String version = pInfo.versionName;
                final int versionCode = pInfo.versionCode;
                final String deviceName = Devices.getDeviceName();

                deviceInfo = version + ";" + String.valueOf(versionCode) + ";ANDROID/" + deviceName + ";" + Build.VERSION.SDK_INT;
                params.put("deviceInfo",deviceInfo);
            } catch(PackageManager.NameNotFoundException e) {
                Log.e(LOG_TAG,"Unable to get device information: " + e.getMessage(),e);
            }

            final InputStream is = postRequest(new URL(BASE_URL + LOGIN_ACTION), params);
            if (is != null) {
                final InputStreamReader reader = new InputStreamReader(is);
                user = gson.fromJson(reader, new TypeToken<JsonUser>() {
                }.getType());
                PelMelApplication.getUiService().setUnreadMessagesCount(
                        user.getUnreadMsgCount());
            }
        } catch (final MalformedURLException | PelmelException e) {
            Log.e("Login", "Unable to login: " + e.getMessage());
        }
        return user;
    }

    public JsonNearbyPlacesResponse getPlaces(double latitude, double longitude,
                                              String token, String parentKey, Integer radius, String searchText) {
        try {
            final Map<String, String> params = new HashMap<>();
            final LocalizationService localizationService = PelMelApplication.getLocalizationService();
            final Location currentLocation = localizationService.getLocation();
            params.put("lat",String.valueOf(currentLocation.getLatitude()));
            params.put("lng",String.valueOf(currentLocation.getLongitude()));
            params.put("searchLat",String.valueOf(latitude));
            params.put("searchLng",String.valueOf(longitude));
            params.put("nxtpUserToken",token);
            if(parentKey!=null) {
                params.put("parentKey",parentKey);
            }
            if(radius != null) {
                params.put("radius",String.valueOf(radius));
            }
            if(searchText!=null){
                params.put("searchText",searchText);
            }
            // querying places
            final InputStream inputStream = postRequest(new URL(BASE_URL
                    + PLACES_LIST_ACTION),params);

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
            } else if (key.startsWith(Event.CAL_TYPE) || key.startsWith(RecurringEvent.CAL_TYPE_RECURRING)) {
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
    public JsonManyToOneMessageList getReviewsAsMessages(User user, String forObjectItemKey, double latitude,
                                                double longitude, int page) {
        try {
            // querying places
            final InputStream inputStream = postRequest(new URL(BASE_URL
                            + REVIEWS_LIST_ACTION),
                    "nxtpUserToken", user.getToken(),
                    "lat", String.valueOf(latitude),
                    "lng", String.valueOf(longitude),
                    "id", forObjectItemKey,
                    "page", String.valueOf(page));

            // If we got something
            if (inputStream != null) {
                // Reading stream
                final InputStreamReader reader = new InputStreamReader(
                        inputStream);

                // Unwrapping JSON
                final JsonManyToOneMessageList response = gson.fromJson(reader,
                        JsonManyToOneMessageList.class);
                return response;
            }

        } catch (final Exception e) {
            Log.e(LOG_TAG, "Failed to get messages : " + e.getMessage(), e);
        }
        return null;
    }

    public JsonOneToOneMessageList getMessages(User user, String otherUserKey,
                                               double latitude, double longitude, boolean markReadOnly) {
        try {
            // querying places
            final InputStream inputStream = postRequest(new URL(BASE_URL
                            + CONVERSATION_LIST_ACTION),
                    "nxtpUserToken", user.getToken(),
                    "from", otherUserKey,
                    "lat", String.valueOf(latitude),
                    "lng", String.valueOf(longitude),
                    "markUnreadOnly", String.valueOf(markReadOnly));

            // If we got something
            if (inputStream != null && ! markReadOnly) {
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
