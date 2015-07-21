package com.nextep.pelmel.listeners;

import com.nextep.pelmel.model.SortType;

/**
 * A listener that gets notified of changes in the sort of a listview.
 * 
 * @author cfondacci
 * 
 */
public interface SortChangeListener {

	/**
	 * This method gets called when the user changed sort of the listview
	 * 
	 * @param sortType
	 *            the {@link SortType} enum
	 */
	void sortChanged(SortType sortType);
}
