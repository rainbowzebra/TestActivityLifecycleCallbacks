package com.windward.www.casio_golf_viewer.casio.golf.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtil {
    public static final String APP_FIRST_FLAG = "first";

    public static String getAppFirstFlag(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String flag=preferences.getString(APP_FIRST_FLAG, "");
        return flag;
    }

    public static void saveAppFirstFlag(Context context, String flag) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(APP_FIRST_FLAG, flag);
        editor.commit();
    }

}
