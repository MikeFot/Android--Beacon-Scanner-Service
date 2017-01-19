package com.michaelfotiadis.ibeaconscanner.utils;

import android.app.Activity;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;


public final class ToastUtils {

    private ToastUtils() {
        // NOOP
    }

    public static SuperActivityToast makeProgressToast(final Activity activity, final String message) {

        SuperActivityToast.cancelAllSuperToasts();

        final SuperActivityToast superActivityToast = new SuperActivityToast(activity, Style.TYPE_PROGRESS_BAR);

        superActivityToast.setAnimations(Style.ANIMATIONS_FADE);
        superActivityToast.setDuration(Style.DURATION_SHORT);
        superActivityToast.setColor(Style.blue().color);
        superActivityToast.setText(message);
        superActivityToast.setTextSize(Style.TEXTSIZE_MEDIUM);
        superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();

        return superActivityToast;
    }

    public static SuperActivityToast makeInfoToast(final Activity activity, final String message) {

        SuperActivityToast.cancelAllSuperToasts();

        final SuperActivityToast superActivityToast = new SuperActivityToast(activity, Style.TYPE_STANDARD);

        superActivityToast.setAnimations(Style.ANIMATIONS_FADE);
        superActivityToast.setDuration(Style.DURATION_SHORT);
        superActivityToast.setColor(Style.green().color);
        superActivityToast.setText(message);
        superActivityToast.setTextSize(Style.TEXTSIZE_MEDIUM);
        superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();

        return superActivityToast;
    }

    public static SuperActivityToast makeWarningToast(final Activity activity, final String message) {

        SuperActivityToast.cancelAllSuperToasts();

        final SuperActivityToast superActivityToast = new SuperActivityToast(activity, Style.TYPE_STANDARD);

        superActivityToast.setAnimations(Style.ANIMATIONS_FADE);
        superActivityToast.setDuration(Style.DURATION_SHORT);
        superActivityToast.setColor(Style.orange().color);
        superActivityToast.setText(message);
        superActivityToast.setTextSize(Style.TEXTSIZE_MEDIUM);
        superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();

        return superActivityToast;
    }

}
