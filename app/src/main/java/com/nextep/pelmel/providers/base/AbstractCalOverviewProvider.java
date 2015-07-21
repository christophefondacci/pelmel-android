package com.nextep.pelmel.providers.base;

import java.util.List;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.providers.OverviewProvider;

public abstract class AbstractCalOverviewProvider implements OverviewProvider {

	private final CalObject object;

	public AbstractCalOverviewProvider(CalObject object) {
		this.object = object;
	}

	@Override
	public String getTitle() {
		return object.getName();
	}

	@Override
	public String getDescription() {
		return object.getDescription();
	}

	@Override
	public int getLikes() {
		return object.getLikeCount();
	}

	@Override
	public CalObject getOverviewObject() {
		return object;
	}

	@Override
	public List<Tag> getTags() {
		return object.getTags();
	}
}
