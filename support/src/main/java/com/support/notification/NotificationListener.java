package com.support.notification;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.support.Constants;
import com.support.R;
import com.support.utils.ResourceUtils;
import com.support.utils.SharedPrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

public abstract class NotificationListener extends FirebaseMessagingService {

    private static final String TAG = NotificationListener.class.getSimpleName();

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
                Log.e(TAG, "subscribeTopics: ", e);
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
        // TODO: 09-Mar-19 token push
    }

    public abstract void traverseMessage(String response);

    @Override
    public void onMessageReceived(RemoteMessage packet) {
        String from = packet.getFrom();
        String message = null;
        if (packet.getData().size() > 0) {
            try {
                message = getMessageFromPacket(packet);
            } catch (JSONException e) {
                Log.e(TAG, "onMessageReceived: ", e);
            }
        } else if (packet.getNotification() != null && !TextUtils.isEmpty(packet.getNotification().getBody())) {
            message = packet.getNotification().getBody();
        } else {
            message = ResourceUtils.getString(R.string.default_notification_message);
        }
        if (from != null) {
            if (from.startsWith("/topics/")) {
                String topic = from.replace("/topics/", "");
                try {
                    if (SharedPrefsUtil.getFCMTopics().contains(topic)) {
                        traverseMessage(message);
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onMessageReceived: ", e);
                }
            } else {
                if (message != null) {
                    traverseMessage(message);
                }
            }
        }
    }

    private String getMessageFromPacket(RemoteMessage packet) throws JSONException {
        Map<String, String> data = packet.getData();
        String message = String.valueOf(data.get(Constants.WebService.NotificationKeys.MAIN_KEY));
        if (TextUtils.isEmpty(message) || message.equals("null")) {
            Iterator<String> keys = data.keySet().iterator();
            JSONObject jsonObject = new JSONObject();
            if (keys.hasNext()) {
                while (keys.hasNext()) {
                    String key = keys.next();
                    jsonObject.put(key, data.get(key));
                }
                message = jsonObject.toString();
            }
        }
        return message;
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
                Log.e(TAG, "onNewToken: ", e);
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
