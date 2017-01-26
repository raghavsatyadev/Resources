package raghav.resources.support.utils;

import raghav.resources.R;

/**
 * Created by resources on 26/1/17.
 */

public class NotificationUtils {

    public static int getSmallIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? /*R.drawable.ic_small_icon*/ 0 : R.mipmap.ic_launcher;
    }
}
