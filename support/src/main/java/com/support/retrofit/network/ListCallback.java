package com.support.retrofit.network;

public interface ListCallback {
    /**
     * @param t        returns a single object from a list of objects
     * @param apiNames {@link com.support.retrofit.network.WebserviceBuilder.ApiNames} to differentiate Apis
     */
    void onListNext(Object t, WebserviceBuilder.ApiNames apiNames);

    /**
     * @param throwable returns Throwable for checking Exception
     * @param apiNames  {@link com.support.retrofit.network.WebserviceBuilder.ApiNames} to differentiate Apis
     */
    void onFailure(Throwable throwable, WebserviceBuilder.ApiNames apiNames);

    /**
     * gives an event of call completion
     *
     * @param apiNames {@link com.support.retrofit.network.WebserviceBuilder.ApiNames} to differentiate Apis
     */
    void onListComplete(WebserviceBuilder.ApiNames apiNames);

}
