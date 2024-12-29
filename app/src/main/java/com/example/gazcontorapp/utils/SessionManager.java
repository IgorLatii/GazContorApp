package com.example.gazcontorapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CONTRACT_NUMBER = "contractNumber";
    private static final String KEY_METER_NUMBER = "meterNumber";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getContractNumber() {
        return sharedPreferences.getString(KEY_CONTRACT_NUMBER, "No data");
    }

    public String getMeterNumber() {
        return sharedPreferences.getString(KEY_METER_NUMBER, "No data");
    }

    public void saveLoginDetails(String contractNumber, String meterNumber) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_CONTRACT_NUMBER, contractNumber);
        editor.putString(KEY_METER_NUMBER, meterNumber);
        editor.apply(); // Saving data
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}

