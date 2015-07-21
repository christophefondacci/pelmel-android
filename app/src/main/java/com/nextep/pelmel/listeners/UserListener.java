package com.nextep.pelmel.listeners;

import com.nextep.pelmel.model.User;

/**
 * Defines a listener which could be notified of user bean availability.
 * 
 * @author cfondacci
 * 
 */
public interface UserListener {

	/**
	 * Informs the receiver that user information is available
	 * 
	 * @param user
	 *            the {@link User} bean
	 */
	void userInfoAvailable(User user);

	void userInfoUnavailable();
}
