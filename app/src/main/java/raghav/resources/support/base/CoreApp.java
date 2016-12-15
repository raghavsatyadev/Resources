package raghav.resources.support.base;

import android.app.Application;
import android.widget.Toast;

import raghav.resources.support.utils.AppForegroundChecker;



public class CoreApp extends Application {
    private static CoreApp mInstance;

    AppForegroundChecker.Listener listener = new AppForegroundChecker.Listener() {
        public void onBecameForeground() {
            Toast.makeText(CoreApp.this, "foreground", Toast.LENGTH_SHORT).show();
        }

        public void onBecameBackground() {
            Toast.makeText(CoreApp.this, "background", Toast.LENGTH_SHORT).show();
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
