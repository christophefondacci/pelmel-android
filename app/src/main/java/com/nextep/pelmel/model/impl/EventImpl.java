package com.nextep.pelmel.model.impl;

import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.base.AbstractCalObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventImpl extends AbstractCalObject implements Event {

	private Date startDate;
	private Date endDate;
	private Place place;
	private List<User> comers = new ArrayList<User>();
	private int comersCount;
	private int reviewsCount;

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

	@Override
	public void setReviewsCount(int reviewsCount) {
		this.reviewsCount = reviewsCount;
	}

	@Override
	public int getReviewsCount() {
		return reviewsCount;
	}

	@Override
	public int getComersCount() {
		return getLikeCount();
	}

	@Override
	public void setComersCount(int comersCount) {
		setLikeCount(comersCount);
	}
}
