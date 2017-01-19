package com.michaelfotiadis.ibeaconscanner.datastore;

import com.michaelfotiadis.ibeaconscanner.utils.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

/**
 * Singleton Class
 *
 * @author Michael Fotiadis
 * @since 24/06/2014
 */
public class Singleton {

    private static volatile Singleton _instance = null;

    private ConcurrentHashMap<String, BluetoothLeDevice> mDeviceMap;
    private ConcurrentHashMap<String, BluetoothLeDevice> mAvailableDevicesList;
    private ConcurrentHashMap<String, BluetoothLeDevice> mUpdatedDevicesList;
    private ConcurrentHashMap<String, BluetoothLeDevice> mNewDevicesList;
    private ConcurrentHashMap<String, BluetoothLeDevice> mMovingCloserDevicesList;
    private ConcurrentHashMap<String, BluetoothLeDevice> mMovingFartherDevicesList;
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

        mMovingFartherDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();
        mMovingCloserDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();

        mDissapearingDevicesList = new ConcurrentHashMap<String, BluetoothLeDevice>();

        mNumberOfScans = 0;
    }

    public BluetoothLeDevice getBluetoothLeDeviceForAddress(String address) {

        for (BluetoothLeDevice device : mDeviceMap.values()) {
            if (device.getAddress().equals(address)) {
                return device;
            }
        }
        return null;
    }

    /**
     * Receives a Device Map and allocates the devices to the appropriate Singleton Device Map
     *
     * @param inputDeviceMap String (ID) - BluetooLeDevice Map
     */
    public void pruneDeviceList(Map<String, BluetoothLeDevice> inputDeviceMap) {

        reportMapContents();

        mUpdatedDevicesList.clear();
        mNewDevicesList.clear();
        mMovingFartherDevicesList.clear();
        mMovingCloserDevicesList.clear();
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
                    mMovingCloserDevicesList.put(updatedDevice.getAddress(), updatedDevice);
                } else {
                    mMovingFartherDevicesList.put(updatedDevice.getAddress(), updatedDevice);
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
        Logger.d(TAG, "Number of devices moving nearer : " + mMovingCloserDevicesList.size());
        Logger.d(TAG, "Number of devices moving farther away : " + mMovingFartherDevicesList.size());
        Logger.d(TAG, "Number of devices that disappeared : " + mDissapearingDevicesList.size());


    }

    /**
     * @return The time in milliseconds of the last time the singleton was updated
     */
    public long getTimeOfLastUpdate() {
        return mTimeOfLastUpdate;
    }

    /**
     * @return The list of IBeacon devices detected during the last scan
     */
    public ConcurrentHashMap<String, BluetoothLeDevice> getAvailableDevicesList() {
        return mAvailableDevicesList;
    }

    public int getAvailableDeviceListSize() {
        if (mAvailableDevicesList != null) {
            return mAvailableDevicesList.size();
        } else {
            return 0;
        }
    }

    public int getNewDeviceListSize() {
        if (mNewDevicesList != null) {
            return mNewDevicesList.size();
        } else {
            return 0;
        }
    }

    public int getUpdatedDeviceListSize() {
        if (mUpdatedDevicesList != null) {
            return mUpdatedDevicesList.size();
        } else {
            return 0;
        }
    }

    public int getMovingCloserDeviceListSize() {
        if (mMovingCloserDevicesList != null) {
            return mMovingCloserDevicesList.size();
        } else {
            return 0;
        }
    }

    public int getMovingFartherDeviceListSize() {
        if (mMovingFartherDevicesList != null) {
            return mMovingFartherDevicesList.size();
        } else {
            return 0;
        }
    }

    public int getDissappearingDeviceListSize() {
        if (mDissapearingDevicesList != null) {
            return mDissapearingDevicesList.size();
        } else {
            return 0;
        }
    }

    public List<String> getDevicesAvailableAsStringList() {
        List<String> list = new ArrayList<String>();
        for (BluetoothLeDevice device : mAvailableDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    public List<String> getDevicesNewAsStringList() {
        List<String> list = new ArrayList<String>();
        for (BluetoothLeDevice device : mNewDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    public List<String> getDevicesUpdatedAsStringList() {
        List<String> list = new ArrayList<String>();
        for (BluetoothLeDevice device : mUpdatedDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    public List<String> getDevicesMovingCloserAsStringList() {
        List<String> list = new ArrayList<String>();
        for (BluetoothLeDevice device : mMovingCloserDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    public List<String> getDevicesMovingFartherAsStringList() {
        List<String> list = new ArrayList<String>();
        for (BluetoothLeDevice device : mMovingFartherDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

    public List<String> getDevicesDissappearingAsStringList() {
        List<String> list = new ArrayList<String>();
        for (BluetoothLeDevice device : mDissapearingDevicesList.values()) {
            list.add(device.getAddress());
        }
        return list;
    }

}
