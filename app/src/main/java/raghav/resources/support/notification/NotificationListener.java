package raghav.resources.support.notification;

import android.app.PendingIntent;
import android.content.Intent;

import raghav.resources.ui.activity.MainActivity;

public class NotificationListener extends com.support.fcm.NotificationListener {

    public void buildPayload(String message) {

        int NOTIFICATION_ID = 0;

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        new NotificationUtils().sendNotification(this, NOTIFICATION_ID, message, null,
                intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
