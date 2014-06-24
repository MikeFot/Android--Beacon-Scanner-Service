package com.michaelfotiadis.ibeaconscanner.services;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;
import com.michaelfotiadis.ibeaconscanner.tasks.ScanAsyncTask;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;

public class ScanService  extends Service {

	private final String TAG = this.toString();
	// Service time fields
	private long mTimeStart;
	private long mTimeEnd;

	private long mServiceDuration;
	// Scan time parameter fields
	private int mScanDuration;
	private int mGapDuration;

	// BlueTooth fields
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;

	// ASyncTask
	private ScanAsyncTask mThread;
	private int mMaximumNumberOfScans = 5;

	@Override
	public void onCreate() {
		super.onCreate();
		// Record the start time
		mTimeStart = Calendar.getInstance().getTimeInMillis();
		Logger.d(TAG, "Started Scan Service at " + mTimeStart);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.d(TAG, "Handling service intent");

		// Retrieve the extra pay loads
		mScanDuration = intent.getIntExtra(CustomConstants.Payloads.PAYLOAD_1.getString(), 2000);
		mGapDuration = intent.getIntExtra(CustomConstants.Payloads.PAYLOAD_2.getString(), 2000);


		Intent broadcastIntent = new Intent(CustomConstants.Broadcasts.BROADCAST_1.getString());

		Logger.i(TAG, "Broadcasting Scanning Status");
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
		
		// Log the extra pay loads
		Logger.d(TAG, "Received Scan Duration " + mScanDuration);
		Logger.d(TAG, "Received Gap Duration " + mGapDuration);

		// Get the blueTooth adapter
		mBluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();

		Logger.d(TAG, "Bluetooth Adapter : " + mBluetoothAdapter.getAddress());
		Logger.d(TAG, "Scan Mode of the adapter : " + mBluetoothAdapter.getScanMode());

		Logger.d(TAG, "Starting Scanner for " + mScanDuration);

		if ((mThread==null) || (mThread.getStatus()==AsyncTask.Status.FINISHED)) {
			Logger.i(TAG, "onStartCommand starting new thread");
			mThread = new ScanAsyncTask(mScanDuration, this);
			mThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
		}
		else {
			Logger.i(TAG, "onStartCommand NOT starting new thread");
		}

		Singleton.getInstance().setNumberOfScans(Singleton.getInstance().getNumberOfScans() + 1);
		Logger.e(TAG, "***Number of scans : " + Singleton.getInstance().getNumberOfScans());


		// Schedule the next run
		Intent repeatingIntent = new Intent(this, this.getClass());
		repeatingIntent.putExtra(CustomConstants.Payloads.PAYLOAD_1.getString(), mScanDuration);
		repeatingIntent.putExtra(CustomConstants.Payloads.PAYLOAD_2.getString(), mGapDuration);

		PendingIntent pengindIntent = PendingIntent.getService(this, 0, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		if (Singleton.getInstance().getNumberOfScans() < mMaximumNumberOfScans && mGapDuration > 0) {
			Logger.e(TAG, "Scheduling Next Run");
			alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + mScanDuration 
					+ mGapDuration, pengindIntent);
			Logger.e(TAG, "Next scan will occur in " + mScanDuration + mGapDuration 
					+ " at " + Calendar.getInstance().getTimeInMillis() + mScanDuration 
					+ mGapDuration);
		}
		else {
			alarm.cancel(pengindIntent);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if (mThread!=null && (mThread.getStatus()==AsyncTask.Status.RUNNING)) {
			mThread.cancel(true);
		}
		mTimeEnd = Calendar.getInstance().getTimeInMillis();
		mServiceDuration = mTimeEnd - mTimeStart;
		Logger.d(TAG, "Service duration in milliseconds : " + mServiceDuration);


		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
