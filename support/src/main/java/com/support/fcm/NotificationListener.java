package com.support.fcm;

import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.support.Constants;
import com.support.R;
import com.support.utils.AppLog;
import com.support.utils.ResourceUtils;
import com.support.utils.SharedPrefsUtil;

import org.json.JSONException;

import java.util.Map;

public abstract class NotificationListener extends FirebaseMessagingService {

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
                    buildPayload(topic);
                }
            } catch (NullPointerException | JSONException e) {
                AppLog.log(false, "NotificationListener: " + "onMessageReceived: ", e);
            }
        } else {
            if (message != null) {
                try {
                    buildPayload(message);
                } catch (JSONException e) {
                    AppLog.log(false, "NotificationListener: " + "onMessageReceived: ", e);
                }
            }
        }
    }

    public abstract void buildPayload(String message) throws JSONException;
}
