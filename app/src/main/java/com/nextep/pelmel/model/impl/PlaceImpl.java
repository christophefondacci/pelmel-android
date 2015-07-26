package com.nextep.pelmel.model.impl;

import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.RecurringEvent;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.base.AbstractCalObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlaceImpl extends AbstractCalObject implements Place {

	private String type;
	private String address;
	private String cityName;
	private int insidersCount;
	private final List<Event> events = new ArrayList<Event>();
	private final List<User> likers = new ArrayList<User>();
	private final List<User> insiders = new ArrayList<User>();
	private Collection<RecurringEvent> recurringEvents = new ArrayList<>();

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void setEvents(List<Event> events) {
		this.events.clear();
		this.events.addAll(events);
	}

	@Override
	public List<Event> getEvents() {
		return events;
	}

	@Override
	public void addEvent(Event event) {
		this.events.add(event);
	}

	@Override
	public void setLikers(List<User> likers) {
		this.likers.clear();
		this.likers.addAll(likers);
	}

	@Override
	public List<User> getLikers() {
		return likers;
	}

	@Override
	public void addLiker(User liker) {
		likers.add(liker);
	}

	@Override
	public List<User> getInsiders() {
		return insiders;
	}

	@Override
	public void setInsiders(List<User> insiders) {
		this.insiders.clear();
		this.insiders.addAll(insiders);
	}

	@Override
	public void addInsider(User insider) {
		this.insiders.add(insider);
	}

	@Override
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public String getCityName() {
		return cityName;
	}

	public void setRecurringEvents(Collection<RecurringEvent> recurringEvents) {
		this.recurringEvents = recurringEvents;
	}

	@Override
	public Collection<RecurringEvent> getRecurringEvents() {
		return recurringEvents;
	}

	@Override
	public void addRecurringEvent(RecurringEvent recurringEvent) {
		recurringEvents.add(recurringEvent);
	}

	@Override
	public int getInsidersCount() {
		return insidersCount;
	}

	@Override
	public void setInsidersCount(int insidersCount) {
		this.insidersCount = insidersCount;
	}
}
