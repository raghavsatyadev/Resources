package com.support.notification;

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

import com.support.R;
import com.support.utils.AppLog;
import com.support.utils.ResourceUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class NotificationUtils {
    private static Uri defaultSoundUri;
    private static Bitmap icLauncher;
    private static int notificationColor;
    private static NotificationCompat.Builder builder;
    private String channelId;

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

    public abstract int getSmallIcon();

    public abstract int getBigIcon();

    public NotificationCompat.Builder getNotificationBuilder(Context context,
                                                             String title,
                                                             String message,
                                                             String imageURL,
                                                             String channelId,
                                                             PendingIntent pendingIntent) {

        if (this.channelId == null)
            this.channelId = ResourceUtils.getString(R.string.default_notification_channel_id);
        if (defaultSoundUri == null)
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (icLauncher == null)
            icLauncher = ((BitmapDrawable) ResourceUtils.getDrawable(getBigIcon())).getBitmap();
        if (notificationColor == 0) {
            notificationColor = ResourceUtils.getColor(R.color.notification_color);
        }

        builder = new NotificationCompat.Builder(context, !TextUtils.isEmpty(channelId) ? channelId : this.channelId)
                .setAutoCancel(true)
                .setSmallIcon(getSmallIcon())
                .setLargeIcon(icLauncher)
                .setSound(defaultSoundUri)
                .setColor(notificationColor)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setTicker(message);
        return setNotificationStyle(builder, imageURL, title, message);
    }

    public void sendNotification(Context context, int NOTIFICATION_ID, String title,
                                 String message, String imageURL, String channelId,
                                 Intent intent, int pendingIntentFlag) {
        NotificationCompat.Builder builder;
        builder = getNotificationBuilder(context,
                !TextUtils.isEmpty(title) ? title : ResourceUtils.getString(R.string.app_name),
                message,
                imageURL,
                channelId,
                PendingIntent.getActivity(context, NOTIFICATION_ID, intent, pendingIntentFlag));
        if (builder != null) {
            getNotificationManager(context).notify(NOTIFICATION_ID, builder.build());
        }
    }
}