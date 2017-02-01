package raghav.resources.support.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import raghav.resources.R;

/**
 * Created by resources on 26/1/17.
 */

public class NotificationUtils {

    private static int getSmallIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT);
        return useWhiteIcon ? R.drawable.ic_small_icon : R.mipmap.ic_launcher;
    }

    public static NotificationCompat.Builder buildBigTextNotification(Context context, @NonNull String title,
                                                                      @NonNull String message,
                                                                      PendingIntent pendingIntent) {
        try {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            return new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSmallIcon(getSmallIcon())
                    .setLargeIcon(((BitmapDrawable) ResourceUtils.getDrawable(R.mipmap.ic_launcher)).getBitmap())
                    .setSound(defaultSoundUri)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setColor(ResourceUtils.getColor(R.color.icon_background))
                    .setTicker(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        } catch (NullPointerException e) {
            AppLog.log(AppLog.D, true, AppLog.TAG, "buildBigTextNotification" + e.getMessage());
        }
        return null;
    }

    public static NotificationCompat.Builder buildBigPictureNotification(Context context, @NonNull String title,
                                                                         @NonNull String message, String imageURL,
                                                                         PendingIntent pendingIntent) {
        try {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSmallIcon(getSmallIcon())
                    .setLargeIcon(((BitmapDrawable) ResourceUtils.getDrawable(R.mipmap.ic_launcher)).getBitmap())
                    .setSound(defaultSoundUri)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setColor(ResourceUtils.getColor(R.color.icon_background))
                    .setTicker(message);
            if (!TextUtils.isEmpty(imageURL)) {
                builder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigLargeIcon(((BitmapDrawable) ResourceUtils.getDrawable(R.mipmap.ic_launcher)).getBitmap())
                        .setSummaryText(message)
                        .setBigContentTitle(title)
                        .bigPicture(getBitmapFromUrl(imageURL)));
            } else {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            }
            return builder;
        } catch (NullPointerException e) {
            AppLog.log(AppLog.D, true, AppLog.TAG, "buildBigPictureNotification" + e.getMessage());
        }
        return null;
    }

    private static Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            AppLog.log(AppLog.D, false, AppLog.TAG, "getBitmapFromUrl" + e.getMessage());
            return null;
        }
    }
}