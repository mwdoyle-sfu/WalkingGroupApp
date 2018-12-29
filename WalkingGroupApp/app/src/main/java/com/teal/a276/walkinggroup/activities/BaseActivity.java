package com.teal.a276.walkinggroup.activities;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.teal.a276.walkinggroup.R;

/**
 * Class that other activities derive off of.
 * Has Shared Error Handling
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected final String SHARED_PREF_LOGGER = "Logger";
    protected final String SHARED_PREF_USERNAME = "userName";
    protected final String SHARED_PREF_PASSWORD = "password";

    protected final String PREFS_NAME = "prefs";
    protected final String CURRENT_THEME = "currentTheme";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currTheme = preferences.getInt(CURRENT_THEME, -1);

        if(currTheme == -1){
            setTheme(R.style.AppTheme_box);
        }else {
            setTheme(currTheme);
        }

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void error(String error) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setPositiveButton(R.string.ok, null);
        alertDialogBuilder.setTitle(R.string.error);
        alertDialogBuilder.setMessage(error);

        alertDialogBuilder.show();
    }

    protected void saveAccountLoginInfo(String email, String password) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_LOGGER, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_PREF_USERNAME, email);
        //only store the password if we have one
        if(password != null) {
            editor.putString(SHARED_PREF_PASSWORD, password);
        }
        editor.apply();
    }
}
