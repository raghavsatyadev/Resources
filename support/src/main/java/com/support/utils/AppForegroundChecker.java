package com.support.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * to use this class put this code in Application class.
 * <p>
 * <p>
 * AppForegroundChecker.Listener listener = new AppForegroundChecker.Listener() {
 * public void onBecameForeground() {
 * Toast.makeText(CoreApp.this, "foreground", Toast.LENGTH_SHORT).show();
 * }
 * <p>
 * public void onBecameBackground() {
 * Toast.makeText(CoreApp.this, "background", Toast.LENGTH_SHORT).show();
 * }
 * };
 * <p>
 * public void onCreate() {
 * super.onCreate();
 * mInstance = this;
 * AppForegroundChecker.get(mInstance).addListener(listener);
 * }
 * public void onTerminate() {
 * AppForegroundChecker.get(getInstance()).removeListener(listener);
 * super.onTerminate();
 * }
 */
public class AppForegroundChecker implements Application.ActivityLifecycleCallbacks {

    public static final long CHECK_DELAY = 500;
    public static final String TAG = AppForegroundChecker.class.getName();
    private static AppForegroundChecker instance;
    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private List<Listener> listeners = new CopyOnWriteArrayList<>();
    private Runnable check;

    public static AppForegroundChecker init(Application application) {
        if (instance == null) {
            instance = new AppForegroundChecker();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static AppForegroundChecker get(Application application) {
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static AppForegroundChecker get(Context ctx) {
        if (instance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            throw new IllegalStateException(
                    "AppForegroundChecker is not initialised and " +
                            "cannot obtain the Application object");
        }
        return instance;
    }

    public static AppForegroundChecker get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "AppForegroundChecker is not initialised - invoke " +
                            "at least once with parametrised init/get");
        }
        return instance;
    }

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground) {
            AppLog.log(false, "AppForegroundChecker " + "onActivityResumed: " + "went foreground");
            for (Listener l : listeners) {
                try {
                    l.onBecameForeground();
                } catch (Exception exc) {
                    AppLog.log(false, "AppForegroundChecker " + "onActivityResumed: " + "Listener threw exception!" + exc.getMessage());
                }
            }
        } else {
            AppLog.log(false, "AppForegroundChecker " + "onActivityResumed: " + "still foreground");
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);

        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    AppLog.log(false, "AppForegroundChecker " + "run: " + "went background");
                    for (Listener l : listeners) {
                        try {
                            l.onBecameBackground();
                        } catch (Exception exc) {
                            AppLog.log(false, "AppForegroundChecker " + "run: " + "Listener threw exception!" + exc.getMessage());
                        }
                    }
                } else {
                    AppLog.log(false, "AppForegroundChecker " + "run: " + "still foreground");
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    public interface Listener {

        void onBecameForeground();

        void onBecameBackground();

    }
}