package com.nextep.pelmel.model;

import java.util.List;

public interface Place extends CalObject {

	String PLACE_TYPE_ASSOCIATION = "asso";
	String PLACE_TYPE_BAR = "bar";
	String PLACE_TYPE_CLUB = "club";
	String PLACE_TYPE_RESTAURANT = "restaurant";
	String PLACE_TYPE_SAUNA = "sauna";
	String PLACE_TYPE_SEXCLUB = "sexclub";
	String PLACE_TYPE_SHOP = "sexshop";

	String getType();

	void setType(String Type);

	String getAddress();

	void setAddress(String address);

	List<Event> getEvents();

	void setEvents(List<Event> events);

	void addEvent(Event event);

	List<User> getLikers();

	void setLikers(List<User> users);

	void addLiker(User user);

	List<User> getInsiders();

	void setInsiders(List<User> insiders);

	void addInsider(User insider);

}
