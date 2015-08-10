package com.nextep.pelmel;

public interface PelMelConstants {

	String OVERVIEW_OBJECT = "ov_object";

	int TAB_PLACES = 0;
	int TAB_MAP = 1;
	int TAB_EVENTS = 2;
	int TAB_CHAT = 3;
	int TAB_SETTINGS = 4;
	String PREFS_NAME = "LOGIN_PREFS";
	String PREF_USERNAME = "USERNAME";
	String PREF_PASSWORD = "PASSWORD";
	String PREF_SEARCH_RADIUS = "RADIUS";

	String PLACE_TYPE_BAR = "bar";
	String PLACE_TYPE_CLUB = "club";
	String PLACE_TYPE_SEXCLUB = "sexclub";
	String PLACE_TYPE_SHOP = "sexshop";
	String PLACE_TYPE_RESTAURANT = "restaurant";
	String PLACE_TYPE_ASSOCIATION = "asso";
	String PLACE_TYPE_HOTEL = "hotel";
	String PLACE_TYPE_SAUNA = "sauna";
	String PLACE_TYPE_OUTDOORS= "outdoors";


	double MAP_USERLOCATION_RADIUS = 1000;
	int MAP_MINIMUM_PLACES_FOR_ZOOM = 3;


	double CHECKIN_DISTANCE = 1.0f;
	int CHECKIN_TIMEOUT_MILLISECS = 10800000;

	String INTENT_PARAM_INDEX = "index";
	String INTENT_PARAM_CAL_KEY = "calkey";
	String INTENT_PARAM_SHOW_MESSAGES = "openMessages";

	String PREF_KEY_PUSH_TOKEN = "androidPushToken";
}
