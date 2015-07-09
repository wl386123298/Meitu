package com.meitu.android.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	/**
	 * show short toast
	 * @param context
	 * @param text
	 */
	public static void showShortToast(Context context , String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * show long toast
	 * @param context
	 * @param text
	 */
	public static void showLongToast(Context context , String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
}
