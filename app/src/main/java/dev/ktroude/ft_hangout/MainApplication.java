package dev.ktroude.ft_hangout;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import dev.ktroude.ft_hangout.utils.AppLifecycleTracker;


/**
 * MainApplication serves as the entry point for the application.
 * This class initializes global application state and manages the lifecycle tracker
 * to detect when the app goes into the background or returns to the foreground.
 */
public class MainApplication extends Application {

    private static AppLifecycleTracker lifecycleTracker;
    private static MainApplication instance;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = this;
        lifecycleTracker = new AppLifecycleTracker();
    }

    /**
     * Provides access to the global lifecycle tracker instance.
     * This allows other parts of the application to check whether the app was in the background.
     *
     * @return The singleton instance of {@link AppLifecycleTracker}.
     */
    public static AppLifecycleTracker getLifecycleTracker() {
        return lifecycleTracker;
    }

    /**
     * Displays a global toast message that can be called from anywhere in the app.
     * Ensures execution on the UI thread.
     *
     * @param message The message to display in the toast.
     */
    public static void showGlobalToast(String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Toast.makeText(instance.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    public static Context getAppContext() {
        return appContext;
    }
}
