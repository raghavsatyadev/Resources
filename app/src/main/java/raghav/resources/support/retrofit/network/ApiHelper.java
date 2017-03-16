package raghav.resources.support.retrofit.network;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public static MultipartBody.Part getMultipartFile(File file) {
        return MultipartBody.Part.createFormData("file_parameter_name", file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file));
    }

    public static RequestBody getMultipartString(String string) {
        return RequestBody.create(MediaType.parse("text/plain"), string);
    }

    public void callApi(Call<T> call, final APIResponse retrofitApiResponse, final ApiNames apiNames) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {

                retrofitApiResponse.onResponse(response, apiNames);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                // Log error here since request failed

                retrofitApiResponse.error(t, apiNames);
            }
        });
    }

    public enum ApiNames {

        login, profile, forgotPassword

    }
}
