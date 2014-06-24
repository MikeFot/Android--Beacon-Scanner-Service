package com.michaelfotiadis.ibeaconscanner.utils;
//package com.eratosthenes.ibeaconscanner.utils;
//
//import android.bluetooth.BluetoothAdapter;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.support.v4.content.LocalBroadcastManager;
//
//import com.eratosthenes.ibeaconscanner.containers.CustomConstants;
//
//public class BluetoothLeScanner {
////	private class HandlerRunnable implements Runnable{
////
////		@Override
////		public void run() {
////			Logger.i(TAG, "~ Stopping Scan (timeout)");
////			mScanning = false;
////			mBluetoothAdapter.stopLeScan(mLeScanCallback);
////			notifyFinished(mScanning);
////		}
////
////	}
////	private final Handler mHandler;
////	private final BluetoothAdapter.LeScanCallback mLeScanCallback;
////	private final BluetoothAdapter mBluetoothAdapter;
////	private boolean mScanning;
////	private Context mContext;
////
////	private final String TAG = this.toString();
////
////	public BluetoothLeScanner(Context context, BluetoothAdapter.LeScanCallback leScanCallback, 
////			BluetoothAdapter bluetoothAdapter){
////		mHandler = new Handler();
////		mLeScanCallback = leScanCallback;
////		mBluetoothAdapter = bluetoothAdapter;
////		mContext = context;
////	}
////
////	public boolean isScanning() {
////		return mScanning;
////	}
////
////	private void notifyFinished(boolean isFileFound){
////
////		Intent broadcastIntent = new Intent(CustomConstants.Broadcasts.BROADCAST_1.getString());
////		broadcastIntent.putExtra(CustomConstants.Payloads.PAYLOAD_1.getString(), mScanning);
////
////		Logger.i(TAG, "Broadcasting Result");
////		LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcastIntent);
////	}
////
////	public void scanLeDevice(final int duration, final boolean enable) {
////		if (enable) {
////			if(mScanning){return;}
////			Logger.i(TAG, "~ Starting Scan");
////			// Stops scanning after a pre-defined scan period.
////			if(duration > 0){
////				Logger.i(TAG, "~ Will stop after: " + duration);
////				mHandler.postDelayed(
////						new HandlerRunnable(),
////						duration);
////			}
////			mScanning = true;
////			mBluetoothAdapter.startLeScan(mLeScanCallback);
////		} else {
////			Logger.i(TAG, "~ Stopping Scan");
////			mScanning = false;
////			mBluetoothAdapter.stopLeScan(mLeScanCallback);
////			notifyFinished(mScanning);
////		}
////	}
//
//}
