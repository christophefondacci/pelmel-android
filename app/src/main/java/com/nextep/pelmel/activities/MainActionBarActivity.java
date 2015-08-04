package com.nextep.pelmel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.providers.impl.ContextSnippetInfoProvider;
import com.nextep.pelmel.services.LocalizationService;

public class MainActionBarActivity extends ActionBarActivity {

	private LocalizationService localizationService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		localizationService = PelMelApplication.getLocalizationService();
	}

	protected LocalizationService getLocalizationService() {
		return localizationService;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pelmel_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case R.id.menu_places:
//			final Intent intentPlaceList = new Intent(this,
//					ListPlacesActivity.class);
//			intentPlaceList.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(intentPlaceList);
//			return true;
		case R.id.menu_export:
			PelMelApplication.getDataService().exportDatabase(this);
			return true;
		case R.id.menu_map:
			ContextSnippetInfoProvider provider = new ContextSnippetInfoProvider();
			PelMelApplication.getSnippetContainerSupport().showSnippetFor(provider,false,true);
			PelMelApplication.getSnippetContainerSupport().minimizeSnippet();
			return true;
//		case R.id.menu_events:
//			final Intent intentEventList = new Intent(this,
//					ListEventsActivity.class);
//			intentEventList.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(intentEventList);
//			return true;
		case R.id.menu_chat:
			final ChatActivity chatFragment = new ChatActivity();
			PelMelApplication.getSnippetContainerSupport().showSnippetForFragment(chatFragment, true, false);
			return true;
		case R.id.menu_settings:
			final Intent intentAccount = new Intent(this, AccountActivity.class);
			intentAccount.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intentAccount);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		getLocalizationService().stopLocationUpdates();
		super.onPause();
	}

	@Override
	protected void onResume() {
		getLocalizationService().startLocationUpdates();
		super.onResume();
	}

}
