package com.michaelfotiadis.ibeaconscanner.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class ScanAsyncTask extends AsyncTask<Void, Void, Void> {

    private final int scanTime;
    private BluetoothManager mBluetoothManager;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private final String TAG = this.toString();

    private ConcurrentHashMap<String, BluetoothLeDevice> mDeviceMap;

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        private final String TAG = "LeScanCallback";

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());

            // Add it to the map if it is an IBeacon
            try {
                new IBeaconDevice(deviceLe);
                if (mDeviceMap.containsKey(deviceLe.getAddress())) {
                    Logger.d(TAG, "Device " + deviceLe.getAddress() + " updated.");
                    mDeviceMap.remove(deviceLe.getAddress());
                    mDeviceMap.put(deviceLe.getAddress(), deviceLe);
                } else {
                    Logger.d(TAG, "Device " + deviceLe.getAddress() + " added.");
                    mDeviceMap.put(deviceLe.getAddress(), deviceLe);
                }
            } catch (Exception e) {
                // Ignore it otherwise
                Logger.e(TAG, deviceLe.getAddress() + " " + e.getLocalizedMessage());
            }
        }
    };

    /**
     * @param scanTime
     * @param c
     */
    public ScanAsyncTask(int scanTime, Context c) {
        this.scanTime = scanTime;
        this.context = c;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        try {
            // Let the thread run for the scan time
            Thread.sleep(scanTime);
        } catch (InterruptedException e) {
            this.cancel(true);
            e.printStackTrace();
        }
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        return null;
    }

    @Override
    protected void onPreExecute() {
        Logger.i(TAG, "onPreExecute");
        // Initialise the device map
        mDeviceMap = new ConcurrentHashMap<String, BluetoothLeDevice>();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void result) {
        Logger.i(TAG, "onPostExecute");
        Logger.d(TAG, "Map contains " + mDeviceMap.size() + " unique devices.");
        Singleton.getInstance().pruneDeviceList(mDeviceMap);

        Intent broadcastIntent = new Intent(CustomConstants.Broadcasts.BROADCAST_2.getString());
        Logger.i(TAG, "Broadcasting Scanning Status Finished");
        context.sendBroadcast(broadcastIntent);

        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        Logger.i(TAG, "onCancelled");
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Void result) {
        Logger.i(TAG, "onCancelled (with result)");
        super.onCancelled(result);
    }

}
