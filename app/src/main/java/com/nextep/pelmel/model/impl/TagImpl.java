package com.nextep.pelmel.model.impl;

import com.nextep.pelmel.model.Tag;

public class TagImpl implements Tag {

	private final String code;
	private final String label;

	public TagImpl(String code, String label) {
		this.code = code;
		this.label = label;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Tag) {
			return ((Tag) o).getCode().equals(getCode());
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}

}
