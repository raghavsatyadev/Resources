package raghav.resources.support.fcm;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.TreeSet;

import raghav.resources.R;
import raghav.resources.support.utils.AppLog;
import raghav.resources.support.utils.ResourceUtils;
import raghav.resources.support.utils.SharedPrefsUtil;

public class TokenRefresh extends FirebaseInstanceIdService {

    private TokenRefresh context;


    public static void subscribeTopics() {
        TreeSet<String> TOPICS = new TreeSet<>();
        TOPICS.add(ResourceUtils.getString(R.string.app_name));

        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        for (String topic : TOPICS) {
            pubSub.subscribeToTopic(topic);
        }
        SharedPrefsUtil.setFCMTopics(TOPICS);
    }

    public static void subscribeTopics(JSONArray jsonArray) {
        TreeSet<String> TOPICS = new TreeSet<>();
        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                TOPICS.add(jsonArray.getString(i));
                pubSub.subscribeToTopic(jsonArray.getString(i));
            } catch (JSONException e) {
                AppLog.log(AppLog.D, true, AppLog.TAG, "subscribeTopics" + e.getMessage());
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