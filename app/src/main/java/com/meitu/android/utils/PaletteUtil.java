package com.meitu.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Window;

/**
 * @Author: wl
 * @Email : wangliang@daoxila.com
 * @Date : 2015/06/24 15:44
 * @Description: {PaletteUtil}
 * @Copyright(c) 2015 www.daoxila.com. All rights reserved.
 */
public class PaletteUtil {

    /**
     * 分析图片的颜色对toolbar和statusbar进行设置
     * @param activity
     * @param mToolbar
     * @param bitmap 用来提取颜色的Bitmap
     */
    @SuppressLint("NewApi")
    public static void colorChange(final Activity activity, final Toolbar mToolbar, Bitmap bitmap) {
        if (bitmap != null) {
            Palette.Builder builder = new Palette.Builder(bitmap);
            builder.generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch vibrant = palette.getVibrantSwatch();//获取充满活力的颜色
                    //palette.getMutedSwatch();//获取柔和的颜色

                    if (vibrant == null) {
                        vibrant = palette.getMutedSwatch();
                    }
                    if (vibrant == null) {
                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            Window window = activity.getWindow();
                            // 很明显，这两货是新API才有的。
                            window.setStatusBarColor(Color.TRANSPARENT);
                            //window.setNavigationBarColor(colorBurn(vibrant.getRgb()));
                        }

                        return;
                    }
                    mToolbar.setBackgroundColor(vibrant.getRgb() == 0 ? Color.TRANSPARENT : vibrant.getRgb());
                    if (android.os.Build.VERSION.SDK_INT >= 21) {
                        Window window = activity.getWindow();
                        // 很明显，这两货是新API才有的。
                        window.setStatusBarColor(colorBurn(vibrant.getRgb() == 0 ? Color.BLACK : vibrant.getRgb()));
                        //window.setNavigationBarColor(colorBurn(vibrant.getRgb()));
                    }
                }
            });
        }
    }

    /**
     * 颜色加深处理
     *
     * @param RGBValues RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     *                  Android中我们一般使用它的16进制，
     *                  例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     *                  red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     *                  所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return
     */
    private static int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }
}
