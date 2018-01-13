package raghav.resources.support.notification;

import android.app.PendingIntent;
import android.content.Intent;

import raghav.resources.ui.activity.SplashActivity;

public class NotificationListener extends com.support.notification.NotificationListener {

    public void buildPayload(String message) {

        int NOTIFICATION_ID = 0;

        Intent intent = new Intent(this, SplashActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        new NotificationUtils().sendNotification(this, NOTIFICATION_ID, null, message, null, null,
                intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
