package com.e510.commons.utils;

import android.util.Log;

import com.e510.incidencelibrary.BuildConfig;

public class LogUtil {
    private static final int MAX_TAG_LENGTH = 19;

    private static String LOG_PREFIX = "E510-";
    private static boolean LOGGING_ENABLED = BuildConfig.DEBUG;
    private static int LOG_PREFIX_LENGTH = LOG_PREFIX.length();

    public static void config(String prefix, boolean debug) {
        LOG_PREFIX = prefix;
        LOGGING_ENABLED = debug;
        LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    }

    public static String makeLogTag(Class clazz) {
        return makeLogTag(clazz.getSimpleName());
    }

    public static String makeLogTag(String str) {
        if (str.length() > MAX_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    public static void logD(final String tag, String message) {
        if (LOGGING_ENABLED && message != null) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message);
            }
        }
    }

    public static void logD(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED && message != null) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message, cause);
            }
        }
    }

    public static void logV(final String tag, String message) {
        if (LOGGING_ENABLED && message != null) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message);
            }
        }
    }

    public static void logV(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED && message != null) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message, cause);
            }
        }
    }

    public static void logI(final String tag, String message) {
        if (LOGGING_ENABLED && message != null) {
            Log.i(tag, message);
        }
    }

    public static void logI(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED && message != null) {
            Log.i(tag, message, cause);
        }
    }

    public static void logW(final String tag, String message) {
        if (LOGGING_ENABLED && message != null) {
            Log.w(tag, message);
        }
    }

    public static void logW(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED && message != null) {
            Log.w(tag, message, cause);
        }
    }

    public static void logE(final String tag, String message) {
        if (LOGGING_ENABLED && message != null) {
            Log.e(tag, message);
        }
    }

    public static void logE(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED && message != null) {
            Log.e(tag, message, cause);
        }
    }

    /**
     * Utility class
     */
    private LogUtil() {
    }
}
