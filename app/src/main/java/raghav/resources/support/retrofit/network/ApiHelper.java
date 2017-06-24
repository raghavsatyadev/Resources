package raghav.resources.support.retrofit.network;

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
                AppLog.log(false, "ApiHelper " + "onFailure: ", t);
                if (!call.isCanceled() && retrofitApiResponse != null)
                    retrofitApiResponse.error(call, t, apiNames);
            }
        });
    }

    public enum ApiNames {

        login, profile, forgotPassword

    }
}
