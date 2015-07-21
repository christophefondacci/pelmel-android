package com.nextep.pelmel.listeners;

import com.nextep.pelmel.model.User;

public interface UserRegisterListener {

	/**
	 * Callback method called when the user was successfully created
	 * 
	 * @param user
	 *            the registered user
	 */
	void userRegistered(User user);

	/**
	 * The registration failed for the reason given by the message
	 * 
	 * @param message
	 */
	void registrationFailed(String message);
}
