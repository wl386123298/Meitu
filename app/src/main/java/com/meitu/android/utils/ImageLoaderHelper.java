package com.meitu.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.meitu.android.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

public class ImageLoaderHelper {
    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private static DisplayImageOptions options;


    public static DisplayImageOptions initOptionsForCommonImage() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_default_loading_image)
                .showImageForEmptyUri(R.drawable.ic_default_loading_image)
                .showImageOnFail(R.drawable.ic_default_loading_image)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        return options;
    }


    /**
     * display the image
     *
     * @param uri
     * @param imageView
     * @param options
     */
    public static void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
        imageLoader.displayImage(uri, imageView, options);
    }

    public static void displayImage(String uri, ImageView imageView) {
        imageLoader.displayImage(uri, imageView, initOptionsForCommonImage());
    }

    public static void displayImage(String uri, ImageView imageView, ImageLoadingListener imageLoadingListener) {
        imageLoader.displayImage(uri, imageView, initOptionsForCommonImage(), imageLoadingListener);
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }


    /**
     * **************用Glide加载图片********************
     */

    public static void displayImageForGlideNoScale(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_default_loading_image)
                .crossFade()
                .into(imageView);
    }

    public static void displayImageForGlide(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_default_loading_image)
                .crossFade()
                .into(imageView);
    }

    public static void displayImageForGlide(Context context, File file, ImageView imageView) {
        Glide.with(context)
                .load(file)
                .centerCrop()
                .placeholder(R.drawable.ic_default_loading_image)
                .crossFade()
                .into(imageView);

    }

    public static void displayImageForGlide(Context context, Uri uri, ImageView imageView) {
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.ic_default_loading_image)
                .crossFade()
                .into(imageView);

    }

    public static void displayImageForGlide(Context context, int resourceId, ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .centerCrop()
                .placeholder(R.drawable.ic_default_loading_image)
                .error(R.drawable.ic_default_loading_image)
                .crossFade()
                .into(imageView);
    }


    public static void getBitmapFromUrlUseGlide(Context context, String url, GlideDrawableImageViewTarget glideDrawableImageViewTarget) {
        Glide.with(context).load(url).centerCrop().placeholder(R.drawable.ic_default_loading_image).crossFade().into(glideDrawableImageViewTarget);
    }

    public static void getBitmapFromUrlUseGlide(Context context, String url, SimpleTarget<Bitmap> simpleTarget) {
        Glide.with(context).load(url).asBitmap().centerCrop().into(simpleTarget);
    }

}
