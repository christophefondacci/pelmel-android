package com.nextep.pelmel.model.base;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.Tag;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCalObject implements CalObject {

	private String key;
	private List<Image> images = new ArrayList<Image>();
	private String name;
	private int likeCount;
	private Double latitude;
	private Double longitude;
	private String description;
	private double distance;
	private String distanceLabel;
	private boolean overviewDataLoaded = false;
	private boolean liked;
	private List<Tag> tags = new ArrayList<Tag>();

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public List<Image> getImages() {
		return images;
	}

	@Override
	public void setImages(List<Image> images) {
		this.images = images;
	}

	@Override
	public void addImage(Image image) {
		this.images.add(image);
	}

	@Override
	public void removeImage(Image image) {
		this.images.remove(image);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getLikeCount() {
		return likeCount;
	}

	@Override
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	@Override
	public Double getLatitude() {
		return latitude;
	}

	@Override
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Override
	public Double getLongitude() {
		return longitude;
	}

	@Override
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean isOverviewDataLoaded() {
		return overviewDataLoaded;
	}

	@Override
	public void setOverviewDataLoaded(boolean overviewDataLoaded) {
		this.overviewDataLoaded = overviewDataLoaded;
	}

	@Override
	public String getDistanceLabel() {
		return distanceLabel;
	}

	@Override
	public void setDistanceLabel(String distanceLabel) {
		this.distanceLabel = distanceLabel;
	}

	@Override
	public double getDistance() {
		return distance;
	}

	@Override
	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@Override
	public List<Tag> getTags() {
		return tags;
	}

	@Override
	public void addTag(Tag tag) {
		this.tags.add(tag);
	}

	@Override
	public void removeTag(Tag tag) {
		this.tags.remove(tag);
	}

	@Override
	public Image getThumb() {
		if (!images.isEmpty()) {
			return images.get(0);
		}
		return null;
	}

	@Override
	public void setThumb(Image thumb) {
		// Removing thumb
		if (!images.isEmpty()) {
			images.remove(0);
		}
		// Replacing
		images.add(0, thumb);
	}

	@Override
	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	@Override
	public boolean isLiked() {
		return liked;
	}
}
