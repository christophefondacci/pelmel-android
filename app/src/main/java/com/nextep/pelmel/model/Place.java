package com.nextep.pelmel.model;

import java.util.Collection;
import java.util.List;

public interface Place extends CalObject {

	String CAL_TYPE = "PLAC";

	String PLACE_TYPE_ASSOCIATION = "asso";
	String PLACE_TYPE_BAR = "bar";
	String PLACE_TYPE_CLUB = "club";
	String PLACE_TYPE_RESTAURANT = "restaurant";
	String PLACE_TYPE_SAUNA = "sauna";
	String PLACE_TYPE_SEXCLUB = "sexclub";
	String PLACE_TYPE_SHOP = "sexshop";
	String PLACE_TYPE_HOTEL = "hotel";
	String PLACE_TYPE_OUTDOORS = "outdoors";

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

	boolean isLiked();
	void setLiked(boolean liked);
	List<User> getInsiders();

	void setInsiders(List<User> insiders);

	int getInsidersCount();
	void setInsidersCount(int count);
	void addInsider(User insider);

	int getReviewsCount();
	void setReviewsCount(int reviewsCount);
	String getCityName();

	void setCityName(String cityName);

	Collection<RecurringEvent> getRecurringEvents();

	void setRecurringEvents(Collection<RecurringEvent> recurringEvents);

	void addRecurringEvent(RecurringEvent recurringEvent);
}
