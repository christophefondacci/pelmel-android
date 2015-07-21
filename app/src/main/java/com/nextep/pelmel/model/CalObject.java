package com.nextep.pelmel.model;

import java.util.List;

public interface CalObject extends Keyed, Localized {

	String getName();

	void setName(String name);

	List<Image> getImages();

	void setImages(List<Image> images);

	void addImage(Image image);

	void removeImage(Image image);

	String getDescription();

	void setDescription(String description);

	boolean isOverviewDataLoaded();

	void setOverviewDataLoaded(boolean isLoaded);

	int getLikeCount();

	void setLikeCount(int likeCount);

	List<Tag> getTags();

	void setTags(List<Tag> tags);

	void addTag(Tag tag);

	void removeTag(Tag tag);

	Image getThumb();
}
