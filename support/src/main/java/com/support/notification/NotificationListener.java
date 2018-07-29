package com.support.notification;

import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.support.R;
import com.support.utils.AppLog;
import com.support.utils.ResourceUtils;
import com.support.utils.SharedPrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

import static com.support.Constants.WebService.NotificationKeys.MAIN_KEY;

public abstract class NotificationListener extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage packet) {
        String from = packet.getFrom();
        String message;
        if (packet.getData().size() > 0) {
            Map data = packet.getData();
            message = String.valueOf(data.get(MAIN_KEY));
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

    public static void subscribeTopics() throws JSONException {
        subscribeTopics(new JSONArray());
    }

    private static String makeTopic(String string) {
        return string.replaceAll("(\\W|^_)*", "");
    }

    public static void subscribeTopics(JSONArray topics) throws JSONException {
        unSubscribeTopics();
        topics.put(ResourceUtils.getString(R.string.app_name));
        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        JSONArray modifiedTopics = new JSONArray();
        for (int i = 0; i < topics.length(); i++) {
            try {
                String topic = makeTopic(topics.getString(i));
                modifiedTopics.put(topic);
                pubSub.subscribeToTopic(topic);
            } catch (JSONException e) {
                AppLog.log(false, "TokenRefresh " + "subscribeTopics: ", e);
            }

        }
        SharedPrefsUtil.setFCMTopics(modifiedTopics);
    }

    public static void unSubscribeTopics() throws JSONException {
        JSONArray fcmTopics = new JSONArray(SharedPrefsUtil.getFCMTopics());
        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        for (int i = 0; i < fcmTopics.length(); i++) {
            pubSub.unsubscribeFromTopic(fcmTopics.getString(i));
        }
        SharedPrefsUtil.setFCMTopics(new JSONArray());
    }

    public static void updateToken() {

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        if ((token == null || token.equals("null")) && !SharedPrefsUtil.isTokenSaved()) {
            SharedPrefsUtil.setFCMTokenStatus(false);
        } else {
            try {
                saveTokenProcess(token);
            } catch (JSONException e) {
                AppLog.log(false, "NotificationListener: " + "onNewToken: ", e);
            }
        }
    }

    private void saveTokenProcess(String token) throws JSONException {
        SharedPrefsUtil.setFCMTokenStatus(true);
        SharedPrefsUtil.setFCMToken(token);
        subscribeTopics();
        SharedPrefsUtil.setTokenSavedServer(false);
        updateToken();
    }
}
