package com.michaelfotiadis.ibeaconscanner.utils;

import android.app.Activity;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;

public class ToastUtils {

	protected final static String TAG = "TOAST UTILITIES";

	public static SuperActivityToast makeProgressToast(Activity activity, SuperActivityToast superActivityToast, String message) {
		SuperActivityToast.cancelAllSuperActivityToasts();
		Logger.d(TAG, "Making Progress Toast");

		superActivityToast = new SuperActivityToast(activity,
				SuperToast.Type.PROGRESS);

		superActivityToast.setAnimations(SuperToast.Animations.FADE);
		superActivityToast.setDuration(SuperToast.Duration.LONG);
		superActivityToast.setBackground(SuperToast.Background.BLUE);
		superActivityToast.setText(message);
		superActivityToast.setTextSize(SuperToast.TextSize.MEDIUM);
		superActivityToast.setIndeterminate(true);

		superActivityToast.show();
		return superActivityToast;
	}

	public static SuperActivityToast  makeInfoToast(Activity activity, String message) {
		SuperActivityToast.cancelAllSuperActivityToasts();
		Logger.d(TAG, "Making Info Toast");

		SuperActivityToast superActivityToast = new SuperActivityToast(activity,
				SuperToast.Type.STANDARD);

		superActivityToast.setAnimations(SuperToast.Animations.FADE);
		superActivityToast.setDuration(SuperToast.Duration.SHORT);
		superActivityToast.setBackground(SuperToast.Background.GREEN);
		superActivityToast.setText(message);
		superActivityToast.setTextSize(SuperToast.TextSize.MEDIUM);
		superActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);

		superActivityToast.show();
		return superActivityToast;
	}

	public static SuperActivityToast  makeWarningToast(Activity activity, String message) {
		SuperActivityToast.cancelAllSuperActivityToasts();
		Logger.d(TAG, "Making Warning Toast");

		SuperActivityToast superActivityToast = new SuperActivityToast(activity,
				SuperToast.Type.STANDARD);

		superActivityToast.setAnimations(SuperToast.Animations.FADE);
		superActivityToast.setDuration(SuperToast.Duration.SHORT);
		superActivityToast.setBackground(SuperToast.Background.ORANGE);
		superActivityToast.setText(message);
		superActivityToast.setTextSize(SuperToast.TextSize.MEDIUM);
		superActivityToast.setIcon(SuperToast.Icon.Dark.EXIT, SuperToast.IconPosition.LEFT);

		superActivityToast.show();
		return superActivityToast;
	}

}
