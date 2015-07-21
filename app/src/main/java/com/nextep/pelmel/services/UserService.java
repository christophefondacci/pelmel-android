package com.nextep.pelmel.services;

import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.listeners.UserRegisterListener;
import com.nextep.pelmel.model.User;

public interface UserService {

	void getCurrentUser(UserListener listener);

	/**
	 * Logs the user in from the given login and password
	 * 
	 * @param user
	 *            the user login
	 * @param password
	 *            the user password
	 */
	void login(String user, String password, UserListener listener);

	/**
	 * Registers a new user using the specified information
	 * 
	 * @param login
	 *            login of the new user to create
	 * @param password
	 *            password of the new user to create
	 * @param passwordConfirm
	 *            password confirmation
	 * @param pseudo
	 *            pseudo of the new user
	 * @param listener
	 *            the {@link UserRegisterListener} to call back
	 */
	void register(String login, String password, String passwordConfirm,
			String pseudo, UserRegisterListener listener);

	/**
	 * Attempts to reconnect the user from last login / password information.
	 */
	void reconnect(UserListener listener);

	/**
	 * Registers the given listener
	 * 
	 * @param listener
	 *            the new {@link UserListener}
	 */
	void addUserListener(UserListener listener);

	/**
	 * Unregisters the given listener to user events
	 * 
	 * @param listener
	 *            the {@link UserListener} to unregister
	 */
	void removeUserListener(UserListener listener);

	void setCurrentUser(User user);

	/**
	 * Logs out the current user
	 */
	void logout();

	/**
	 * Persists the last used login information
	 */
	void saveLastLoginInfo();

	/**
	 * Retrieves the search radius to use when fetching places
	 * 
	 * @return the current search radius
	 */
	int getSearchRadius();

	/**
	 * Defines the radius to use in any subsequent search
	 * 
	 * @param radius
	 *            the radius to use
	 */
	void setSearchRadius(int radius);
}
