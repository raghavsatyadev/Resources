package raghav.resources.support.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import raghav.resources.R;

public class NotificationUtils {
    private static Uri defaultSoundUri;
    private static Bitmap icLauncher;
    private static int notificationColor;
    private static NotificationCompat.Builder builder;

    private static int getSmallIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT);
        return useWhiteIcon ? R.drawable.ic_small_icon : R.drawable.ic_launcher;
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context,
                                                                    String title,
                                                                    String message,
                                                                    String imageURL,
                                                                    PendingIntent pendingIntent) {

        if (builder == null) {
            if (defaultSoundUri == null)
                defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (icLauncher == null)
                icLauncher = ((BitmapDrawable) ResourceUtils.getDrawable(R.drawable.ic_launcher)).getBitmap();
            if (notificationColor == 0) {
                notificationColor = ResourceUtils.getColor(R.color.notification_color);
            }
            builder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setSmallIcon(getSmallIcon())
                    .setLargeIcon(icLauncher)
                    .setSound(defaultSoundUri)
                    .setColor(notificationColor);
        }
        builder.setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setTicker(message);
        return setNotificationStyle(builder, imageURL, title, message);
    }

    private static NotificationCompat.Builder setNotificationStyle(NotificationCompat.Builder builder,
                                                                   String imageURL,
                                                                   String title,
                                                                   String message) {
        if (!TextUtils.isEmpty(imageURL)) {
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigLargeIcon(icLauncher)
                    .setSummaryText(message)
                    .setBigContentTitle(title)
                    .bigPicture(getBitmapFromUrl(imageURL)));
        } else {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }
        return builder;
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
            AppLog.log(false, "NotificationUtils " + "getBitmapFromUrl: ", e);
            return null;
        }
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void sendNotification(Context context, int NOTIFICATION_ID,
                                        String message, String imageURL,
                                        Intent intent, int pendingIntentFlag) {
        NotificationCompat.Builder builder;
        builder = NotificationUtils.getNotificationBuilder(context,
                ResourceUtils.getString(R.string.app_name),
                message,
                imageURL,
                PendingIntent.getActivity(context, NOTIFICATION_ID, intent, pendingIntentFlag));
        if (builder != null) {
            getNotificationManager(context).notify(NOTIFICATION_ID, builder.build());
        }
    }
}