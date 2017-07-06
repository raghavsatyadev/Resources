package raghav.resources.support.retrofit.network;

/**
 * Created by raghav on 6/7/17.
 */

public interface ListCallback {
    void onListNext(Object t, WebserviceBuilder.ApiNames apiNames);

    void onFailure(Throwable throwable, WebserviceBuilder.ApiNames apiNames);

    void onListComplete(WebserviceBuilder.ApiNames apiNames);

}
