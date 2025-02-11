package dev.ktroude.ft_hangout.utils;

import static dev.ktroude.ft_hangout.MainApplication.getAppContext;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dev.ktroude.ft_hangout.MainApplication;
import dev.ktroude.ft_hangout.R;

/**
 * Tracks the lifecycle of the application to detect when it moves between foreground and background.
 * It helps determine whether the app was in the background and has returned to the foreground.
 */
public class AppLifecycleTracker implements DefaultLifecycleObserver {

    private boolean wasInBackground = false;
    private long lastExitTime = 0;

    /**
     * Constructor that registers this tracker as an observer of the application's lifecycle.
     * It starts monitoring when the class is instantiated.
     */
    public AppLifecycleTracker() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Called when the app moves to the background (i.e., no activity is visible).
     * This sets a flag to indicate that the app was in the background and saves the timestamp.
     *
     * @param owner The lifecycle owner (not used in this implementation).
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.d("DEBUG_APP", "Application is in background !");
        wasInBackground = true;
        lastExitTime = System.currentTimeMillis();
    }

    /**
     * Called when the app moves to the foreground (i.e., at least one activity is visible).
     * If the app was in the background, it displays a toast with the elapsed time.
     *
     * @param owner The lifecycle owner (not used in this implementation).
     */
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.d("DEBUG_APP", "Application is back !");

        if (wasInBackground) {
            wasInBackground = false; // Reset flag
            showTimeInBackgroundToast();
        }
    }

    /**
     * Displays a toast showing how long the app was in the background.
     */
    private void showTimeInBackgroundToast() {
        if (lastExitTime != 0) {

            long diffInMillis = System.currentTimeMillis() - lastExitTime;
            long diffInSeconds = diffInMillis / 1000;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(new Date(lastExitTime));

            String toastMsg = formattedDate + "\n" + getAppContext().getString(R.string.toast_pause) + " " + diffInSeconds + " sec";
            MainApplication.showGlobalToast(toastMsg);
        }
    }
}
