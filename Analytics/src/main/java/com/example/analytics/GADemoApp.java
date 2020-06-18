package com.example.analytics;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class GADemoApp extends Application {

	/* Analytics tracker instance */
	Tracker tracker;

	/* This is the getter for the tracker instance. This is called from
	 * within the Activity to get a reference to the tracker instance.
	 */
	public synchronized Tracker getTracker() {
		if (tracker == null) {
			// Get the singleton Analytics instance, get Tracker from it
			GoogleAnalytics instance = GoogleAnalytics.getInstance(this);

			// Start tracking the app with your web property ID
			/*
			 * Define web property ID obtained after creating a profile for the app. If
			 * using the Gradle plugin, this should be available as R.xml.global_tracker.
			 */
			String webId = "UA-NNNNNNNN-Y";
			tracker = instance.newTracker( webId );

			// Any app-specific Application setup code goes here...
		}
		return tracker;
	}
}
