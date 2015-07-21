package com.nextep.pelmel.model;

/**
 * An interface for refreshable elements
 * 
 * @author cfondacci
 * 
 */
public interface Refreshable {

	/**
	 * Forces the element to refresh its content from its source
	 */
	void refresh(Object... args);
}
