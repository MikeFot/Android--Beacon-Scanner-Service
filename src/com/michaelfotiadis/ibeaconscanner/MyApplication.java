package com.michaelfotiadis.ibeaconscanner;

import android.app.Application;
import android.content.res.Configuration;

import com.michaelfotiadis.ibeaconscanner.utils.Logger;

public class MyApplication extends Application {

	private final String TAG = "MyApplication";
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		Logger.d(TAG, "Starting Application");
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
//		Logger.d(TAG, "Terminating Service due to low memory");
//		new ScanProcess().cancelServiceAlarm(getApplicationContext());
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
//		Logger.d(TAG, "Terminating Service due to App Exit");
//		new ScanProcess().cancelServiceAlarm(getApplicationContext());
	}

}
