package com.michaelfotiadis.ibeaconscanner.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.michaelfotiadis.ibeaconscanner.R;
import com.michaelfotiadis.ibeaconscanner.adapter.ExpandableListAdapter;
import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;
import com.michaelfotiadis.ibeaconscanner.processes.ScanHelper;
import com.michaelfotiadis.ibeaconscanner.tasks.MonitorTask;
import com.michaelfotiadis.ibeaconscanner.tasks.MonitorTask.OnBeaconDataChangedListener;
import com.michaelfotiadis.ibeaconscanner.utils.BluetoothHelper;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;
import com.michaelfotiadis.ibeaconscanner.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements OnChildClickListener, OnCheckedChangeListener {

    private static final int RESULT_SETTINGS = 1;
    private final String TAG = MainActivity.class.getSimpleName();
    ExpandableListAdapter mListAdapter;
    List<String> mListDataHeader;
    HashMap<String, List<String>> mListDataChild;
    private BluetoothHelper mBluetoothHelper;
    private ExpandableListView mExpandableListView;
    private CharSequence mTextViewContents;
    private MonitorTask mMonitorTask;
    // Receivers
    private ResponseReceiver mScanReceiver;
    private SharedPreferences mSharedPrefs;
    private boolean mIsScanRunning = false;
    private boolean mIsToastScanningNowShown;
    private boolean mIsToastStoppingScanShown;

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {


        if (!isChecked) {
            if (mIsScanRunning) {
                serviceToggle();
            }
        } else {

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionsResultAction() {

                        @Override
                        public void onGranted() {

                            if (!mBluetoothHelper.isBluetoothLeSupported()) {
                                ToastUtils.makeWarningToast(MainActivity.this, getString(R.string.toast_no_le));
                                buttonView.setChecked(false);
                                return;
                            } else {
                                if (!mBluetoothHelper.isBluetoothOn()) {
                                    ScanHelper.cancelService(MainActivity.this);
                                    mBluetoothHelper.askUserToEnableBluetoothIfNeeded();
                                    buttonView.setChecked(false);
                                    return;
                                }
                            }

                            serviceToggle();
                        }

                        @Override
                        public void onDenied(final String permission) {
                            ToastUtils.makeWarningToast(MainActivity.this, getString(R.string.toast_warning_permission_not_granted));
                            buttonView.setChecked(false);
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    public boolean onChildClick(final ExpandableListView parent, final View v,
                                final int groupPosition, final int childPosition, final long id) {

        if (mListDataChild != null) {
            final String address = mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition);
            final Intent intent = new Intent(this, DeviceActivity.class);
            intent.putExtra(
                    CustomConstants.Payloads.PAYLOAD_1.toString(),
                    Singleton.getInstance().getBluetoothLeDeviceForAddress(address));
            startActivity(intent);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.app_name));

        mExpandableListView = (ExpandableListView) findViewById(R.id.listViewResults);
        mExpandableListView.setOnChildClickListener(this);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState != null) {
            mTextViewContents = savedInstanceState.getCharSequence(CustomConstants.Payloads.PAYLOAD_1.toString());
            mIsScanRunning = savedInstanceState.getBoolean(CustomConstants.Payloads.PAYLOAD_2.toString(), false);
            mIsToastScanningNowShown = savedInstanceState.getBoolean(CustomConstants.Payloads.PAYLOAD_4.toString(), false);
            mIsToastStoppingScanShown = savedInstanceState.getBoolean(CustomConstants.Payloads.PAYLOAD_5.toString(), false);
        }

        // initialise Bluetooth utilities
        mBluetoothHelper = new BluetoothHelper(this);

        // monitor the singleton
        registerMonitorTask();

        // Wait for broadcasts from the scanning process
        registerResponseReceiver();
        SuperActivityToast.cancelAllSuperToasts();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final View layout = menu.findItem(R.id.action_toggle).getActionView();
        final SwitchCompat toggle = (SwitchCompat) layout.findViewById(R.id.switchForActionBar);

        toggle.setChecked(mIsScanRunning);
        toggle.setOnCheckedChangeListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // same as using a normal menu
        switch (item.getItemId()) {
            case R.id.action_settings:
                startPreferencesActivity();
                break;
            default:
                break;
        }
        return true;
    }

    public void serviceToggle() {
        SuperActivityToast.cancelAllSuperToasts();

        if (mIsScanRunning) {
            // Cancels the alarms if the scan is already running
            ScanHelper.cancelService(this);
            mIsToastScanningNowShown = false;
            ToastUtils.makeInfoToast(this, getString(R.string.toast_interrupted));
            mIsScanRunning = false;
        } else {
            // This ScanHelper will also cancel all alarms on continuation
            ScanHelper.scanForIBeacons(MainActivity.this, getScanTime(), getPauseTime());
        }
    }

    @Override
    protected void onDestroy() {
        removeReceivers();
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putCharSequence(CustomConstants.Payloads.PAYLOAD_1.toString(), mTextViewContents);
        outState.putBoolean(CustomConstants.Payloads.PAYLOAD_2.toString(), mIsScanRunning);
        outState.putBoolean(CustomConstants.Payloads.PAYLOAD_4.toString(), mIsToastScanningNowShown);
        outState.putBoolean(CustomConstants.Payloads.PAYLOAD_5.toString(), mIsToastStoppingScanShown);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        // Cancel the alarm
        SuperActivityToast.cancelAllSuperToasts();
        ScanHelper.cancelService(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SuperActivityToast.cancelAllSuperToasts();

        handleResume();

    }

    private void handleResume() {

            if (mBluetoothHelper.isBluetoothOn()
                    && mBluetoothHelper.isBluetoothLeSupported()) {
                Logger.i(TAG, "Bluetooth has been activated");
                if (mIsScanRunning) {
                    Logger.d(TAG, "Restarting Scan Service");
                    ScanHelper.scanForIBeacons(MainActivity.this, getScanTime(), getPauseTime());
                }

                if (mIsToastScanningNowShown) {
                    ToastUtils.makeProgressToast(this, getString(R.string.toast_scanning));
                }
            } else {
                SuperActivityToast.cancelAllSuperToasts();
                ToastUtils.makeProgressToast(this, getString(R.string.toast_waiting));
            }
        updateListData();
    }

    protected void removeReceivers() {
        try {
            this.unregisterReceiver(mScanReceiver);
            Logger.d(TAG, "Scan Receiver Unregistered Successfully");
        } catch (final Exception e) {
            Logger.d(
                    TAG,
                    "Scan Receiver Already Unregistered. Exception : "
                            + e.getLocalizedMessage());
        }
    }

    protected void stopMonitorTask() {
        if (mMonitorTask != null) {
            Logger.d(TAG, "Monitor Task paused");
            mMonitorTask.stop();
        }
    }

    private int getPauseTime() {
        final String result = mSharedPrefs.getString(
                getString(R.string.pref_pausetime),
                String.valueOf(getResources().getInteger(R.integer.default_pausetime)));
        return Integer.parseInt(result);
    }

    private int getScanTime() {
        final String result = mSharedPrefs.getString(
                getString(R.string.pref_scantime),
                String.valueOf(getResources().getInteger(R.integer.default_scantime)));
        return Integer.parseInt(result);

    }

    private void notifyDataChanged() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Singleton.getInstance().getAvailableDevicesList() != null) {
                    updateListData();
                }
            }
        });
    }

    private void registerMonitorTask() {
        Logger.d(TAG, "Starting Monitor Task");
        mMonitorTask = new MonitorTask(new OnBeaconDataChangedListener() {
            @Override
            public void onDataChanged() {
                Logger.d(TAG, "Singleton Data Changed");
                notifyDataChanged();
            }
        });
        mMonitorTask.start();
    }

    private void registerResponseReceiver() {
        Logger.d(TAG, "Registering Response Receiver");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CustomConstants.Broadcasts.BROADCAST_1.getString());
        intentFilter.addAction(CustomConstants.Broadcasts.BROADCAST_2.getString());

        mScanReceiver = new ResponseReceiver();
        this.registerReceiver(mScanReceiver, intentFilter);
    }

    private void startPreferencesActivity() {
        Logger.d(TAG, "Starting Settings Activity");
        final Intent intent = new Intent(this, ScanPreferencesActivity.class);
        startActivityForResult(intent, RESULT_SETTINGS);
    }

    private void updateListData() {

        Logger.d(TAG, "Updating List Data");
        mListDataHeader = new ArrayList<>();
        mListDataHeader.add("Available Devices (" + Singleton.getInstance().getAvailableDeviceListSize() + ")");
        mListDataHeader.add("New Devices (" + Singleton.getInstance().getNewDeviceListSize() + ")");
        mListDataHeader.add("Updated Devices (" + Singleton.getInstance().getUpdatedDeviceListSize() + ")");
        mListDataHeader.add("Moving Closer Devices (" + Singleton.getInstance().getMovingCloserDeviceListSize() + ")");
        mListDataHeader.add("Moving Farther Device (" + Singleton.getInstance().getMovingFartherDeviceListSize() + ")");
        mListDataHeader.add("Disappearing Devices (" + Singleton.getInstance().getDisappearingDeviceListSize() + ")");

        mListDataChild = new HashMap<>();
        mListDataChild.put(mListDataHeader.get(0), Singleton.getInstance().getDevicesAvailableAsStringList());
        mListDataChild.put(mListDataHeader.get(1), Singleton.getInstance().getDevicesNewAsStringList());
        mListDataChild.put(mListDataHeader.get(2), Singleton.getInstance().getDevicesUpdatedAsStringList());
        mListDataChild.put(mListDataHeader.get(3), Singleton.getInstance().getDevicesMovingCloserAsStringList());
        mListDataChild.put(mListDataHeader.get(4), Singleton.getInstance().getDevicesMovingFartherAsStringList());
        mListDataChild.put(mListDataHeader.get(5), Singleton.getInstance().getDevicesDisappearingAsStringList());

        mListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
        Logger.d(TAG, "Setting Adapter");
        mExpandableListView.setAdapter(mListAdapter);
    }

    public class ResponseReceiver extends BroadcastReceiver {
        private final String TAG = ResponseReceiver.class.getSimpleName();

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Logger.d(TAG, "On Receiver Result");
            if (intent.getAction().equalsIgnoreCase(
                    CustomConstants.Broadcasts.BROADCAST_1.getString())) {
                Logger.i(TAG, "Scan Running");
                SuperActivityToast.cancelAllSuperToasts();
                mIsScanRunning = true;
                mIsToastScanningNowShown = true;
                ToastUtils.makeProgressToast(MainActivity.this, getString(R.string.toast_scanning));

            } else if (intent.getAction().equalsIgnoreCase(
                    CustomConstants.Broadcasts.BROADCAST_2.getString())) {
                Logger.i(TAG, "Service Finished");
                SuperActivityToast.cancelAllSuperToasts();
                mIsToastScanningNowShown = false;
                //				isToastStoppingScanShown = false;
                ToastUtils.makeInfoToast(MainActivity.this, getString(R.string.toast_completed));
            }
        }
    }

}
