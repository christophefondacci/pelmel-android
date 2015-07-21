package com.nextep.pelmel.model;

public interface Localized {

	Double getLatitude();

	Double getLongitude();

	void setLatitude(Double latitude);

	void setLongitude(Double longitude);

	String getDistanceLabel();

	void setDistanceLabel(String distanceLabel);

	double getDistance();

	void setDistance(double distance);
}
