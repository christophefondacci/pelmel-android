package com.nextep.pelmel.services.impl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextep.json.model.impl.JsonCheckinResponse;
import com.nextep.json.model.impl.JsonLoggedInUser;
import com.nextep.json.model.impl.JsonPrivateListResponse;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.LoginActivity;
import com.nextep.pelmel.exception.PelmelException;
import com.nextep.pelmel.gson.GsonHelper;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.listeners.UserRegisterListener;
import com.nextep.pelmel.model.Action;
import com.nextep.pelmel.model.CurrentUser;
import com.nextep.pelmel.model.NetworkStatus;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.ServiceCallback;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserServiceImpl implements UserService {

	private static final String LOG_USER_TAG = "USER_SERVICE";

	private static final String NETWORK_ACTION_ACCEPT = "CONFIRM";
	private static final String NETWORK_ACTION_CANCEL = "CANCEL";
	private static final String NETWORK_ACTION_REQUEST= "REQUEST";
	private static final String NETWORK_ACTION_INVITE = "INVITE";

	private CurrentUser currentUser;
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
		this.currentUser = (CurrentUser)user;
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

	@Override
	public Place getCheckedInPlace(User user) {
		if(user.getLastLocation() != null && user.getLastLocationTime().getTime()+PelMelConstants.CHECKIN_TIMEOUT_MILLISECS>System.currentTimeMillis()) {
			return user.getLastLocation();
		}
		return null;
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
										new TypeToken<JsonLoggedInUser>() {
										}.getType());
								currentUser = (CurrentUser)PelMelApplication
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
	public CurrentUser getLoggedUser() {
		return currentUser;
	}

	@Override
	public boolean isCheckedInAt(Place place) {
		if(place != null) {
			return currentUser.getLastLocation() != null && currentUser.getLastLocationTime() != null && currentUser.getLastLocation().getKey().equals(place.getKey()) && (System.currentTimeMillis() - currentUser.getLastLocationTime().getTime()) < PelMelConstants.CHECKIN_TIMEOUT_MILLISECS;
		} else {
			return false;
		}
	}

	@Override
	public void checkIn(Place place, CheckInCallback callback) {
		checkInOrOut(place,callback,false);
	}

	private void checkInOrOut(final Place place, CheckInCallback callback, boolean checkout) {
		final Location loc = PelMelApplication.getLocalizationService()
				.getLocation();

		// Invoking checkin webservice
		JsonCheckinResponse response = webService.checkInOrOut(currentUser, place, loc.getLatitude(), loc.getLongitude(), checkout);

		if(response == null) {
			if(callback != null) {
				callback.checkInFailed(place,"Checkin failed");
			}
		}
		final Place lastLocation = currentUser.getLastLocation();
		if(lastLocation != null && lastLocation.getKey().equals(response.getPreviousPlaceKey())) {
			lastLocation.setOverviewDataLoaded(false);
			lastLocation.setInsidersCount(response.getPreviousPlaceUsersCount());
		}
		if(place.getKey().equals(response.getNewPlaceKey())) {
			place.setOverviewDataLoaded(false);
			place.setInsidersCount(response.getNewPlaceUsersCount());
		}

		// Adjusting last user location
		if(!checkout) {
			currentUser.setLastLocation(place);
			currentUser.setLastLocationTime(new Date());
		} else {
			currentUser.setLastLocation(null);
		}
		currentUser.setLastLocationTime(new Date());

		PelMelApplication.getUiService().executeOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(lastLocation != null) {
					PelMelApplication.getSnippetContainerSupport().getMapFragment().refreshMarkerFor(lastLocation);
				}
				PelMelApplication.getSnippetContainerSupport().getMapFragment().refreshMarkerFor(place);
			}
		});

		if(callback != null) {
			if(checkout) {
				callback.didCheckOut(currentUser,place);
			} else {
				callback.didCheckIn(currentUser, place, lastLocation);
			}
		}
	}

	public void checkOut(Place place, CheckInCallback callback) {
		checkInOrOut(place, callback, true);
	}

	@Override
	public void resetPassword(final String email, final ServiceCallback callback) {

		PelMelApplication.runOnBackgroundThread(new Runnable() {
			@Override
			public void run() {
				try {
					webService.postRequest(new URL(WebService.BASE_URL + "/lostPassword"), "email", email);
					PelMelApplication.runOnMainThread(new Runnable() {
						@Override
						public void run() {
							callback.success();
						}
					});

				} catch (MalformedURLException | PelmelException e) {
					Log.e(LOG_USER_TAG, "Unable to call lostPassword service: " + e.getMessage(), e);
					PelMelApplication.runOnMainThread(new Runnable() {
						@Override
						public void run() {
							callback.failure("Unable to call lostPassword service: " + e.getMessage());
						}
					});
				}
			}
		});
	}

	@Override
	public NetworkStatus getNetworkStatusFor(User user) {
		final CurrentUser currentUser = getLoggedUser();
		for(User u : currentUser.getNetworkPendingApprovals()) {
			if(u.getKey().equals(user.getKey())) {
				return NetworkStatus.PENDING_APPROVAL;
			}
		}
		for(User u : currentUser.getNetworkPendingRequests()) {
			if(u.getKey().equals(user.getKey())) {
				return NetworkStatus.PENDING_REQUEST;
			}
		}
		for(User u : currentUser.getNetworkUsers()) {
			if(u.getKey().equals(user.getKey())) {
				return NetworkStatus.FRIENDS;
			}
		}
		return NetworkStatus.NOT_IN_NETWORK;

	}

	private String getActionCodeFromAction(Action networkAction) {
		String actionCode =  null;
		switch(networkAction) {
			case NETWORK_ACCEPT:
				actionCode = NETWORK_ACTION_ACCEPT;
				break;
			case NETWORK_CANCEL:
				actionCode = NETWORK_ACTION_CANCEL;
				break;
			case NETWORK_INVITE:
				actionCode = NETWORK_ACTION_INVITE;
				break;
			case NETWORK_REQUEST:
				actionCode = NETWORK_ACTION_REQUEST;
				break;
		}
		return actionCode;
	}

	public void refreshNetwork() throws PelmelException {
		executeNetworkAction(Action.NETWORK_LIST,null);
	}
	@Override
	public void executeNetworkAction(Action action, User user) throws PelmelException{
		String actionCode = getActionCodeFromAction(action);
		if(actionCode != null || action == Action.NETWORK_LIST) {
			// Getting logged user
			final CurrentUser currentUser = getLoggedUser();
			Log.d(LOG_USER_TAG,"Current user is " + currentUser);
			// Building arguments map
			final Map<String, String> params = new HashMap<>();
			params.put("nxtpUserToken",currentUser.getToken());
			if(actionCode != null) {
				params.put("userKey",user.getKey());
				params.put("action", actionCode);
			} else {
				params.put("userKey",currentUser.getKey());
			}


			// Calling
			try {
				final InputStream is = webService.postRequest(new URL(WebService.BASE_URL + "/mobilePrivateList"), params);
				final InputStreamReader reader = new InputStreamReader(is);
				JsonPrivateListResponse response = GsonHelper.getGson().fromJson(reader, new TypeToken<JsonPrivateListResponse>() {
				}.getType());
				PelMelApplication.getDataService().fillPrivateLists(currentUser, response);
				PelMelApplication.getSnippetContainerSupport().refresh();
			} catch(MalformedURLException e) {
				throw new PelmelException("Malformed URL: " + e.getMessage(),e);
			}
		}

	}


}
