package raghav.resources.utils;

import android.support.annotation.NonNull;
import android.util.Log;

public class AppLog {

    public final static int V = 4, D = 1, E = 2, I = 3;
    public static final String TAG = AppLog.class.getSimpleName();

    public static void log(int logLevel, String tag, @NonNull String message) {
        boolean isDebug = true;
        if (isDebug) {
            if (tag == null || tag.equals("")) {
                tag = TAG;
            }
            switch (logLevel) {
                case D:
                    Log.d(tag, message);
                    break;
                case V:
                    Log.v(tag, message);
                    break;
                case E:
                    Log.e(tag, message);
                    break;
                case I:
                    Log.i(tag, message);
                    break;
                default:
                    Log.v(tag, message);
            }
        }
    }
}
