package com.primitive.natives.helper;

import java.util.List;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;

public class CameraOptimaizerHelper {

	/**
	 * getOptimalPreviewSize
	 * @param sizes
	 * @param width
	 * @param height
	 * @return
	 */
	public static Size getOptimalPreviewSize(final List<Size> sizes, final int width, final int height)
	{
		final long started = Logger.start();
		try
		{
			Size size = null;

			if (sizes != null)
			{
				final double targetRatio = width / height;
				for (final Size s : sizes)
				{
					final double ratio = (double) s.width / s.height;
					if (Math.abs(ratio - targetRatio) < 0.1) {
						size = s;
						break;
					}
				}
			}
			return (size != null)
					? size
					: sizes.get(0);
		} finally
		{
			Logger.end(started);
		}
	}

	/**
	 * getOptimalPictureSize
	 * @param sizes
	 * @return
	 */
	public static Camera.Size getOptimalPictureSize(final List<Camera.Size> sizes)
	{
		final long started = Logger.start();
		try
		{
			final double targetRatio = (double) 4 / 3;
			Size size = null;
			for (final Size s : sizes)
			{
				final double ratio = (double) s.width / s.height;
				final int gasosu = s.width * s.height;
				if ((gasosu < (300 * 10000)) && (Math.abs(ratio - targetRatio) < 0.1))
				{
					size = s;
					break;
				}
			}
			return size != null ? size : sizes.get(0);
		}
		finally
		{
			Logger.end(started);
		}
	}

	/**
	 * getOptimalPreviewFormat
	 * @param parameters
	 * @return
	 */
	public static int getOptimalPreviewFormat(Camera.Parameters parameters)
	{
		int result = 0;
		for (int f : parameters.getSupportedPreviewFormats())
		{
			if (f == ImageFormat.NV21)
			{
				Logger.debug("ImageFormat.NV21");
				result = f;
			}
			else if (f == ImageFormat.JPEG || f == ImageFormat.RGB_565)
			{
				if (f == ImageFormat.JPEG )
				{
					Logger.debug("ImageFormat.JPEG");
				}
				if(f == ImageFormat.RGB_565)
				{
					Logger.debug("ImageFormat.RGB_565");
				}
				result = f;
			}
		}
		return result;
	}
}
