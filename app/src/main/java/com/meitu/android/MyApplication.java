package com.meitu.android;

import android.app.Application;
import android.content.Context;

import com.lidroid.xutils.util.LogUtils;
import com.meitu.android.config.AppConfig;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.onlineconfig.OnlineConfigAgent;

/**
 * @Author: wl
 * @Email : wangliang@daoxila.com
 * @Date : 2015/06/18 13:03
 * @Description: {描述这个类的作用}
 * @Copyright(c) 2015 www.daoxila.com. All rights reserved.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //默认为false，不是debug模式前后得10分钟配置的才会变
        //这里需要动态获取配置信息，所以设置为true
        OnlineConfigAgent.getInstance().setDebugMode(true);
        initImageLoader(getApplicationContext());
    }


    public static void initImageLoader(Context context) {
        LogUtils.allowI = AppConfig.DEBUG;
        LogUtils.allowD = AppConfig.DEBUG;
        LogUtils.allowE = AppConfig.DEBUG;
        LogUtils.allowV = AppConfig.DEBUG;
        LogUtils.allowW = AppConfig.DEBUG;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        //.writeDebugLogs()// Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

}
