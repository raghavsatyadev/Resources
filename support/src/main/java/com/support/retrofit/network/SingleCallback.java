package com.support.retrofit.network;

public interface SingleCallback {
    /**
     * @param o        Whole response Object
     * @param apiNames {@link com.support.retrofit.network.WebserviceBuilder.ApiNames} to differentiate Apis
     */
    void onSingleSuccess(Object o, WebserviceBuilder.ApiNames apiNames);

    /**
     * @param throwable returns {@link Throwable} for checking Exception
     * @param apiNames  {@link com.support.retrofit.network.WebserviceBuilder.ApiNames} to differentiate Apis
     */
    void onFailure(Throwable throwable, WebserviceBuilder.ApiNames apiNames);
}
