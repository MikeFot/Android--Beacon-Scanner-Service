package com.michaelfotiadis.ibeaconscanner.datastore;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;

import com.michaelfotiadis.ibeaconscanner.utils.Logger;

/**
 * Singleton Class
 * @since 24/06/2014
 * @author Michael Fotiadis
 *
 */
public class Singleton {

	private static volatile Singleton _instance = null;

	private ConcurrentHashMap<String, BluetoothLeDevice> mDeviceMap;
	private ConcurrentHashMap<String, BluetoothLeDevice> mAvailableDevicesList;
	private ConcurrentHashMap<String, BluetoothLeDevice> mUpdatedDevicesList;
	private ConcurrentHashMap<String, BluetoothLeDevice> mNewDevicesList;
	private ConcurrentHashMap<String, BluetoothLeDevice> mMovingNearDevicesList;
	private ConcurrentHashMap<String, BluetoothLeDevice> mMovingAwayDevicesList;
	private ConcurrentHashMap<String, BluetoothLeDevice> mDissapearingDevicesList;
	
	private int mNumberOfScans;

	private long mTimeOfLastUpdate = 0;
	
	public int getNumberOfScans() {
		return mNumberOfScans;
	}

	public void setNumberOfScans(int mNumberOfScans) {
		this.mNumberOfScans = mNumberOfScans;
	}

	private final String TAG = "Singleton";

	/**
	 * 
	 * @return Instance of the Singleton
	 */
	public static Singleton getInstance() {
		if (_instance == null) {
			synchronized (Singleton.class) {
				if (_instance == null) {
					_instance = new Singleton();
				}
			}
		}
		return _instance;
	}
	
	/**
	 * 
	 * @param instance Sets the Instance of the Singleton
	 */
	public static void setInstance(Singleton instance) {
		Singleton._instance = instance;
	}

	/**
	 * Singleton Constructor
	 */
	public Singleton() {
		mDeviceMap = new ConcurrentHashMap<String, BluetoothLeDevice>();
		mAvailableDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();
		
		mNewDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();
		mUpdatedDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();

		mMovingAwayDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();
		mMovingNearDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();

		mDissapearingDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();
		
		mNumberOfScans = 0;
	}

	/**
	 * Receives a Device Map and allocates the devices to the appropriate Singleton Device Map
	 * @param inputDeviceMap String (ID) - BluetooLeDevice Map
	 */
	public void pruneDeviceList(Map<String, BluetoothLeDevice> inputDeviceMap) {

		reportMapContents();

		mUpdatedDevicesList.clear();
		mNewDevicesList.clear();
		mMovingAwayDevicesList.clear();
		mMovingNearDevicesList.clear();
		mDissapearingDevicesList.clear();

		for (BluetoothLeDevice originalDevice : mDeviceMap.values()) {
			if (!inputDeviceMap.containsKey(originalDevice.getAddress())) {
				Logger.d(TAG, "Device disappeared : " + originalDevice.getAddress());
				mDissapearingDevicesList.put(originalDevice.getAddress(), originalDevice);
			}
		}

		for (BluetoothLeDevice updatedDevice : inputDeviceMap.values()) {
			mAvailableDevicesList.put(updatedDevice.getAddress(), updatedDevice);
			
			if (mDeviceMap.containsKey(updatedDevice.getAddress())) {

				Logger.d(TAG, "Device has been updated!");
				Logger.d(TAG, "Device : " + updatedDevice.getAddress());
				Logger.d(TAG, "With average RSSI : " + updatedDevice.getRunningAverageRssi());

				BluetoothLeDevice originalDevice = mDeviceMap.get(updatedDevice.getAddress());

				if (updatedDevice.getRunningAverageRssi() <= originalDevice.getRunningAverageRssi()) {
					mMovingNearDevicesList.put(updatedDevice.getAddress(), updatedDevice);
				} else {
					mMovingAwayDevicesList.put(updatedDevice.getAddress(), updatedDevice);
				}

				mDeviceMap.put(updatedDevice.getAddress(), updatedDevice);
				mUpdatedDevicesList.put(updatedDevice.getAddress(), updatedDevice);
			} else {
				Logger.d(TAG, "New Device : " + updatedDevice.getAddress());
				Logger.d(TAG, "With average RSSI : " + updatedDevice.getRunningAverageRssi());

				mDeviceMap.put(updatedDevice.getAddress(), updatedDevice);
				mNewDevicesList.put(updatedDevice.getAddress(), updatedDevice);
			}
		}
		reportMapContents();
		mTimeOfLastUpdate = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Logs the contents of the device hash maps (for debugging purposes)
	 */
	private void reportMapContents() {
		Logger.d(TAG, "***Reporting updated devices");
		Logger.d(TAG, "Total devices in memory : " + mDeviceMap.size());
		for (BluetoothLeDevice originaldevice : mDeviceMap.values()) {
			Logger.d(TAG, "Device : " + originaldevice.getAddress());
			Logger.d(TAG, "With average RSSI : " + originaldevice.getRunningAverageRssi());
			
			try {
				Logger.d(TAG, "With Accuracy : " + new IBeaconDevice(originaldevice).getAccuracy());
			} catch (Exception e) {
				Logger.e(TAG, "Failed to cast IBeacon " + originaldevice.getAddress() + " " + e.getLocalizedMessage());
			}
		
		}
		Logger.d(TAG, "Total devices in memory : " + mDeviceMap.size());
		Logger.d(TAG, "Number of available devices on the last scan : " + mAvailableDevicesList.size());
		Logger.d(TAG, "Number of updated devices on the last scan : " + mUpdatedDevicesList.size());
		Logger.d(TAG, "Number of new devices on the last scan : " + mNewDevicesList.size());
		Logger.d(TAG, "Number of devices moving nearer : " + mMovingNearDevicesList.size());
		Logger.d(TAG, "Number of devices moving farther away : " + mMovingAwayDevicesList.size());
		Logger.d(TAG, "Number of devices that disappeared : " + mDissapearingDevicesList.size());
		

		
	}

	/**
	 * 
	 * @return The time in milliseconds of the last time the singleton was updated 
	 */
	public long getTimeOfLastUpdate() {
		return mTimeOfLastUpdate;
	}

	/**
	 * 
	 * @return The list of IBeacon devices detected during the last scan
	 */
	public ConcurrentHashMap<String, BluetoothLeDevice> getAvailableDevicesList() {
		return mAvailableDevicesList;
	}
	
}
