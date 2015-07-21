package com.nextep.pelmel.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.base.AbstractCalObject;

public class EventImpl extends AbstractCalObject implements Event {

	private Date startDate;
	private Date endDate;
	private Place place;
	private List<User> comers = new ArrayList<User>();

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public Place getPlace() {
		return place;
	}

	@Override
	public void setPlace(Place place) {
		this.place = place;
	}

	@Override
	public void setComers(List<User> comers) {
		this.comers = comers;
	}

	@Override
	public void addComer(User comer) {
		this.comers.add(comer);
	}

	@Override
	public List<User> getComers() {
		return comers;
	}
}
