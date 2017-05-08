package raghav.resources.support.retrofit.network;

import android.text.TextUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import raghav.resources.support.utils.AppLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiHelper<T> {

    private static ApiHelper apiHelper;

    private ApiHelper() {

    }

    public static ApiHelper getInstance() {
        if (apiHelper == null) {
            apiHelper = new ApiHelper();
        }

        return apiHelper;
    }

    public static MultipartBody.Part getMultiPartImage(String fileParameter, File file) {
        if (file != null)
            return MultipartBody.Part.createFormData(fileParameter, file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file));
        return null;
    }

    public static MultipartBody.Part getMultiPartImage(String fileParameter, String filepath) {
        if (!TextUtils.isEmpty(filepath)) {
            File file = new File(filepath);
            try {
                return getMultiPartImage(fileParameter, file);
            } catch (Exception e) {
                AppLog.log(AppLog.D, true, AppLog.TAG, "ApiHelper " + "getMultiPartImage: " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }

    }

    public static RequestBody getMultipartString(String string) {
        return RequestBody.create(MediaType.parse("text/plain"), string);
    }

    public void callApi(Call<T> call, final APIResponse retrofitApiResponse, final ApiNames apiNames) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (retrofitApiResponse != null) {
                    retrofitApiResponse.onResponse(response, apiNames);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                AppLog.log(false, "ApiHelper " + "onFailure: " + t.getMessage());
                if (!call.isCanceled() && retrofitApiResponse != null)
                    retrofitApiResponse.error(call, t, apiNames);
            }
        });
    }

    public enum ApiNames {

        login, profile, forgotPassword

    }
}
