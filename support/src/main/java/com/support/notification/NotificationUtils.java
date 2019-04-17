package com.support.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.support.R;
import com.support.utils.ResourceUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public abstract class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();
    private static final int MINIMUM = 1;

    private static NotificationCompat.Builder setNotificationStyle(NotificationCompat.Builder builder,
                                                                   Bitmap icLauncher, String imageURL,
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
            Log.e(TAG, "getBitmapFromUrl: ", e);
            return null;
        }
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public abstract int getSmallIcon();

    public abstract int getBigIcon();

    @NonNull
    private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String getNotificationChannel(NotificationManager notificationManager, String channelId) {
        String id = TextUtils.isEmpty(channelId) ? ResourceUtils.getString(R.string.channel_id) : channelId;

        // The user-visible name of the channel.
        String channelName = ResourceUtils.getString(R.string.channel_name);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(id, channelName, importance);

        notificationManager.createNotificationChannel(mChannel);
        return id;
    }

    public static int getRandomNotificationID() {
        return new Random().nextInt(Integer.MAX_VALUE - MINIMUM) + MINIMUM;
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context,
                                                              String title,
                                                              String message,
                                                              String imageURL,
                                                              String channelId,
                                                              PendingIntent pendingIntent) {

        if (!TextUtils.isEmpty(channelId))
            channelId = ResourceUtils.getString(R.string.channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icLauncher = getBitmapFromDrawable(ResourceUtils.getDrawable(getBigIcon()));
        int notificationColor = ResourceUtils.getColor(R.color.notification_color);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setAutoCancel(true)
                .setSmallIcon(getSmallIcon())
                .setLargeIcon(icLauncher)
                .setSound(defaultSoundUri)
                .setColor(notificationColor)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setTicker(message);

        return setNotificationStyle(builder, icLauncher, imageURL, title, message);
    }

    public void sendNotification(Context context, int NOTIFICATION_ID, String title,
                                 String message, String imageURL, String channelId,
                                 Intent intent, int pendingIntentFlag) {
        NotificationManager notificationManager = getNotificationManager(context);
        String appName = ResourceUtils.getString(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = getNotificationChannel(notificationManager, channelId);
        }

        NotificationCompat.Builder builder;
        builder = getNotificationBuilder(context,
                !TextUtils.isEmpty(title) ? title : appName,
                message,
                imageURL,
                channelId,
                PendingIntent.getActivity(context, NOTIFICATION_ID, intent, pendingIntentFlag));
        if (builder != null) {

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}