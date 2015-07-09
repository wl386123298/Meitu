package com.meitu.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * @Author: wl
 * @Email : wangliang@daoxila.com
 * @Date : 2015/06/26 13:31
 * @Description: {描述这个类的作用}
 * @Copyright(c) 2015 www.daoxila.com. All rights reserved.
 */
public class ShareUtils {
    /**
     * 分享
     * @param context
     * @param activityTitle 分享窗口的标题
     * @param msgTitle 分享的标题
     * @param msgText 分享内容
     * @param imgPath 图片路径
     */
    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

}
