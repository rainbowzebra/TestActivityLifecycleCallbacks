package com.windward.www.casio_golf_viewer.casio.golf.util;

import android.util.Log;

public class Debug {
	public static final String LOG_TAG = "luzhoudache";
	public static boolean IS_LOG = true;

	/**
	 * If the isLog is true,send a debug log message
	 * 
	 * @param msg
	 *            The message you would like logged.
	 */
	public static void logDebug(String msg) {
		if (IS_LOG) {
			Log.d(LOG_TAG, msg);
		}
	}

	/**
	 * If the isLog is true,send an error log message
	 * 
	 * @param msg
	 *            The message you would like logged.
	 */
	public static void logError(String msg) {
		if (IS_LOG) {
			Log.e(LOG_TAG, msg);
		}
	}

	/**
	 * If the isLog is true,send and info log message.
	 * 
	 * @param msg
	 *            The message you would like logged.
	 */
	public static void logInfo(String msg) {
		if (IS_LOG) {
			Log.i(LOG_TAG, msg);
		}
	}
}
