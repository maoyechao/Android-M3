package com.example.m3app.ui;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private static SharedPreference sharedPreference;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences userSP;
    private final static String KEY = "sp_general";
    private final static String KEY_PERSON = "sp_person";


    private SharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        userSP = context.getSharedPreferences(KEY_PERSON, Context.MODE_PRIVATE);
    }

    public static SharedPreference getInstance(Context context) {
        if (sharedPreference == null) {
            sharedPreference = new SharedPreference(context);
        }
        return sharedPreference;
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long getLong(String key, Long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public float getFloat(String key, Float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    public boolean remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        return editor.commit();
    }

    public boolean removeAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        return editor.commit();
    }
}

