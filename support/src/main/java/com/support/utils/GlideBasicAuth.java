package com.support.utils;

import android.content.Context;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

public class GlideBasicAuth implements GlideModule {
    private static String API_PASSWORD = "";
    private static String API_USERNAME = "";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) { /* no customization */ }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(String.class, InputStream.class, new HeaderLoader.Factory());
    }

    private static class HeaderLoader extends BaseGlideUrlLoader<String> {

        static String credentials = API_USERNAME + ":" + API_PASSWORD;
        static String basic =
                "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        public static final Headers HEADERS = new LazyHeaders.Builder()
                .addHeader("Authorization", basic)
                .addHeader("Accept", "application/json")
                .build();

        public HeaderLoader(Context context) {
            super(context);
        }

        @Override
        protected String getUrl(String model, int width, int height) {
            return model;
        }

        @Override
        protected Headers getHeaders(String model, int width, int height) {
            return HEADERS;
        }

        public static class Factory implements ModelLoaderFactory<String, InputStream> {
            @Override
            public StreamModelLoader<String> build(Context context, GenericLoaderFactory factories) {
                return new HeaderLoader(context);
            }

            @Override
            public void teardown() { /* nothing to free */ }
        }
    }
}