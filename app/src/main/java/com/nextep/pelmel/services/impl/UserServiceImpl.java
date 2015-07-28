package com.nextep.pelmel.services.impl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.LoginActivity;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.listeners.UserRegisterListener;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.UserService;
import com.nextep.pelmel.services.WebService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

public class UserServiceImpl implements UserService {

	private static final String LOG_USER_TAG = "USER_SERVICE";
	private User currentUser;
	private final Set<UserListener> userListeners = new HashSet<UserListener>();
	private WebService webService;

	// TODO change this by local storage
	private String lastLogin = null; // = "christophet49@gmail.com";
	private String lastPassword = null; // = "tdk1558";

	@Override
	public void getCurrentUser(UserListener userListener) {
		// userListeners.add(userListener);
		if (currentUser == null) {
			reconnect(userListener);
		} else {
			userListener.userInfoAvailable(currentUser);
		}
	}

	@Override
	public void setCurrentUser(User user) {
		this.currentUser = user;
		// If the user is logged (i.e. not null)
		if (user != null) {
			// We notify everyone the user info is available
			for (final UserListener listener : userListeners) {
				listener.userInfoAvailable(user);
			}
		}
	}

	@Override
	public void addUserListener(UserListener listener) {
		userListeners.add(listener);
		// If we have a user logged in, we call back immediately
		if (currentUser != null) {
			listener.userInfoAvailable(currentUser);
		}
	}

	@Override
	public void removeUserListener(UserListener listener) {
		userListeners.remove(listener);
	}

	@Override
	public void login(String user, String password, UserListener listener) {
		// if (listener != null) {
		// addUserListener(listener);
		// }
		lastLogin = user;
		lastPassword = password;
		reconnect(listener);
	}

	@Override
	public void reconnect(final UserListener listener) {
		if ((lastLogin == null || "".equals(lastLogin))
				&& (lastPassword == null || "".equals(lastPassword))) {
			showLoginDialog();
		} else {
			new AsyncTask<Void, Void, User>() {
				@Override
				protected User doInBackground(Void... params) {
					// Connecting
					final JsonUser jsonUser = webService.connect(lastLogin,
							lastPassword);
					if (jsonUser != null) {
						final DataService dataService = PelMelApplication
								.getDataService();
						// Converting JSON to bean
						final User user = dataService.getUserFromJson(jsonUser);
						return user;
					} else {
						return null;
					}
				}

				@Override
				protected void onPostExecute(User result) {
					// Setting as current
					setCurrentUser(result);
					if (result != null) {
						listener.userInfoAvailable(result);
					} else {
						listener.userInfoUnavailable();
					}
				};
			}.execute();
		}
	}

	private void showLoginDialog() {
		final Intent intent = new Intent(PelMelApplication.getInstance()
				.getBaseContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PelMelApplication.getInstance().startActivity(intent);
	}

	public void setWebService(WebService webService) {
		this.webService = webService;
	}

	@Override
	public void logout() {
		// Resetting current user
		setCurrentUser(null);
		// Setting password as null
		lastPassword = null;
		// Applying changes in the preference so that another run of the app
		// will not relog in
		final SharedPreferences prefs = PelMelApplication.getInstance()
				.getSharedPreferences(PelMelConstants.PREFS_NAME, 0);
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PelMelConstants.PREF_PASSWORD, null);
		editor.commit();
	}

	@Override
	public void register(final String login, final String password,
			final String passwordConfirm, final String pseudo,
			final UserRegisterListener listener) {
		if (password != null && !password.equals(passwordConfirm)) {
			listener.registrationFailed(PelMelApplication.getInstance()
					.getString(R.string.registerPasswordError));
		} else if (login == null || password == null || passwordConfirm == null
				|| pseudo == null || "".equals(login.trim())
				|| "".equals(password.trim())
				|| "".equals(passwordConfirm.trim())
				|| "".equals(pseudo.trim())) {
			listener.registrationFailed(PelMelApplication.getInstance()
					.getString(R.string.registerEmptyValueError));
		} else {
			lastLogin = login;
			lastPassword = password;
			final Location loc = PelMelApplication.getLocalizationService()
					.getLocation();
			new AsyncTask<Void, Void, User>() {
				@Override
				protected User doInBackground(Void... params) {

					final HttpClient http = new DefaultHttpClient();
					final HttpPost post = new HttpPost(WebService.BASE_URL
							+ "/mobileRegister");

					final MultipartEntity multipart = new MultipartEntity();
					try {
						multipart.addPart("email", new StringBody(login));
						multipart.addPart("name", new StringBody(pseudo));
						multipart.addPart("password", new StringBody(password));
						multipart.addPart(
								"lat",
								new StringBody(
										String.valueOf(loc.getLatitude())));
						multipart.addPart(
								"lng",
								new StringBody(String.valueOf(loc
										.getLongitude())));
						multipart.addPart("passwordConfirm", new StringBody(
								passwordConfirm));
						post.setEntity(multipart);
						final HttpResponse response = http.execute(post);

						final HttpEntity entity = response.getEntity();
						if (entity != null) {
							final InputStream is = entity.getContent();
							final InputStreamReader reader = new InputStreamReader(
									is);
							final Gson gson = new Gson();
							try {
								final JsonUser jsonUser = gson.fromJson(reader,
										new TypeToken<JsonUser>() {
										}.getType());
								currentUser = PelMelApplication
										.getDataService().getUserFromJson(
												jsonUser);
								return currentUser;
							} catch (final RuntimeException e) {
								Log.e(LOG_USER_TAG, "Error registering user : "
										+ e.getMessage());
							}
						}
					} catch (final UnsupportedEncodingException e) {
						Log.e(LOG_USER_TAG,
								"Error registering user : " + e.getMessage());
					} catch (final ClientProtocolException e) {
						Log.e(LOG_USER_TAG,
								"Error registering user : " + e.getMessage());
					} catch (final IOException e) {
						Log.e(LOG_USER_TAG,
								"Error registering user : " + e.getMessage());
					}
					return null;
				}

				@Override
				protected void onPostExecute(User result) {
					if (result != null) {
						listener.userRegistered(result);
					} else {
						listener.registrationFailed(PelMelApplication
								.getInstance().getText(R.string.registerFailed)
								.toString());
					}

				};
			}.execute();
		}
	}

	@Override
	public void saveLastLoginInfo() {
		// Saving those credentials
		final SharedPreferences prefs = PelMelApplication.getInstance()
				.getSharedPreferences(PelMelConstants.PREFS_NAME, 0);
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PelMelConstants.PREF_USERNAME, lastLogin);
		editor.putString(PelMelConstants.PREF_PASSWORD, lastPassword);
		editor.commit();
	}

	@Override
	public int getSearchRadius() {
		final SharedPreferences prefs = PelMelApplication.getInstance()
				.getSharedPreferences(PelMelConstants.PREFS_NAME, 0);
		final int radius = prefs.getInt(PelMelConstants.PREF_SEARCH_RADIUS, 0);
		return radius;
	}

	@Override
	public void setSearchRadius(int radius) {
		final SharedPreferences prefs = PelMelApplication.getInstance()
				.getSharedPreferences(PelMelConstants.PREFS_NAME, 0);
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(PelMelConstants.PREF_SEARCH_RADIUS, radius);
		editor.commit();
	}

	@Override
	public User getLoggedUser() {
		return currentUser;
	}

	@Override
	public boolean isCheckedInAt(Place place) {
		return currentUser.getLastLocation() != null && currentUser.getLastLocation().getKey().equals(place.getKey()) && (System.currentTimeMillis()-currentUser.getLastLocationTime().getTime())<PelMelConstants.CHECKIN_TIMEOUT_MILLISECS;
	}
}
