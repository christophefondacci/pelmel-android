package com.nextep.pelmel.model;

import java.util.Date;
import java.util.List;

public interface Event extends CalObject {

	Date getStartDate();

	void setStartDate(Date startDate);

	Date getEndDate();

	void setEndDate(Date endDate);

	Place getPlace();

	void setPlace(Place place);

	List<User> getComers();

	void setComers(List<User> comers);

	void addComer(User comer);
}
