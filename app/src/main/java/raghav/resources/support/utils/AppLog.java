package raghav.resources.support.utils;

import android.support.annotation.NonNull;
import android.util.Log;

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
//                    if (!local) Mint.logEvent(message, MintLogLevel.Debug);
                    Log.d(tag, message);
                    break;
                case V:
//                    if (!local) Mint.logEvent(message, MintLogLevel.Verbose);
                    Log.v(tag, message);
                    break;
                case E:
//                    if (!local) Mint.logEvent(message, MintLogLevel.Error);
                    Log.e(tag, message);
                    break;
                case I:
//                    if (!local) Mint.logEvent(message, MintLogLevel.Info);
                    Log.i(tag, message);
                    break;
                default:
//                    if (!local) Mint.logEvent(message);
                    Log.v(tag, message);
            }
        }
    }
}
