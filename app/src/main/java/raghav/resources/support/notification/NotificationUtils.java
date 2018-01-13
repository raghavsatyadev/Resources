package raghav.resources.support.notification;

import raghav.resources.R;

public class NotificationUtils extends com.support.notification.NotificationUtils {

    public int getSmallIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT);
        return useWhiteIcon ? R.drawable.ic_small_icon : R.mipmap.ic_launcher;
    }

    @Override
    public int getBigIcon() {
        return R.mipmap.ic_launcher;
    }
}