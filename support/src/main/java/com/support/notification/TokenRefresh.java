package com.support.notification;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.support.R;
import com.support.utils.AppLog;
import com.support.utils.ResourceUtils;
import com.support.utils.SharedPrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.TreeSet;

public class TokenRefresh extends FirebaseInstanceIdService {

    private TokenRefresh context;


    public static void subscribeTopics() {
        TreeSet<String> TOPICS = new TreeSet<>();
        TOPICS.add(makeTopic(ResourceUtils.getString(R.string.app_name)));
        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        for (String topic : TOPICS) {
            pubSub.subscribeToTopic(topic);
        }
        SharedPrefsUtil.setFCMTopics(TOPICS);
    }

    private static String makeTopic(String string) {
        return string.replaceAll("(\\W|^_)*", "");
    }

    public static void subscribeTopics(JSONArray topics) {
        TreeSet<String> TOPICS = new TreeSet<>();
        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        for (int i = 0; i < topics.length(); i++) {
            try {
                String topic = makeTopic(topics.getString(i));
                TOPICS.add(topic);
                pubSub.subscribeToTopic(topic);
            } catch (JSONException e) {
                AppLog.log(false, "TokenRefresh " + "subscribeTopics: ", e);
            }

        }
        SharedPrefsUtil.setFCMTopics(TOPICS);
    }

    public static void unSubscribeTopics() {
        ArrayList<String> strings = new ArrayList<>(SharedPrefsUtil.getFCMTopics());
        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        for (int i = 0; i < strings.size(); i++) {
            pubSub.unsubscribeFromTopic(strings.get(i));
        }
    }

    public static void updateToken(Context context) {
//        if (!SharedPrefsUtil.isTokenSavedServer()
//                && SharedPrefsUtil.isTokenSaved() && !TextUtils.isEmpty(SharedPrefsUtil.getFCMToken())) {
//            if (PermissionUtil.checkPermission(context, PermissionUtil.Permissions.READ_PHONE_STATE)) {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put(Constants.WebService.FCMUpdateKeys.DEVICE_ID, DeviceUtils.getDeviceUid());
//                headers.put(Constants.WebService.FCMUpdateKeys.DEVICE_TOKEN, SharedPrefsUtil.getFCMToken());
//
//                new CustomRequest(context, new StringRequestListener() {
//                    @Override
//                    public void onTaskCompleted(String response) {
//                        SharedPrefsUtil.setTokenSavedServer(true);
//                    }
//
//                    @Override
//                    public void onTaskFailed(String status) {
//                        SharedPrefsUtil.setTokenSavedServer(false);
//                    }
//                }).createRequest(Constants.WebService.DEVICE_LINK, CustomRequest.methodTypes.POST, headers, null, null);
//            }
//        }
    }

    @Override
    public void onTokenRefresh() {
        context = this;

        String token = FirebaseInstanceId.getInstance().getToken();
        if ((token == null || token.equals("null")) && !SharedPrefsUtil.isTokenSaved()) {
            SharedPrefsUtil.setFCMTokenStatus(false);
        } else {
            saveTokenProcess(token);
        }
    }

    private void saveTokenProcess(String token) {
        SharedPrefsUtil.setFCMTokenStatus(true);
        SharedPrefsUtil.setFCMToken(token);
        subscribeTopics();
        SharedPrefsUtil.setTokenSavedServer(false);
        updateToken(context);
    }
}