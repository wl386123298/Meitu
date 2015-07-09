package com.meitu.android.utils;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.util.PreferencesCookieStore;
import com.meitu.android.model.ImageListByAlbumIdModel;
import com.meitu.android.model.ImageListModel;
import com.meitu.android.model.TabModel;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wliang on 15/5/30.
 */
public class HttpUtils {
    private static PreferencesCookieStore cookieStore;
    //超时时间
    private static final int TIME_OUT = 15 * 1000;

    private static final String REQUEST_CHARSET = "UTF-8";

    private static final String BASE_URL = "http://img.m.duba.com/api.php";

    /*是否设置缓存*/
    private static boolean isSetCache = true;

    /**
     * 获取httpUtils
     *
     * @param context
     * @param isUseCookies 是否使用Cookie
     * @return
     */
    public static com.lidroid.xutils.HttpUtils getHttp(Context context, boolean isUseCookies) {
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();
        httpUtils.configTimeout(TIME_OUT);
        httpUtils.configDefaultHttpCacheExpiry(0);
        httpUtils.configDefaultHttpCacheExpiry(0);

        if (cookieStore == null) {
            cookieStore = new PreferencesCookieStore(context);
        }


        if (isUseCookies) {
            httpUtils.configCookieStore(cookieStore);
        }

        return httpUtils;

    }

    /**
     * 获取Tab
     *
     * @param context
     * @param mReqesRequetResultListener
     */
    public static void getMenu(final Context context, final RequestResultListener mReqesRequetResultListener) {
        com.lidroid.xutils.HttpUtils httpUtils = getHttp(context, false);
        RequestParams params = new RequestParams(REQUEST_CHARSET);
        params.addQueryStringParameter("c", "photo");
        params.addQueryStringParameter("a", "getCategoryList");

        httpUtils.send(HttpRequest.HttpMethod.GET, BASE_URL, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.i(responseInfo.result);
                String status = JSON.parseObject(responseInfo.result).getString("status");
                if ("0".equals(status)) {
                    if (isSetCache) {
                        ACache.get(context).put("TabMenu", responseInfo.result);
                    }
                    String data = JSON.parseObject(responseInfo.result).getString("data");
                    List<TabModel> tabList = JSON.parseArray(data, TabModel.class);

                    if (mReqesRequetResultListener != null) {
                        mReqesRequetResultListener.loadSuccess(tabList);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                isSetCache = !isSetCache;

                String cacheResult = ACache.get(context).getAsString("TabMenu");
                if (null != mReqesRequetResultListener) {
                    if (!TextUtils.isEmpty(cacheResult)) {
                        String data = JSON.parseObject(cacheResult).getString("data");
                        List<TabModel> tabList = JSON.parseArray(data, TabModel.class);
                        mReqesRequetResultListener.loadSuccess(tabList);
                    } else {
                        mReqesRequetResultListener.loadFail(s);
                    }
                }
            }
        });
    }


    /**
     * 获取图片列表
     *
     * @param context
     * @param page
     * @param category_id
     */
    public static void getImageList(final Context context, final int page, final String category_id, final RequestResultListener<ImageListModel> mReqesRequetResultListener) {
        com.lidroid.xutils.HttpUtils httpUtils = getHttp(context, false);
        RequestParams params = new RequestParams(REQUEST_CHARSET);
        params.addQueryStringParameter("c", "photo");
        params.addQueryStringParameter("a", "getImageList");
        params.addQueryStringParameter("page", String.valueOf(page));
        params.addQueryStringParameter("category", category_id);

        httpUtils.send(HttpRequest.HttpMethod.GET, BASE_URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.i("*****" + responseInfo.result);
                String status = JSON.parseObject(responseInfo.result).getString("status");
                if ("0".equals(status)) {
                    if (isSetCache) {
                        ACache.get(context).put("getImageList_" + category_id + "_" + page, responseInfo.result);
                    }
                    String data = JSON.parseObject(responseInfo.result).getString("data");
                    List<ImageListModel> imageListModelList = JSON.parseArray(data, ImageListModel.class);

                    if (mReqesRequetResultListener != null) {
                        mReqesRequetResultListener.loadSuccess(imageListModelList);
                    }
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                isSetCache = !isSetCache;
                ToastUtil.showShortToast(context, "出错了,请检查你的网络设置");

                if (mReqesRequetResultListener != null) {
                    String cacheResult = ACache.get(context).getAsString("getImageList_" + category_id + "_" + page);
                    if (cacheResult == null) {
                        mReqesRequetResultListener.loadFail(s);
                    } else {
                        String data = JSON.parseObject(cacheResult).getString("data");
                        List<ImageListModel> imageListModelList = JSON.parseArray(data, ImageListModel.class);
                        mReqesRequetResultListener.loadSuccess(imageListModelList);
                    }
                }
            }
        });

    }


    /***
     * 通过id获取图片列表
     *
     * @param context
     * @param category_id
     * @param id
     */
    public static void getImageListByAlbumId(Context context, String category_id, String id, final RequestResultListener<ImageListByAlbumIdModel> imageListByAlbumIdModelRequestResultListener) {
        com.lidroid.xutils.HttpUtils httpUtils = getHttp(context, false);
        RequestParams params = new RequestParams(REQUEST_CHARSET);
        params.addQueryStringParameter("c", "photo");
        params.addQueryStringParameter("a", "getImageListByAlbumId");
        params.addQueryStringParameter("category", category_id);
        params.addQueryStringParameter("id", id);
        httpUtils.send(HttpRequest.HttpMethod.GET, BASE_URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.i(responseInfo.result);
                ImageListByAlbumIdModel model = new ImageListByAlbumIdModel();
                List<ImageListByAlbumIdModel> imageListByAlbumIdModelsList = new ArrayList<ImageListByAlbumIdModel>();
                String status = JSON.parseObject(responseInfo.result).getString("status");
                if ("0".equals(status)) {
                    String data = JSON.parseObject(responseInfo.result).getString("data");
                    List<ImageListByAlbumIdModel.AlbumModel> albumModelList = JSONObject.parseArray(data, ImageListByAlbumIdModel.AlbumModel.class);

                    String info = JSON.parseObject(responseInfo.result).getString("info");

                    ImageListByAlbumIdModel.Info infoModel = JSON.parseObject(info, ImageListByAlbumIdModel.Info.class);

                    model.setAlbumModelList(albumModelList);
                    model.setInfo(infoModel);

                    imageListByAlbumIdModelsList.add(model);
                }

                if (imageListByAlbumIdModelRequestResultListener != null)
                    imageListByAlbumIdModelRequestResultListener.loadSuccess(imageListByAlbumIdModelsList);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.i(s);
                if (imageListByAlbumIdModelRequestResultListener != null)
                    imageListByAlbumIdModelRequestResultListener.loadFail(s);
            }
        });
    }


    public interface RequestResultListener<T> {
        void loadSuccess(List<T> tabList);

        void loadFail(String message);
    }
}

