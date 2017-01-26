package raghav.resources.support.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import raghav.resources.R;

/**
 * Created by resources on 26/1/17.
 */

public class NotificationUtils {

    public static int getSmallIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? /*R.drawable.ic_small_icon*/ 0 : R.mipmap.ic_launcher;
    }

    public static NotificationCompat.Builder buildNotification(Context context, String message, PendingIntent pendingIntent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat.Builder(context)
                .setContentTitle(ResourceUtils.getString(R.string.app_name))
                .setAutoCancel(true)
                .setSmallIcon(getSmallIcon())
                .setLargeIcon(((BitmapDrawable) ContextCompat.getDrawable(context, R.mipmap.ic_launcher)).getBitmap())
                .setSound(defaultSoundUri)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setColor(ResourceUtils.getColor(R.color.icon_background))
                .setTicker(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    }
}
