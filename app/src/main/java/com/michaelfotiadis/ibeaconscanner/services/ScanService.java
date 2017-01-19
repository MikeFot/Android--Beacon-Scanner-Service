package com.michaelfotiadis.ibeaconscanner.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;

import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;
import com.michaelfotiadis.ibeaconscanner.tasks.ScanAsyncTask;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;

import java.util.Calendar;

public class ScanService  extends Service {

	public class BluetoothReceiver extends BroadcastReceiver {
		private String TAG = "Bluetooth Receiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.ERROR);
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					Logger.d(TAG, "Bluetooth Receiver State OFF");
					Logger.d(TAG, "Stopping Service");
					ScanService.this.stopSelf();
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					Logger.d(TAG, "Bluetooth Receiver State Turning OFF");
					break;
				case BluetoothAdapter.STATE_ON:
					Logger.d(TAG, "Bluetooth Receiver State ON");
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					Logger.d(TAG, "Bluetooth Receiver State Turning ON");
					break;
				}
			}
		}
	}
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

	private BluetoothReceiver mBluetoothReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Record the start time
		mTimeStart = Calendar.getInstance().getTimeInMillis();
		
		Logger.d(TAG, "Started Scan Service at " + mTimeStart);
	}

	@Override
	public void onDestroy() {
		unregisterBluetoothReceiver();
		if (mThread!=null && (mThread.getStatus()==AsyncTask.Status.RUNNING)) {
			mThread.cancel(true);
		}
		mTimeEnd = Calendar.getInstance().getTimeInMillis();
		mServiceDuration = mTimeEnd - mTimeStart;
		Logger.d(TAG, "Service duration in milliseconds : " + mServiceDuration);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.d(TAG, "Handling service intent");

		if (intent == null || intent.getExtras() == null) {
			Logger.e(TAG, "No Extras for Service. Aborting...");
			return START_NOT_STICKY;
		}

		registerBluetoothReceiver();
		
		// Retrieve the extra pay loads
		mScanDuration = intent.getIntExtra(CustomConstants.Payloads.PAYLOAD_1.getString(), 2000);
		mGapDuration = intent.getIntExtra(CustomConstants.Payloads.PAYLOAD_2.getString(), 2000);

		Intent broadcastIntent = new Intent(CustomConstants.Broadcasts.BROADCAST_1.getString());
		Logger.i(TAG, "Broadcasting Scanning Status Started");
		this.sendBroadcast(broadcastIntent);

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

		if (mGapDuration > 0) {
			Logger.i(TAG, "Scheduling Next Run");
			alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + mScanDuration 
					+ mGapDuration, pengindIntent);
			Logger.i(TAG, "Next scan will occur in " + mScanDuration + mGapDuration 
					+ " at " + Calendar.getInstance().getTimeInMillis() + mScanDuration 
					+ mGapDuration);
		}
		else {
			alarm.cancel(pengindIntent);
			unregisterBluetoothReceiver();
		}
		return START_NOT_STICKY;
	}

	private void registerBluetoothReceiver() {
		Logger.d(TAG, "Registering Bluetooth Receiver");
		IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

		mBluetoothReceiver = new BluetoothReceiver();
		this.registerReceiver(mBluetoothReceiver, intentFilter);
	}

	private void unregisterBluetoothReceiver() {
		try {
			this.unregisterReceiver(mBluetoothReceiver);
			Logger.d(TAG, "Bluetooth Receiver Unregistered Successfully");
		} catch (Exception e) {
			Logger.d(
					TAG,
					"Bluetooth Receiver Already Unregistered. Exception : "
							+ e.getLocalizedMessage());
		}
	}
}
