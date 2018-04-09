package com.support.utils;

import org.json.JSONArray;

public class SharedPrefsUtil {

    public static String getFCMTopics() {
        return SharedPrefsHelper.getInstance().get(FCM.TOPICS, "[]");
    }

    public static void setFCMTopics(JSONArray topics) {
        SharedPrefsHelper.getInstance().save(FCM.TOPICS, topics.toString());
    }

    public static void setFCMTokenStatus(boolean status) {
        SharedPrefsHelper.getInstance().save(FCM.TOKEN_STATUS, status);
    }

    public static boolean isTokenSaved() {
        return SharedPrefsHelper.getInstance().get(FCM.TOKEN_STATUS, false);
    }

    public static String getFCMToken() {
        return SharedPrefsHelper.getInstance().get(FCM.TOKEN);
    }

    public static void setFCMToken(String token) {
        SharedPrefsHelper.getInstance().save(FCM.TOKEN, token);
    }

    public static boolean isTokenSavedServer() {
        return SharedPrefsHelper.getInstance().get(FCM.TOKEN_SERVER_STATUS, false);
    }

    public static void setTokenSavedServer(boolean status) {
        SharedPrefsHelper.getInstance().save(FCM.TOKEN_SERVER_STATUS, status);
    }

    public interface FCM {
        String TOPICS = "Topics";
        String TOKEN_STATUS = "TokenStatus";
        String TOKEN = "Token";
        String TOKEN_SERVER_STATUS = "TokenServerStatus";
    }
}
