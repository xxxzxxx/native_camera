package com.primitive.natives.helper;

import android.content.Context;
import android.util.DisplayMetrics;

public class ObjectScaler
{
	/**
	 * convertPixelsToDpi
	 * @param context
	 * @param px
	 * @return
	 */
	public static float convertPixelsToDpi(Context context,float px)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	/**
	 * convertDpiToPixel
	 * @param context
	 * @param dp
	 * @return
	 */
	public static float convertDpiToPixel(Context context,float dp)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}
}
