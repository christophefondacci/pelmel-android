package com.nextep.pelmel.activities;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.views.BadgeView;

public class TabBarActivity extends TabActivity implements OnTabChangeListener {

	public static final String TAB_PLACES = "places";
	public static final String TAB_MAP = "map";
	public static final String TAB_EVENTS = "events";
	public static final String TAB_CHAT = "chat";
	public static final String TAB_SETTINGS = "settings";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);

		final Intent intentPlaceList = new Intent(this,
				ListPlacesActivity.class);
		final Intent intentEventList = new Intent(this,
				ListEventsActivity.class);
		final Intent intentMap = new Intent(this, MapActivity.class);
		final Intent intentAccount = new Intent(this, AccountActivity.class);
		final Intent intentChat = new Intent(this, ChatActivity.class);
		final TabHost tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		final Resources res = PelMelApplication.getInstance().getResources();

		// First tab : places list
		final TabSpec tab1 = tabHost
				.newTabSpec(TAB_PLACES)
				.setIndicator(res.getString(R.string.tabPlaceList),
						res.getDrawable(R.drawable.tab_place))
				.setContent(intentPlaceList);
		tabHost.addTab(tab1);

		// Second tab : Map
		final TabSpec tab2 = tabHost
				.newTabSpec(TAB_MAP)
				.setIndicator(res.getString(R.string.tabMap),
						res.getDrawable(R.drawable.tab_location))
				.setContent(intentMap);
		tabHost.addTab(tab2);

		// Third tab : events list
		final TabSpec tab3 = tabHost
				.newTabSpec(TAB_EVENTS)
				.setIndicator(res.getString(R.string.tabEvents),
						res.getDrawable(R.drawable.tab_calendar))
				.setContent(intentEventList);
		tabHost.addTab(tab3);

		// Fourth tab : Chat
		final TabSpec tab4 = tabHost
				.newTabSpec(TAB_CHAT)
				.setIndicator(res.getString(R.string.tabMessages),
						res.getDrawable(R.drawable.tab_chat))
				.setContent(intentChat);
		tabHost.addTab(tab4);
		TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		BadgeView badgeView = new BadgeView(this, tabWidget, 3);
		PelMelApplication.getUiService().registerUnreadMsgBadgeView(badgeView);

		// Fifth tab : Settings
		final TabSpec tab5 = tabHost
				.newTabSpec(TAB_SETTINGS)
				.setIndicator(res.getString(R.string.tabSettings),
						res.getDrawable(R.drawable.tab_settings))
				.setContent(intentAccount);
		tabHost.addTab(tab5);

		tabHost.setCurrentTab(PelMelApplication.getCurrentTab());

	}

	@Override
	public void onTabChanged(String tabId) {
		final TabHost tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i)
					.setBackgroundColor(Color.BLACK);
		}

		tabHost.getTabWidget()
				.getChildAt(tabHost.getCurrentTab())
				.setBackgroundDrawable(
						getResources().getDrawable(R.drawable.tab_bg_gradient));
		tabHost.getTabWidget().setDividerDrawable(
				getResources().getDrawable(R.drawable.tab_bg_gradient));
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), 0);
	}
}
