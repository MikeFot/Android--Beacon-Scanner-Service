package com.michaelfotiadis.ibeaconscanner.processes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;
import com.michaelfotiadis.ibeaconscanner.services.ScanService;
import com.michaelfotiadis.ibeaconscanner.utils.Logger;

public final class ScanHelper {

    private static final String TAG = ScanHelper.class.getSimpleName();

    private ScanHelper() {
        // NOOP
    }

    /**
     * /**
     * Takes 2 time variables and starts a Scan Service
     *
     * @param context          Context calling this method
     * @param scanDurationTime Scan duration in milliseconds
     * @param scanGapTime      Time between consecutive scans in milliseconds
     */
    public static void scanForIBeacons(final Context context, final int scanDurationTime, final int scanGapTime) {
        if (!isAlarmUp(context)) {
            Logger.d(TAG, "Generating service intent");
            Logger.d(TAG, "Scan Time : " + scanDurationTime);
            Logger.d(TAG, "Gap Time : " + scanGapTime);

            cancelService(context);

            Singleton.getInstance().setNumberOfScans(0);

            // Create the service intent
            final Intent serviceIntent = new Intent(context, ScanService.class);
            // Pass time variables to the intent
            serviceIntent.putExtra(CustomConstants.Payloads.PAYLOAD_1.getString(), scanDurationTime);
            serviceIntent.putExtra(CustomConstants.Payloads.PAYLOAD_2.getString(), scanGapTime);

            // Start the service
            Logger.d(TAG, "Attempting to start scan service");
            context.startService(serviceIntent);
        } else {
            Logger.e(TAG, "Not starting Service because alarm is already up");
        }
    }

    public static void cancelService(final Context context) {
        Logger.d(TAG, "Cancelling Service Alarm");
        final Intent repeatingIntent = new Intent(context, ScanService.class);
        final PendingIntent pendingIntent = PendingIntent.getService(context, 0, repeatingIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

    private static boolean isAlarmUp(final Context context) {
        final boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent(context, ScanService.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            Logger.d(TAG, "Alarm is already active");
        } else {
            Logger.d(TAG, "Alarm not set");
        }
        return alarmUp;
    }
}
