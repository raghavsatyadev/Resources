package raghav.resources.support.retrofit.network;

/**
 * Created by raghav on 24-06-2017.
 */

public interface SingleCallback {
    void onSingleSuccess(Object t, WebserviceBuilder.ApiNames apiNames);

    void onFailure(Throwable throwable, WebserviceBuilder.ApiNames apiNames);
}
