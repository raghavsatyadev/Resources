package com.support.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ImageUtils {
    public static void loadImageWithLoader(ImageView imageView, String url, View progressBar, boolean isCenterCrop, ImageLoaderListener imageLoaderListener) {
        try {
            if (url == null) url = "";
            progressBar.setVisibility(View.VISIBLE);
            GlideRequest<Drawable> request = GlideApp.with(imageView.getContext())
                    .load(url)
                    .dontAnimate()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            if (imageLoaderListener != null) imageLoaderListener.onError(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            if (imageLoaderListener != null) imageLoaderListener.onImageLoaded();
                            return false;
                        }
                    });
            if (isCenterCrop) request.centerCrop().into(imageView);
            else request.into(imageView);
        } catch (Exception e) {
            AppLog.log(false, "ImageUtils: " + "loadImageWithLoader: ", e);
        }
    }

    public static void loadImageWithLoader(ImageView imageView, String url, View progressBar, boolean isCenterCrop) {
        loadImageWithLoader(imageView, url, progressBar, isCenterCrop, null);
    }

    public static void loadImage(ImageView imageView, String url, @DrawableRes int placeHolder, boolean isCenterCrop, boolean dontAnimate, ImageLoaderListener imageLoaderListener) {
        try {
            if (url == null) url = "";
            GlideRequest<Drawable> request = GlideApp.with(imageView.getContext())
                    .load(url);
            if (placeHolder != 0) request.placeholder(placeHolder);
            if (dontAnimate) {
                request = request.dontAnimate();
            }
            if (imageLoaderListener != null)
                request.listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageLoaderListener.onError(e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageLoaderListener.onImageLoaded();
                        return false;
                    }
                });
            if (isCenterCrop) request.centerCrop().into(imageView);
            else request.into(imageView);
        } catch (Exception e) {
            AppLog.log(false, "ImageUtils: " + "loadImage: ", e);
        }
    }

    public static void loadImage(ImageView imageView, @StringRes int url, @DrawableRes int placeHolder, boolean isCenterCrop, ImageLoaderListener imageLoaderListener) {
        loadImage(imageView, ResourceUtils.getString(url), placeHolder, isCenterCrop, true, imageLoaderListener);
    }

    public static void loadImage(ImageView imageView, String url, @DrawableRes int placeHolder, boolean isCenterCrop, ImageLoaderListener imageLoaderListener) {
        loadImage(imageView, url, placeHolder, isCenterCrop, true, imageLoaderListener);
    }

    public static void loadImage(ImageView imageView, @StringRes int url, @DrawableRes int placeHolder, boolean isCenterCrop) {
        loadImage(imageView, ResourceUtils.getString(url), placeHolder, isCenterCrop, false, null);
    }

    public static void loadImage(ImageView imageView, String url, @DrawableRes int placeHolder, boolean isCenterCrop) {
        loadImage(imageView, url, placeHolder, isCenterCrop, false, null);
    }

    public static void loadImage(ImageView imageView, @StringRes int url, @DrawableRes int placeHolder, boolean isCenterCrop, boolean dontAnimate) {
        loadImage(imageView, ResourceUtils.getString(url), placeHolder, isCenterCrop, dontAnimate, null);
    }

    public static void loadImage(ImageView imgRowComment, String url, int placeHolder, boolean isCenterCrop, boolean dontAnimate) {
        loadImage(imgRowComment, url, placeHolder, isCenterCrop, dontAnimate, null);
    }

    public interface ImageLoaderListener {
        void onImageLoaded();

        void onError(Exception e);
    }
}
