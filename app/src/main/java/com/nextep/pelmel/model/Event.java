package com.nextep.pelmel.model;

import java.util.Date;
import java.util.List;

public interface Event extends CalObject {

	String CAL_TYPE = "EVNT";
	Date getStartDate();

	void setStartDate(Date startDate);

	Date getEndDate();

	void setEndDate(Date endDate);

	Place getPlace();

	void setPlace(Place place);

	List<User> getComers();

	void setComers(List<User> comers);

	void addComer(User comer);

	void setComersCount(int comersCount);
	int getComersCount();

	int getReviewsCount();
	void setReviewsCount(int reviewsCount);
}
