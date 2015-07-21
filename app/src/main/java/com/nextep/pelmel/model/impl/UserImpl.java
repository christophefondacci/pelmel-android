package com.nextep.pelmel.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.base.AbstractCalObject;

public class UserImpl extends AbstractCalObject implements User {

	private Date birthDate;
	private boolean online;
	private Place lastLocation;
	private Date lastLocationTime;
	private final List<Place> likedPlaces = new ArrayList<Place>();
	private final List<User> likedUsers = new ArrayList<User>();
	private int heightInCm = 170;
	private int weightInKg = 70;
	private String token;
	private String descriptionKey;

	@Override
	public Date getBirthDate() {
		return birthDate;
	}

	@Override
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public List<Place> getLikedPlaces() {
		return likedPlaces;
	}

	@Override
	public void setLikedPlaces(List<Place> likedPlaces) {
		this.likedPlaces.clear();
		this.likedPlaces.addAll(likedPlaces);
	}

	@Override
	public List<User> getLikedUsers() {
		return likedUsers;
	}

	@Override
	public void setLikedUsers(List<User> likedUsers) {
		this.likedUsers.clear();
		this.likedUsers.addAll(likedUsers);
	}

	@Override
	public void setLastLocation(Place lastLocation) {
		this.lastLocation = lastLocation;
	}

	@Override
	public Place getLastLocation() {
		return lastLocation;
	}

	@Override
	public void setLastLocationTime(Date lastLocationTime) {
		this.lastLocationTime = lastLocationTime;
	}

	@Override
	public Date getLastLocationTime() {
		return lastLocationTime;
	}

	@Override
	public void setHeight(int heightInCm) {
		this.heightInCm = heightInCm;
	}

	@Override
	public int getHeight() {
		return heightInCm;
	}

	@Override
	public void setWeight(int weightInKg) {
		this.weightInKg = weightInKg;
	}

	@Override
	public int getWeight() {
		return weightInKg;
	}

	@Override
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public String getDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}
}
