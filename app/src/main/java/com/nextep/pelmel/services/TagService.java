package com.nextep.pelmel.services;

import java.util.List;

import com.nextep.pelmel.model.Tag;

/**
 * Provides services regarding tag manipulation
 * 
 * @author cfondacci
 * 
 */
public interface TagService {

	/**
	 * Lists all known tags
	 * 
	 * @return a list of all tags
	 */
	List<Tag> listTags();

	/**
	 * Provides the tag instance corresponding to the given code.
	 * 
	 * @param code
	 *            the code of the tag to look for
	 * @return the corresponding {@link Tag} instance or <code>null</code> if
	 *         not found
	 */
	Tag getTag(String code);

	/**
	 * Provides the Android resource corresponding to the given tag.
	 * 
	 * @param tag
	 *            the {@link Tag} to get a resource for
	 * @return the corresponding android resource (always defined, worst case is
	 *         default tag image)
	 */
	int getImageResource(Tag tag);
}
