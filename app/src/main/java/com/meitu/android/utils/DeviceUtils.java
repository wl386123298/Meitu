/**
 * @TODO
 * Author wl
 * Date  @2013-5-28
 * Copyright 2013 www.daoxila.com. All rights reserved.
 */
package com.meitu.android.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;


public class DeviceUtils {

	/**
	 * 
	 * 获取设备的型号
	 * 
	 */
	public static String getDeviceModel() {
		String model = Build.MODEL;

		if (model == null) {
			return "";
		} else {
			return model;
		}
	}

	/**
	 * 
	 * 获取设备 SDK版本名
	 * 
	 */
	public static int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	public static int getStatuBar(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	public static int getWindowHeight(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		return display.getHeight();
	}

	public static int getWindowWidth(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		return display.getWidth();
	}

	/**
	 * 
	 * 获取设备 SDK版本号
	 * 
	 */
	public static int getSDKVersionInt() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 判断是否存在SD卡
	 * 
	 * @return
	 */
	public static boolean isSdCardExist() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 
	 * 获取设备的IMEI号
	 * 
	 */
	public static String getIMEI(Context context) {
		TelephonyManager teleMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return teleMgr.getDeviceId();
	}

	/**
	 * 
	 * 获取设备的IMSI号
	 * 
	 */
	public static String getIMSI(Context context) {
		TelephonyManager teleMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return teleMgr.getSubscriberId();
	}

	/**
	 * 获取设备号 wifi mac + imei + cpu serial
	 * 
	 * @return 设备号
	 * 
	 */
	public static String getMobileUUID(Context context) {
		String uuid = "";
		// 先获取mac
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		/* 获取mac地址 */
		if (wifiMgr != null) {
			WifiInfo info = wifiMgr.getConnectionInfo();
			if (info != null && info.getMacAddress() != null) {
				uuid = info.getMacAddress().replace(":", "");
			}
		}

		// 再加上imei
		TelephonyManager teleMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = teleMgr.getDeviceId();
		uuid += imei;

		// 最后再加上cpu
		String str = "", strCPU = "", cpuAddress = "";
		try {
			String[] args = { "/system/bin/cat", "/proc/cpuinfo" };
			ProcessBuilder cmd = new ProcessBuilder(args);
			Process pp = cmd.start();
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					if (str.indexOf("Serial") > -1) {
						strCPU = str.substring(str.indexOf(":") + 1, str.length());
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		uuid += cpuAddress;

		// 如果三个加在一起超过64位的话就截取
		if (uuid != null && uuid.length() > 64) {
			uuid = uuid.substring(0, 64);
		}
		return uuid;
	}


	/**
	 * 获取屏幕的宽高
	 * 
	 * @param dm
	 *            设备显示对象描述
	 * @return int数组, int[0] - width, int[1] - height
	 */
	public static int[] getScreenSize(DisplayMetrics dm) {
		int[] result = new int[2];
		result[0] = dm.widthPixels;
		result[1] = dm.heightPixels;
		return result;
	}

	/**
	 * Dip转换为实际屏幕的像素值
	 * 
	 * @param dm
	 *            设备显示对象描述
	 * @param dip
	 *            dip值
	 * @return 匹配当前屏幕的像素值
	 */
	public static int getPixelFromDip(DisplayMetrics dm, float dip) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, dm) + 0.5f);
	}

	/**
	 * 判断当前设备的数据服务是否有效
	 * 
	 * @return true - 有效，false - 无效
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectMgr == null) {
			return false;
		}

		NetworkInfo nwInfo = connectMgr.getActiveNetworkInfo();
		if (nwInfo == null || !nwInfo.isAvailable()) {
			return false;
		}
		return true;
	}

	public static boolean isAppInstalled(Context context, String pkgName) {
		if (context == null) {
			return false;
		}

		try {
			context.getPackageManager().getPackageInfo(pkgName, PackageManager.PERMISSION_GRANTED);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * (检查是否具有拨号功能)
	 */
	public static boolean isCallIntentAvailable(Context context, String action) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * 是否具有短信功能
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSMSIntentAvailable(Context context) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("sms_body", "");
		intent.setType("vnd.android-dir/mms-sms");
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
}