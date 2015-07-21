package com.nextep.pelmel.listeners;

import android.app.Activity;

import com.nextep.pelmel.model.CalObject;

public interface OverviewListener {

	Activity getContext();

	void overviewDataAvailable(CalObject object);
}
