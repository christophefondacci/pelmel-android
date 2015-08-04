package com.nextep.pelmel.services;

import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.listeners.UserRegisterListener;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;

public interface UserService {

    interface CheckInCallback {
        void didCheckIn(User user, Place place, Place previousLocation);
        void didCheckOut(User user, Place fromPlace);
        void checkInFailed(Place place, String reason);
    }
    void getCurrentUser(UserListener listener);

    /**
     * Logs the user in from the given login and password
     *
     * @param user     the user login
     * @param password the user password
     */
    void login(String user, String password, UserListener listener);

    /**
     * Registers a new user using the specified information
     *
     * @param login           login of the new user to create
     * @param password        password of the new user to create
     * @param passwordConfirm password confirmation
     * @param pseudo          pseudo of the new user
     * @param listener        the {@link UserRegisterListener} to call back
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
     * @param listener the new {@link UserListener}
     */
    void addUserListener(UserListener listener);

    /**
     * Unregisters the given listener to user events
     *
     * @param listener the {@link UserListener} to unregister
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
     * @param radius the radius to use
     */
    void setSearchRadius(int radius);

    /**
     * Get currently logged in user.
     *
     * @return the currently logged user (use with care, getCurrentUser() should generally be preferred when possible)
     */
    User getLoggedUser();

    /**
     * Returns whether the current user is currently checked in at the given place
     *
     * @param place the Place to check
     * @return <code>true</code> when current user is checked in at that place, else <code>false</code>
     */
    boolean isCheckedInAt(Place place);

    /**
     * Checks the current user in the given place. The user will be automatically checked out from any
     * previous location
     * @param place the place to check the user in
     * @param callback the callback for checkin process notification
     */
    void checkIn(Place place,CheckInCallback callback);

    /**
     * Checks the current user out the given place.
     *
     * @param place the place where the user should be checked out from
     * @param callback the callback that should be called when action is complete
     */
    void checkOut(Place place, CheckInCallback callback);
}
