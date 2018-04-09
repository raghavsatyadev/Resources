package com.support.notification;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.support.R;
import com.support.utils.AppLog;
import com.support.utils.ResourceUtils;
import com.support.utils.SharedPrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;

public class TokenRefresh extends FirebaseInstanceIdService {
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
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        if ((token == null || token.equals("null")) && !SharedPrefsUtil.isTokenSaved()) {
            SharedPrefsUtil.setFCMTokenStatus(false);
        } else {
            try {
                saveTokenProcess(token);
            } catch (JSONException e) {
                AppLog.log(false, "TokenRefresh: " + "onTokenRefresh: ", e);
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