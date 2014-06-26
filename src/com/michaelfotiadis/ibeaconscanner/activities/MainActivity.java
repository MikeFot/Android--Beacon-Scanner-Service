package com.michaelfotiadis.ibeaconscanner.activities;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.michaelfotiadis.ibeaconscanner.R;
import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;
import com.michaelfotiadis.ibeaconscanner.processes.ScanProcess;
import com.michaelfotiadis.ibeaconscanner.tasks.MonitorTask;
import com.michaelfotiadis.ibeaconscanner.tasks.MonitorTask.OnBeaconDataChangedListener;
import com.michaelfotiadis.ibeaconscanner.utils.BluetoothUtils;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;
import com.michaelfotiadis.ibeaconscanner.utils.ToastUtils;

public class MainActivity extends FragmentActivity {

	public class ResponseReceiver extends BroadcastReceiver {
		private String TAG = "Response Receiver";
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG, "On Receiver Result");
			if (intent.getAction().equalsIgnoreCase(
					CustomConstants.Broadcasts.BROADCAST_1.getString())) {
				Logger.i(TAG, "Scan Running");
				SuperActivityToast.cancelAllSuperActivityToasts();
				isScanRunning = true;
				isToastScanningNowShown = true;
				mSuperActivityToast = ToastUtils.makeProgressToast(MainActivity.this, mSuperActivityToast, mToastStringScanningNow);
				mButton.setText(getResources().getString(R.string.label_stop_scanning));
				
			} else if (intent.getAction().equalsIgnoreCase(
					CustomConstants.Broadcasts.BROADCAST_2.getString())) {
				Logger.i(TAG, "Service Finished");
				SuperActivityToast.cancelAllSuperActivityToasts();
				isToastScanningNowShown = false;
//				isToastStoppingScanShown = false;
				ToastUtils.makeInfoToast(MainActivity.this, mToastStringScanFinished);
				mButton.setEnabled(true);
			}
		}
	}

	private final String TAG = this.toString();

	private BluetoothUtils mBluetoothUtils;

	private Button mButton;
	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	private final int mScanTime = 5000;

	// use Gap Time of -1 to disable repeated scanning
	private final int mGapTime = 5000;

	private TextView mTextView;
	private CharSequence mTextViewContents;

	private MonitorTask mMonitorTask;

	// Receivers
	private ResponseReceiver mScanReceiver;


	private boolean isScanRunning = false;

	private SuperActivityToast mSuperActivityToast;

	private final String mToastStringScanningNow = "Scanning...";
	private final String mToastStringScanFinished = "Scan finished";
	private final String mToastStringEnableLE = "Please enable bluetooth to continue...";
	private final String mToastStringNoLE = "Device does not support Bluetooth LE";
	private final String mToastStringScanInterrupted = "Scan Interrupted";

	private boolean isToastScanningNowShown;
	private boolean isToastStoppingScanShown;
	
	private boolean mButtonState = true;

	private void notifyTextViewDataChanged() {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (Singleton.getInstance().getAvailableDevicesList() != null) {
					mTextView.setText("Number of Available Devices : " 
							+ String.valueOf(Singleton.getInstance().getAvailableDevicesList().size()));

					StringBuilder message;
					for (BluetoothLeDevice device : Singleton.getInstance().getAvailableDevicesList().values()) {

						IBeaconDevice iBeacon = new IBeaconDevice(device);
						message = new StringBuilder();
						message.append(CustomConstants.LINE_SEPARATOR);
						message.append("ID : ");
						message.append(iBeacon.getAddress());
						message.append(" with Accuracy : ");
						message.append(CustomConstants.df.format(iBeacon.getAccuracy()));
						message.append(" m");
						mTextView.append(message.toString());
					}
				}
			}
		});

	}

	public void onClickStartScanning(View view) {
		Logger.d(TAG, "Click on Scan Button");
		SuperActivityToast.cancelAllSuperActivityToasts();

		if (isScanRunning) {
			// Cancels the alarms if the scan is already running
//			mButton.setEnabled(false);
			new ScanProcess().cancelService(this);
			isToastScanningNowShown = false;
			mSuperActivityToast = ToastUtils.makeInfoToast(this, mToastStringScanInterrupted);
			mButton.setText(getResources().getString(R.string.label_start_scanning));
			isScanRunning = false;
		} else {
			// This ScanProcess will also cancel all alarms on continuation
			new ScanProcess().scanForIBeacons(MainActivity.this, mScanTime, mGapTime);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		Logger.d(TAG, "Starting Main Activity");

		mTextView = (TextView) findViewById(R.id.textViewReportScanResults);

		if (savedInstanceState != null) {
			mTextViewContents = savedInstanceState.getCharSequence(CustomConstants.Payloads.PAYLOAD_1.toString());
			isScanRunning = savedInstanceState.getBoolean(CustomConstants.Payloads.PAYLOAD_2.toString(), false);
			mButtonState = savedInstanceState.getBoolean(CustomConstants.Payloads.PAYLOAD_3.toString(), true);
			isToastScanningNowShown  = savedInstanceState.getBoolean(CustomConstants.Payloads.PAYLOAD_4.toString(), false);
			isToastStoppingScanShown  = savedInstanceState.getBoolean(CustomConstants.Payloads.PAYLOAD_5.toString(), false);
		}

		mButton = (Button) findViewById(R.id.buttonStartScanningMain);
		mButton.setEnabled(false);

		mBluetoothUtils = new BluetoothUtils(this);

		// monitor the singleton
		registerMonitorTask();

		// Wait for broadcasts from the scanning process
		registerResponseReceiver();
		SuperActivityToast.cancelAllSuperActivityToasts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		removeReceivers();
		Logger.d(TAG, "App onDestroy");
		super.onDestroy();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// Cancel the alarm
		SuperActivityToast.cancelAllSuperActivityToasts();
		new ScanProcess().cancelService(this);
		Logger.d(TAG, "App onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SuperActivityToast.cancelAllSuperActivityToasts();
		
		if (!mBluetoothUtils.isBluetoothLeSupported()) {
			mSuperActivityToast = ToastUtils.makeWarningToast(this, mToastStringNoLE );
		} else {
			if (!mBluetoothUtils.isBluetoothOn()) {
				new ScanProcess().cancelService(this);
				mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
			}
			if (mBluetoothUtils.isBluetoothOn()
					&& mBluetoothUtils.isBluetoothLeSupported()) {
				Logger.i(TAG, "Bluetooth has been activated");
				mButton.setEnabled(mButtonState);
				if (isScanRunning && mButtonState) {
					Logger.d(TAG, "Restarting Scan Service");
					new ScanProcess().scanForIBeacons(MainActivity.this, mScanTime, mGapTime);
				} 
				
				if(isToastScanningNowShown) {
					mSuperActivityToast = ToastUtils.makeProgressToast(this, mSuperActivityToast, mToastStringScanningNow);
				}
				
//				if (isToastStoppingScanShown) {
//					mSuperActivityToast = ToastUtils.makeProgressToast(this, mSuperActivityToast, mToastStringStoppingScan);
//				}
				
				if (mTextViewContents != null && !mTextViewContents.equals("")) {
					mTextView.setText(mTextViewContents);
				}
			} else {
				mSuperActivityToast = ToastUtils.makeProgressToast(this, mSuperActivityToast, mToastStringEnableLE);
			}
		}
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putCharSequence(CustomConstants.Payloads.PAYLOAD_1.toString(), mTextViewContents);
		outState.putBoolean(CustomConstants.Payloads.PAYLOAD_2.toString(), isScanRunning);
		outState.putBoolean(CustomConstants.Payloads.PAYLOAD_3.toString(), mButton.isEnabled());
		outState.putBoolean(CustomConstants.Payloads.PAYLOAD_4.toString(), isToastScanningNowShown);
		outState.putBoolean(CustomConstants.Payloads.PAYLOAD_5.toString(), isToastStoppingScanShown);
		super.onSaveInstanceState(outState);
	}

	private void registerMonitorTask() {
		Logger.d(TAG, "Starting Monitor Task");
		mMonitorTask = new MonitorTask(new OnBeaconDataChangedListener() {
			@Override
			public void onDataChanged() {
				Logger.d(TAG, "Singleton Data Changed");
				notifyTextViewDataChanged();
			}
		});
		mMonitorTask.start();
	}



	private void registerResponseReceiver() {
		Logger.d(TAG, "Registering Response Receiver");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CustomConstants.Broadcasts.BROADCAST_1.getString());
		intentFilter.addAction(CustomConstants.Broadcasts.BROADCAST_2.getString());

		mScanReceiver = new ResponseReceiver();
		this.registerReceiver(mScanReceiver, intentFilter);
	}

	protected void removeReceivers() {
		try {
			this.unregisterReceiver(mScanReceiver);
			Logger.d(TAG, "Scan Receiver Unregistered Successfully");
		} catch (Exception e) {
			Logger.d(
					TAG,
					"Scan Receiver Already Unregistered. Exception : "
							+ e.getLocalizedMessage());
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	protected void stopMonitorTask() {
		if (mMonitorTask != null) {
			Logger.d(TAG, "Monitor Task paused");
			mMonitorTask.stop();
		}
	}
}
