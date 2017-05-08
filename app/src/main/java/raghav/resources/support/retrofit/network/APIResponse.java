package raghav.resources.support.retrofit.network;

import retrofit2.Call;
import retrofit2.Response;

public interface APIResponse<T> {

    void onResponse(Response<T> response, ApiHelper.ApiNames apiNames);

    void error(Call<T> call, Throwable t, ApiHelper.ApiNames apiNames);

}
