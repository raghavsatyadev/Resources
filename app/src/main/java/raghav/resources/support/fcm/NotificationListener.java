package raghav.resources.support.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import raghav.resources.R;
import raghav.resources.SplashActivity;
import raghav.resources.support.Constants;
import raghav.resources.support.utils.AppLog;
import raghav.resources.support.utils.NotificationUtils;
import raghav.resources.support.utils.ResourceUtils;
import raghav.resources.support.utils.SharedPrefsUtil;

public class NotificationListener extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage packet) {
        String from = packet.getFrom();
        String message;
        if (packet.getData().size() > 0) {
            Map data = packet.getData();
            message = String.valueOf(data.get(Constants.WebService.NotificationKeys.MAIN_KEY));
            if (!TextUtils.isEmpty(message) || message.equals("null")) {
                if (packet.getNotification() != null && !TextUtils.isEmpty(packet.getNotification().getBody())) {
                    message = packet.getNotification().getBody();
                }
            }
        } else if (packet.getNotification() != null && !TextUtils.isEmpty(packet.getNotification().getBody())) {
            message = packet.getNotification().getBody();
        } else {
            message = ResourceUtils.getString(R.string.default_notification_message);
        }
        if (from.startsWith("/topics/")) {
            String topic = from.replace("/topics/", "");
            try {
                if (SharedPrefsUtil.getFCMTopics().contains(topic)) {
                    sendNotification(topic);
                }
            } catch (NullPointerException ignored) {
                AppLog.log(AppLog.D, false, AppLog.TAG, "onMessageReceived" + ignored.getMessage());
            }
        } else {
            if (message != null) {
//                try {
//                    JSONObject messageJSON = new JSONObject(message);

                sendNotification(message);
//                } catch (JSONException e) {
//                    AppLog.log(AppLog.D, false, AppLog.TAG, "onMessageReceived" + e.getMessage());
//                }
            }
        }
    }

    private void sendNotification(String message) {

        int NOTIFICATION_ID = 0;

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = NotificationUtils.buildBigTextNotification(this,
                ResourceUtils.getString(R.string.app_name),
                message,
                pendingIntent);

        if (builder != null) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(0, builder.build());
        }
    }
}
