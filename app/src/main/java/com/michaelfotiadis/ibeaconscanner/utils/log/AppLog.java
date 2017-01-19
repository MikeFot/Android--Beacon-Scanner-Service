package com.michaelfotiadis.ibeaconscanner.utils.log;

import android.util.Log;

import com.michaelfotiadis.ibeaconscanner.BuildConfig;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class AppLog {
    private static final Set<String> CLASSNAME_TO_ESCAPE = getEscapedClassNames();
    private static final boolean INCLUDE_METHOD = BuildConfig.DEV_MODE;
    private static final String LINE_PREFIX = "APP:";
    private static final int MAX_TAG_LENGTH = 50;
    private static final String PACKAGE_PREFIX = BuildConfig.APPLICATION_ID + ".";

    private AppLog() {
        // Avoid instantiation
    }

    public static void v(final String message) {
        vInternal(message);
    }

    public static void i(final String message) {
        iInternal(message);
    }

    public static void d(final String message) {
        dInternal(message);
    }

    public static void e(final String message) {
        eInternal(message, null);
    }

    public static void e(final String message, final Exception e) {
        eInternal(message, e);
    }

    public static void w(final String message) {
        wInternal(message, null);
    }

    public static void w(final String message, final Exception e) {
        wInternal(message, e);
    }

    private static void vInternal(final String message) {
        if (BuildConfig.DEV_MODE) {
            Log.v(calcTag(), calcMessage(message));
        }

    }

    private static void iInternal(final String message) {
        if (BuildConfig.DEV_MODE) {
            Log.i(calcTag(), calcMessage(message));
        }

    }

    private static void dInternal(final String message) {
        if (BuildConfig.DEV_MODE) {
            Log.d(calcTag(), calcMessage(message));
        }
    }

    private static String calcTag() {
        final String caller = getCallingMethod();
        if (caller == null) {
            return "";
        } else {
            final String shortTag = caller.replace(PACKAGE_PREFIX, "");
            final boolean shouldBeShorter = shortTag.length() > MAX_TAG_LENGTH;

            if (shouldBeShorter) {
                final int length = shortTag.length();
                final int start = length - MAX_TAG_LENGTH;
                return shortTag.substring(start, length);
            } else {
                return shortTag;
            }
        }
    }

    private static String calcMessage(final String message) {
        return LINE_PREFIX + message;
    }

    private static String getCallingMethod() {
        final StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        if (stacks != null) {
            for (final StackTraceElement stack : stacks) {
                final String cn = stack.getClassName();
                if (cn != null && !CLASSNAME_TO_ESCAPE.contains(cn)) {
                    if (INCLUDE_METHOD) {
                        return cn + "#" + stack.getMethodName();
                    } else {
                        return cn;
                    }
                }
            }
        }
        return null;
    }

    private static void eInternal(final String message, final Exception e) {
        if (BuildConfig.DEV_MODE) {
            Log.e(calcTag(), calcMessage(message), e);
        }
    }

    private static Set<String> getEscapedClassNames() {
        final Set<String> set = new HashSet<>();

        set.add("java.lang.Thread");
        set.add("dalvik.system.VMStack");
        set.add(Log.class.getName());
        set.add(AppLog.class.getName());

        return set;
    }

    private static void wInternal(final String message, final Exception e) {
        if (BuildConfig.DEV_MODE) {
            Log.w(calcTag(), calcMessage(message), e);
        }
    }
}
