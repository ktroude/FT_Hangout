package dev.ktroude.ft_hangout;

import android.app.Application;

import dev.ktroude.ft_hangout.utils.AppLifecycleTracker;

/**
 * MainApplication serves as the entry point for the application.
 * <p>
 * This class is responsible for initializing global application state and managing the lifecycle tracker
 * to detect when the app goes into the background or returns to the foreground.
 * </p>
 */
public class MainApplication extends Application {

    private static AppLifecycleTracker lifecycleTracker;

    /**
     * Called when the application is first created.
     * <p>
     * This method initializes the {@link AppLifecycleTracker}, which tracks whether the application
     * moves to the background or returns to the foreground.
     * </p>
     */
    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleTracker = new AppLifecycleTracker();
    }

    /**
     * Provides access to the global lifecycle tracker instance.
     * <p>
     * This allows other parts of the application to check whether the app was in the background.
     * </p>
     *
     * @return The singleton instance of {@link AppLifecycleTracker}.
     */
    public static AppLifecycleTracker getLifecycleTracker() {
        return lifecycleTracker;
    }
}
