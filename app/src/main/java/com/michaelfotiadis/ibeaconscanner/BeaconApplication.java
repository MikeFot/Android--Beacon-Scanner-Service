package com.michaelfotiadis.ibeaconscanner;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;

import io.fabric.sdk.android.Fabric;

public class BeaconApplication extends Application {

	private final String TAG = "BeaconApplication";
	

	@Override
	public void onCreate() {
		Logger.d(TAG, "Starting Application");
		super.onCreate();
		Fabric.with(this, new Crashlytics());
	}

}
