package raghav.resources.support.notification;

import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import raghav.resources.ui.activity.SplashActivity;

public class NotificationListener extends com.support.notification.NotificationListener {

    @Override
    public void traverseMessage(String response) {
        try {
            if (!TextUtils.isEmpty(response)) {
                String title = null, message = null, imageURL = null;
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("title")) {
                    title = jsonObject.getString("title");
                }
                if (jsonObject.has("description")) {
                    message = jsonObject.getString("description");
                }
                if (jsonObject.has("image_url")) {
                    imageURL = jsonObject.getString("image_url");
                }
                sendMessage(title, message, imageURL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String title, String message, String imageURL) {
        int NOTIFICATION_ID = NotificationUtils.getRandomNotificationID();

        Intent intent = new Intent(this, SplashActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        new NotificationUtils().sendNotification(this,
                NOTIFICATION_ID,
                title,
                message,
                imageURL,
                null,
                intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
