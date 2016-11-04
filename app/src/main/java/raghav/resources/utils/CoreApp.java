package raghav.resources.utils;

import android.app.Application;

/**
 * Created by raghav on 4/11/16.
 */

public class CoreApp extends Application {
    private static CoreApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized CoreApp getInstance() {
        return mInstance;
    }
}
