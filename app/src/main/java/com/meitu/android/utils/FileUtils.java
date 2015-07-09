package com.meitu.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Author: wl
 * @Email : wangliang@daoxila.com
 * @Date : 2015/06/25 13:33
 * @Description: {FileUtils}
 * @Copyright(c) 2015 www.daoxila.com. All rights reserved.
 */
public class FileUtils {
    public final static byte[] loadFile(URI filename) throws IOException {
        File f = new File(filename);
        FileInputStream input = new FileInputStream(f);
        int len = input.available();
        byte[] buffer = new byte[len];
        input.read(buffer, 0, len);
        input.close();
        return buffer;
    }

    public static File getTempDir(Context mContext) {
        File dir = new File(Environment.getExternalStorageDirectory()
                .toString() + File.separator + mContext.getPackageName());
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                dir = new File("/mnt/sdcard2" + File.separator + mContext.getPackageName());
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        dir = mContext.getFilesDir();
                        LogUtils.i("FILE is so bad!");
                    }
                }
            }
        }

        return dir;
    }

    public static File getCamearDir(Context mContext) {
        File dir = new File(Environment.getExternalStorageDirectory()
                .toString() + File.separator + "DCIM" + File.separator + "Camera");
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                dir = new File("/mnt/sdcard2" + File.separator + "DCIM" + File.separator + "Camera");
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        dir = mContext.getFilesDir();
                        LogUtils.i("FILE is so bad!");
                    }
                }
            }
        }

        return dir;
    }

    public static File getImageCacheDir(Context mContext) {
        File dir = new File(Environment.getExternalStorageDirectory()
                .toString() + File.separator + mContext.getPackageName() + File.separator + "cache");
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                dir = new File("/mnt/sdcard2" + File.separator
                        + mContext.getPackageName() + File.separator + "cache");
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        dir = mContext.getFilesDir();
                        LogUtils.i("FILE:" + "is so bad!");
                    }
                }
            }
        }

        return dir;
    }


    public static void clearImageDir(Context mContext) {
        File dir = getImageCacheDir(mContext);
        for (File f : dir.listFiles()) {
            LogUtils.i("clear temp file " + f.getPath());
            f.delete();
        }
    }

    public static File createTempFile(Context mContext, String prefix, String suffix) {
        // Log.e("FILE", Environment.getExternalStorageDirectory().toString());
        File dir = getTempDir(mContext);
        //File dir = getCamearDir();

        File f = null;
        f = new File(dir, prefix + suffix);
        return f;
    }

    public static String saveTempBitmap(Context mContext, Bitmap bitmap) {
        try {
            File file = FileUtils.createTempFile(mContext, getNowTime(), ".jpg");
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            //缩放
            //Bitmap.createScaledBitmap(bitmap, 130, 160, true);

            os.flush();
            os.close();

            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取当前系统时间
     *
     * @return yyyyMMddHHmmss
     */
    protected static String getNowTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
    }

    /**
     * 检查sd是否存在
     *
     * @return false:不存在，反之
     */
    public static Boolean SDCardIsExsit() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用

            LogUtils.i("SD card is not avaiable/writeable right now.");
            return false;
        }

        return true;
    }
}
