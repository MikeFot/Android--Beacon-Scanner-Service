package com.michaelfotiadis.ibeaconscanner.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.michaelfotiadis.ibeaconscanner.R;
import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.processes.ScanProcess;
import com.michaelfotiadis.ibeaconscanner.utils.BluetoothUtils;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;

public class MainActivity extends FragmentActivity {

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

	private ResponseReceiver mScanReceiver;
	
	public class ResponseReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG, "On Receiver Result");
			if (intent.getAction().equalsIgnoreCase(
					CustomConstants.Broadcasts.BROADCAST_1.getString())) {
				Logger.i(TAG, "Scan Started");
				launchRingDialog();
			} 
		}
	}
	
	protected void removeReceivers() {
		try {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(
					mScanReceiver);
			Logger.d(TAG, "Receiver Unregistered Successfully");
		} catch (Exception e) {
			Logger.d(
					TAG,
					"Receiver Already Unregistered. Exception : "
							+ e.getLocalizedMessage());
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		mTitle = getTitle();
		Logger.d(TAG, "Starting Application");

		mButton = (Button) findViewById(R.id.buttonStartScanningMain);
		mButton.setEnabled(false);

		mBluetoothUtils = new BluetoothUtils(this);
		mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mBluetoothUtils.isBluetoothOn()
				&& mBluetoothUtils.isBluetoothLeSupported()) {
			Logger.i(TAG, "Bluetooth has been activated");
			mButton.setEnabled(true);
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			TextView textView = (TextView) rootView.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(
					getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

	public void onClickStartScanning(View view) {
		Logger.d(TAG, "Click on Scan Button");
		
		new ScanProcess().cancelServiceAlarm(this);
		
		Logger.d(TAG, "Registering Response Receiver");
		IntentFilter mIntentFilter = new IntentFilter(
				CustomConstants.Broadcasts.BROADCAST_1.getString());

		mScanReceiver = new ResponseReceiver();
		Logger.d(TAG, "Registering Receiver");
		LocalBroadcastManager.getInstance(this).registerReceiver(mScanReceiver,
				mIntentFilter);
		
		new ScanProcess().scanForIBeacons(MainActivity.this, mScanTime, mGapTime);
	}

	public void launchRingDialog () {
		final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...",	"Scanning ...", true);
		ringProgressDialog.setCancelable(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(mScanTime);
				} catch (Exception e) {
					Logger.e(TAG, e.getLocalizedMessage());
				}
				ringProgressDialog.dismiss();
			}
		}).start();
	}

	@Override
	protected void onPause() {
		// Cancel the alarm
		new ScanProcess().cancelServiceAlarm(this);
		removeReceivers();
		Logger.d(TAG, "App onPause");
		super.onPause();
	}
}
