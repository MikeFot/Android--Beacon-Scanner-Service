package com.michaelfotiadis.ibeaconscanner;

import android.app.Application;

import com.michaelfotiadis.ibeaconscanner.utils.Logger;

public class MyApplication extends Application {

	private final String TAG = "MyApplication";
	

	@Override
	public void onCreate() {
		Logger.d(TAG, "Starting Application");
		super.onCreate();
	}

}
