package com.support.base;

import android.app.Application;

import com.support.utils.AppForegroundChecker;
import com.support.utils.Toaster;

public class CoreApp extends Application {
    private static CoreApp mInstance;

    AppForegroundChecker.Listener listener = new AppForegroundChecker.Listener() {
        public void onBecameForeground() {
            Toaster.shortToast("foreground");
        }

        public void onBecameBackground() {
            Toaster.shortToast("background");
        }
    };

    public static synchronized CoreApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppForegroundChecker.get(mInstance).addListener(listener);
    }

    @Override
    public void onTerminate() {
        AppForegroundChecker.get(getInstance()).removeListener(listener);
        super.onTerminate();
    }
}
