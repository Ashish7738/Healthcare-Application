package com.example.healthcare;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class code {

    // Delay execution for a specified time
    public static void delay(Runnable runnable, int milliseconds) {
        new Handler().postDelayed(runnable, milliseconds);
    }

    // Setup password visibility toggle for EditText fields
    public static void setupPasswordVisibilityToggle(final EditText... passwordFields) {
        final int rightDrawableIndex = 2;

        for (final EditText password : passwordFields) {
            password.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= password.getRight() - password.getCompoundDrawables()[rightDrawableIndex].getBounds().width()) {
                            int selection = password.getSelectionEnd();
                            boolean passwordVisible = isPasswordVisible(password);

                            // Toggle password visibility
                            if (passwordVisible) {
                                setPasswordHidden(password);
                            } else {
                                setPasswordVisible(password);
                            }

                            password.setSelection(selection);
                        }
                    }
                    return false;
                }
            });
        }
    }

    // Check if the password is currently visible
    private static boolean isPasswordVisible(EditText password) {
        return password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance();
    }

    // Set password field to be hidden
    private static void setPasswordHidden(EditText password) {
        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.baseline_visibility_off_24, 0);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    // Set password field to be visible
    private static void setPasswordVisible(EditText password) {
        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.baseline_visibility_24, 0);
        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    // Start activity with shared element transitions
    public static void sharedElementTransitions(Context context, Intent intent, Pair<View, String>[] pairs) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
        context.startActivity(intent, options.toBundle());
    }

    // Shared Preferences related methods for logged-in username
    private static final String PREF_NAME = "loginPrefs";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";
    private static String loggedInUsername;

    public static void setLoggedInUsername(Context context, String username) {
        loggedInUsername = username;

        // Save the username in SharedPreferences for persistent storage
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LOGGED_IN_USERNAME, username);
        editor.apply();
    }

    public static String getLoggedInUsername(Context context) {
        if (loggedInUsername == null) {
            // If not in memory, retrieve from SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            loggedInUsername = preferences.getString(KEY_LOGGED_IN_USERNAME, "");
        }
        return loggedInUsername;
    }

    // Set and get logged-in user type
    public static void setLoggedInUserType(Context context, String userType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userType", userType);
        editor.apply();
    }

    public static String getLoggedInUserType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userType", "");
    }

    // Check if the device is connected to the internet
    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
