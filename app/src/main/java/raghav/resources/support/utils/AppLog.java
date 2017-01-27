package raghav.resources.support.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class AppLog {

    public final static int V = 4, D = 1, E = 2, I = 3;
    public static final String TAG = AppLog.class.getSimpleName();

    public static void log(int logLevel, boolean local, String tag, @NonNull String message) {
        boolean isDebug = true;
        if (isDebug) {
            if (tag == null || tag.equals("")) {
                tag = TAG;
            }
            switch (logLevel) {
                case D:
//                    if (!local) Crashlytics.log(Log.DEBUG, TAG, message);
                    Log.d(tag, message);
                    break;
                case V:
//                    if (!local) Crashlytics.log(Log.VERBOSE, TAG, message);
                    Log.v(tag, message);
                    break;
                case E:
//                    if (!local) Crashlytics.log(Log.ERROR, TAG, message);
                    Log.e(tag, message);
                    break;
                case I:
//                    if (!local) Crashlytics.log(Log.INFO, TAG, message);
                    Log.i(tag, message);
                    break;
                default:
                    if (!local) Crashlytics.log(Log.WARN, TAG, message);
                    Log.v(tag, message);
            }
        }
    }
}
