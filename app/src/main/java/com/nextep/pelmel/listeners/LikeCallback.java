package com.nextep.pelmel.listeners;

import com.nextep.pelmel.model.CalObject;

/**
 * A callback which should be implemented to get notified of a like operation
 * 
 * @author cfondacci
 * 
 */
public interface LikeCallback {

	/**
	 * The given object has been successfully liked. this method will be called
	 * on the main thread.
	 * 
	 * @param object
	 *            the object which has been liked
	 * @param newLikeCount
	 *            the new like count
	 * @param newDislikesCount
	 *            the new dislikes count
	 */
	void liked(CalObject object, int newLikeCount, int newDislikesCount);
}
