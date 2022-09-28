package com.codecoy.securecalculator;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;

public class App extends Application {
    private FirebaseAnalytics mFirebaseAnalytics;
  public static Fragment albumFragment;
    public static Fragment albumFragmentGrid;

    @Override
    public void onCreate() {
        super.onCreate();

// Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
