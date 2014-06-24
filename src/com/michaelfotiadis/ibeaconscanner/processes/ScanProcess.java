package com.michaelfotiadis.ibeaconscanner.processes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

//import com.eratosthenes.ibeaconscanner.services.ScanService;

import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;
import com.michaelfotiadis.ibeaconscanner.services.ScanService;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;

public class ScanProcess {

	private final String TAG = this.toString();	

	/**
	 * 	
	/**
	 * Takes 2 time variables and starts a Scan Service 
	 * @param context Context calling this method
	 * @param scanDurationTime Scan duration in milliseconds
	 * @param scanGapTime Time between consecutive scans in milliseconds
	 */
	public void scanForIBeacons(Context context, int scanDurationTime, int scanGapTime) {
		Logger.d(TAG, "Generating service intent");
		Logger.d(TAG, "Scan Time : " + scanDurationTime);
		Logger.d(TAG, "Gap Time : " + scanGapTime);
		
		
		Singleton.getInstance().setNumberOfScans(0);
		
		// Create the service intent
		Intent serviceIntent = new Intent(context, ScanService.class);
		// Pass time variables to the intent
		serviceIntent.putExtra(CustomConstants.Payloads.PAYLOAD_1.getString(), scanDurationTime);
		serviceIntent.putExtra(CustomConstants.Payloads.PAYLOAD_2.getString(), scanGapTime);
		
		// Start the service
		Logger.d(TAG, "Attempting to start scan service");
		context.startService(serviceIntent);
	}

	public void cancelServiceAlarm(Context context) {
		Logger.d(TAG, "Cancelling Service Alarm");
		Intent repeatingIntent = new Intent(context, ScanService.class);
		PendingIntent pengindIntent = PendingIntent.getService(context, 0, repeatingIntent, 
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		alarm.cancel(pengindIntent);
	}
}
