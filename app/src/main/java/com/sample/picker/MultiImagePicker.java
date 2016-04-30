package com.sample.picker;

import android.app.Application;

import com.facebook.FacebookSdk;


public class MultiImagePicker extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initializing Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

}
