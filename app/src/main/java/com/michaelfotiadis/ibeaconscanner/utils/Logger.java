package com.michaelfotiadis.ibeaconscanner.utils;

import android.util.Log;

/** 27 Sep 2013
 * Radu Savutiu
 * @author Radu Savutiu
*/
public class Logger {
	private final static boolean LOG_DEBUG = true;
	public static void i(String TAG, String msg) {
		if (LOG_DEBUG) {
			Log.i(TAG, "^" + msg);
		}
	}
	public static void i(String TAG, String msg, Throwable thr) {
		if (LOG_DEBUG) {
			Log.i(TAG, "^" + msg, thr);
		}
	}
	public static void d(String TAG, String msg) {
		if (LOG_DEBUG) {
			Log.d(TAG, "^" + msg);
		}
	}
	public static void d(String TAG, String msg, Throwable thr) {
		if (LOG_DEBUG) {
			Log.d(TAG, "^" + msg, thr);
		}
	}
	public static void e(String TAG, String msg) {
		if (LOG_DEBUG) {
			Log.e(TAG, "^" + msg);
		}
	}
	public static void e(String TAG, String msg, Throwable thr) {
		if (LOG_DEBUG) {
			Log.e(TAG, "^" + msg, thr);
		}
	}
	public static void v(String TAG, String msg) {
		if (LOG_DEBUG) {
			Log.v(TAG, "^" + msg);
		}
	}
	public static void v(String TAG, String msg, Throwable thr) {
		if (LOG_DEBUG) {
			Log.v(TAG, "^" + msg, thr);
		}
	}
	public static void w(String TAG, String msg) {
		if (LOG_DEBUG) {
			Log.v(TAG, "^" + msg);
		}
	}
	public static void w(String TAG, String msg, Throwable thr) {
		if (LOG_DEBUG) {
			Log.v(TAG, "^" + msg, thr);
		}
	}
}