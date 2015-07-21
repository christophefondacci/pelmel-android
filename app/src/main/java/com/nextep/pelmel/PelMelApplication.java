package com.nextep.pelmel;

import android.app.Application;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.providers.OverviewProvider;
import com.nextep.pelmel.providers.impl.EventOverviewProvider;
import com.nextep.pelmel.providers.impl.PlaceOverviewProvider;
import com.nextep.pelmel.providers.impl.UserOverviewProvider;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.ImageService;
import com.nextep.pelmel.services.LocalizationService;
import com.nextep.pelmel.services.MessageService;
import com.nextep.pelmel.services.TagService;
import com.nextep.pelmel.services.UIService;
import com.nextep.pelmel.services.UserService;
import com.nextep.pelmel.services.WebService;
import com.nextep.pelmel.services.impl.DataServiceImpl;
import com.nextep.pelmel.services.impl.ImageServiceImpl;
import com.nextep.pelmel.services.impl.LocalizationServiceImpl;
import com.nextep.pelmel.services.impl.MessageServiceImpl;
import com.nextep.pelmel.services.impl.TagServiceimpl;
import com.nextep.pelmel.services.impl.UIServiceImpl;
import com.nextep.pelmel.services.impl.UserServiceImpl;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class PelMelApplication extends Application implements
		GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

	private static PelMelApplication instance;
	private WebService webService;
	private DataService dataService;
	private ImageService imageService;
	private TagService tagService;
	private UserService userService;
	private UIService uiService;
	private MessageService messageService;
	private LocalizationService localizationService;
	private LocationManager locationManager;

	private Object overviewObject;
	private int tabIndex = 0;
	private String searchParentKey;

	public static Application getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		initServices();

		// Create global configuration and initialize ImageLoader with this
		// configuration
		final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();
		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions).threadPoolSize(3)
				.memoryCache(new LruMemoryCache(100)).build();
		ImageLoader.getInstance().init(config);

	}

	@Override
	public void onConnected(Bundle bundle) {

	}

	/**
	 * Initializes all services
	 */
	private void initServices() {
		webService = new WebService();
		imageService = new ImageServiceImpl(getApplicationContext());
		dataService = new DataServiceImpl();
		tagService = new TagServiceimpl();
		userService = new UserServiceImpl();
		uiService = new UIServiceImpl();
		messageService = new MessageServiceImpl();
		localizationService = new LocalizationServiceImpl();

		((UserServiceImpl) userService).setWebService(webService);
		((MessageServiceImpl) messageService).setDataService(dataService);
		((MessageServiceImpl) messageService).setWebService(webService);
		((DataServiceImpl) dataService).setUserService(userService);
		localizationService.init();

		// TODO: remove default user
		// final User testUser = new UserImpl();
		// testUser.setName("test user");
		// testUser.setBirthDate(new Date(79, 9, 11));
		// testUser.setHeight(172);
		// testUser.setWeight(92);
		// testUser.addTag(tagService.getTag(Tag.MUSCLE));
		// testUser.addTag(tagService.getTag(Tag.BEARD));
		//
		// userService.setCurrentUser(testUser);

		((DataServiceImpl) dataService).setWebService(webService);
		((DataServiceImpl) dataService).setTagService(tagService);
	}

	// public static WebService getWebService() {
	// return instance.webService;
	// }

	public static DataService getDataService() {
		return instance.dataService;
	}

	public static UIService getUiService() {
		return instance.uiService;
	}

	public static ImageService getImageService() {
		return instance.imageService;
	}

	public static TagService getTagService() {
		return instance.tagService;
	}

	public static UserService getUserService() {
		return instance.userService;
	}

	public static MessageService getMessageService() {
		return instance.messageService;
	}

	public static LocalizationService getLocalizationService() {
		return instance.localizationService;
	}

	public static void setOverviewObject(Object overviewObject) {
		instance.overviewObject = overviewObject;
	}

	public static void setSearchParentKey(String parentKey) {
		instance.searchParentKey = parentKey;
	}

	public static String getSearchParentKey() {
		return instance.searchParentKey;
	}

	public static void setCurrentTab(int tab) {
		instance.tabIndex = tab;
	}

	public static int getCurrentTab() {
		return instance.tabIndex;
	}

	public static OverviewProvider getOverviewProvider() {
		if (instance.overviewObject != null) {
			if (instance.overviewObject instanceof Place) {
				return new PlaceOverviewProvider(
						(Place) instance.overviewObject);
			} else if (instance.overviewObject instanceof User) {
				return new UserOverviewProvider((User) instance.overviewObject);
			} else if (instance.overviewObject instanceof Event) {
				return new EventOverviewProvider(
						(Event) instance.overviewObject);
			}
		}
		return null;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e("geoloc", "CONNECTION FAILED");
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	public static void runOnMainThread(Runnable runnable) {
		final Handler handler = new Handler(instance.getMainLooper());
		handler.post(runnable);
	}
}
