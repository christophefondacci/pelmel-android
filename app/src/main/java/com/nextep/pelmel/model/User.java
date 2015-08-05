package com.nextep.pelmel.model;

import java.util.Date;
import java.util.List;

public interface User extends CalObject {

	String CAL_TYPE = "USER";
	Date getBirthDate();

	void setBirthDate(Date birthDate);

	boolean isOnline();

	void setOnline(boolean online);

	List<Place> getLikedPlaces();

	void setLikedPlaces(List<Place> places);

	List<User> getLikedUsers();

	void setLikedUsers(List<User> users);

	Place getLastLocation();

	void setLastLocation(Place place);

	Date getLastLocationTime();

	void setLastLocationTime(Date lastLocationTime);

	void setHeight(int heightInCm);

	int getHeight();

	void setWeight(int weightInKg);

	int getWeight();

	String getToken();

	void setToken(String token);

	String getCityName();
	void setCityName(String cityName);

	double getRawDistanceMiles();
	void setRawDistanceMiles(double miles);
}
