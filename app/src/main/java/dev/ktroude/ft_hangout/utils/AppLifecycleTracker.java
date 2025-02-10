package dev.ktroude.ft_hangout.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

public class AppLifecycleTracker implements DefaultLifecycleObserver {

    private boolean wasInBackground = false;

    public AppLifecycleTracker() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.d("DEBUG_APP", "Application is in background !");
        wasInBackground = true;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.d("DEBUG_APP", "Application is back !");
    }

    public boolean wasInBackground() {
        boolean result = wasInBackground;
        wasInBackground = false;
        return result;
    }
}
