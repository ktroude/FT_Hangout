package dev.ktroude.ft_hangout;

import android.app.Application;

import dev.ktroude.ft_hangout.utils.AppLifecycleTracker;

public class MainApplication extends Application {

    private static AppLifecycleTracker lifecycleTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleTracker = new AppLifecycleTracker();
    }

    public static AppLifecycleTracker getLifecycleTracker() {
        return lifecycleTracker;
    }
}
