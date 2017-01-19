package com.michaelfotiadis.ibeaconscanner.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

public class BluetoothUtils {

	private final Activity mActivity;
	private final BluetoothAdapter mBluetoothAdapter;
	private final BluetoothManager mBluetoothManager;

	public final static int REQUEST_ENABLE_BT = 2001;

	public BluetoothUtils(Activity activity){
		mActivity = activity;
		mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
	}

	public boolean askUserToEnableBluetoothIfNeeded(){
		if (isBluetoothLeSupported() && (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setMessage("Please enable Bluetooth to continue!")
			.setCancelable(false)
			.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					mActivity.startActivity(new Intent(
							Settings.ACTION_BLUETOOTH_SETTINGS));
				}
			})
			.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					mActivity.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
		return true;
	}

	public BluetoothAdapter getBluetoothAdapter(){
		return mBluetoothAdapter;
	}

	public boolean isBluetoothLeSupported(){
		return mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	public boolean isBluetoothOn(){
		if (mBluetoothAdapter == null) {
			return false;
		} else {
			return mBluetoothAdapter.isEnabled();
		}
	}

}
