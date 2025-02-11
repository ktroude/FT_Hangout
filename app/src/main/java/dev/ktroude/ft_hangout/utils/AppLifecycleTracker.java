package dev.ktroude.ft_hangout.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

/**
 * Tracks the lifecycle of the application to detect when it moves between foreground and background.
 * It helps determine whether the app was in the background and has returned to the foreground.
 */
public class AppLifecycleTracker implements DefaultLifecycleObserver {

    private boolean wasInBackground = false;

    /**
     * Constructor that registers this tracker as an observer of the application's lifecycle.
     * It starts monitoring when the class is instantiated.
     */
    public AppLifecycleTracker() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Called when the app moves to the background (i.e., no activity is visible).
     * This sets a flag to indicate that the app was in the background.
     *
     * @param owner The lifecycle owner (not used in this implementation).
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.d("DEBUG_APP", "Application is in background !");
        wasInBackground = true;
    }

    /**
     * Called when the app moves to the foreground (i.e., at least one activity is visible).
     * Logs that the application has returned, but does not reset the background flag.
     *
     * @param owner The lifecycle owner (not used in this implementation).
     */
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.d("DEBUG_APP", "Application is back !");
    }

    /**
     * Checks if the app was in the background and resets the flag.
     *
     * @return {@code true} if the app was previously in the background, {@code false} otherwise.
     */
    public boolean wasInBackground() {
        boolean result = wasInBackground;
        wasInBackground = false; // Reset flag after checking
        return result;
    }
}

