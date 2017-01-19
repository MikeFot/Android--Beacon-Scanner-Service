package com.michaelfotiadis.ibeaconscanner.tasks;

import com.michaelfotiadis.ibeaconscanner.datastore.Singleton;

import java.util.Timer;
import java.util.TimerTask;

public class MonitorTask {
	private final OnBeaconDataChangedListener mOnDataChangedListener;

	private Timer mTimer;
	private final int mTimerFrequency = 1000;
	private long mTimeOfLastUpdate = 0;

	public MonitorTask(OnBeaconDataChangedListener onDataChangedListener){
		mOnDataChangedListener = onDataChangedListener;

		// start monitor
		mTimer = new Timer();

		// when something changes, call onMonitoredDataChanged();
	}

	public void start(){
		mTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				long lastUpdate = Singleton.getInstance().getTimeOfLastUpdate();
				
				if (mTimeOfLastUpdate < lastUpdate) {
					mTimeOfLastUpdate = lastUpdate;
					onMonitoredDataChanged();
				}
			}
		}, 0, mTimerFrequency);

	}

	public void stop(){
		mTimer.cancel();
	}

	private void onMonitoredDataChanged(){
		mOnDataChangedListener.onDataChanged();
	}



	public interface OnBeaconDataChangedListener{

		public void onDataChanged();

	}

}
