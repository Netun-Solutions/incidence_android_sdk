package com.e510.commons.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {

    public static String loadData(Context context, String prefName)
    {
        return loadData(context, prefName, null);
    }
    public static String loadData(Context context, String prefName, String defaultValue)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(prefName, defaultValue);
    }

    public static void saveData(Context context, String prefName, String prefValue)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(prefName, prefValue);
        prefEditor.commit();
    }

    public static void removeData(Context context, String prefName)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.remove(prefName);
        prefEditor.commit();
    }

    public static void removeAllData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.clear();
        prefEditor.commit();
    }
}
