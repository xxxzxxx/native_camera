package com.primitive.natives.helper;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.Surface;
import android.view.WindowManager;

public class RotationHelper
{
	@SuppressLint("NewApi")
	/**
	 * isPhoneMode
	 * @param context
	 * @return
	 */
	public static boolean isPhoneMode(Context context)
	{
		boolean isPhone;
		Resources r = context.getResources();
		Configuration configuration = r.getConfiguration();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			isPhone = ((configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) < Configuration.SCREENLAYOUT_SIZE_LARGE);
		}
		else
		{
			isPhone = (configuration.smallestScreenWidthDp < 600);
		}
		return isPhone;
	}

	/**
	 * isPortrait
	 * @param context
	 * @return
	 */
	public static boolean isPortrait(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int rot = wm.getDefaultDisplay().getRotation();
		boolean portrait = true;
		boolean isPhone = isPhoneMode(context);
		switch (rot) {
		case Surface.ROTATION_0:
		case Surface.ROTATION_180:
			portrait = isPhone ? true : false;
			break;
		case Surface.ROTATION_90:
		case Surface.ROTATION_270:
			portrait = isPhone ? false : true;
			break;
		}
		return portrait;
	}

	/**
	 * isRandscape
	 * @param context
	 * @return
	 */
	public static boolean isRandscape(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int rot = wm.getDefaultDisplay().getRotation();
		boolean portrait = true;
		boolean isPhone = isPhoneMode(context);
		switch (rot) {
		case Surface.ROTATION_0:
		case Surface.ROTATION_180:
			portrait = isPhone ? false : true;
			break;
		case Surface.ROTATION_90:
		case Surface.ROTATION_270:
			portrait = isPhone ? true : false;
			break;
		}
		return portrait;
	}

	/**
	 * getCameraDisplayOrientation
	 * @param context
	 * @return
	 */
	public static int getCameraDisplayOrientation(Context context)
	{
		final long started = Logger.start();
		try
		{
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			int rot = wm.getDefaultDisplay().getRotation();

			boolean isPhone = isPhoneMode(context);
			int degree = 0;
			switch (rot) {
			case Surface.ROTATION_0:
				Logger.debug("Surface.ROTATION_0");
				degree = 0;
				break;
			case Surface.ROTATION_90:
				Logger.debug("Surface.ROTATION_90");
				degree = 90;
				break;
			case Surface.ROTATION_180:
				Logger.debug("Surface.ROTATION_180");
				degree = 180;
				break;
			case Surface.ROTATION_270:
				Logger.debug("Surface.ROTATION_270");
				degree = 270;
				break;
			}
			degree = isPhone ? degree : degree + 90;
			return (90 + 360 - degree) % 360;
		}
		finally
		{
			Logger.end(started);
		}
	}
}
