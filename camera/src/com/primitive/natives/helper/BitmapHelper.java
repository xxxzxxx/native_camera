package com.primitive.natives.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;

public class BitmapHelper
{
	/**
	 * getFileExtension
	 * @param format
	 * @return
	 */
	public static String getFileExtension(CompressFormat format)
	{
		final long started = Logger.start();
		String extension = null;
		switch (format){
			case JPEG:
			extension = "jpg";
			break;
			case PNG:
			extension = "png";
			break;
		};
		Logger.end(started);
		return extension;
	}

	/**
	 * saveBitmapToStrageExternal
	 * @param bitmap
	 * @param folderName
	 * @param fileName
	 * @param format
	 * @param quality
	 * @throws IOException
	 */
	public static void saveBitmapToStrageExternal(Bitmap bitmap, String folderName, String fileName,CompressFormat format, int quality) throws IOException
	{
		final long started = Logger.start();
		String extension = getFileExtension(format);
		File root = Environment.getExternalStorageDirectory();
		File target = new File(root, String.format("%s/%s.%s", folderName, fileName,extension));
		saveBitmap(target, bitmap, folderName, fileName,format,quality);
		Logger.end(started);
	}

	/**
	 * saveBitmap
	 * @param target
	 * @param bitmap
	 * @param folderName
	 * @param fileName
	 * @param format
	 * @param quality
	 * @throws IOException
	 */
	public static void saveBitmap(File target, Bitmap bitmap, String folderName, String fileName,CompressFormat format, int quality) throws IOException
	{
		final long started = Logger.start();
		FileOutputStream fstream = null;
		try
		{
			fstream = new FileOutputStream(target);
			bitmap.compress(format, quality, fstream);
		} catch (FileNotFoundException ex)
		{
			throw ex;
		} finally
		{
			if (fstream != null)
			{
				fstream.close();
			}
			Logger.end(started);
		}
	}

	/**
	 * createBitmapFromPreviewFrame
	 * @param format
	 * @param size
	 * @param frameBytes
	 * @return
	 */
	public static Bitmap createBitmapFromPreviewFrame(int format, Camera.Size size, byte[] frameBytes)
	{
		final long started = Logger.start();
		Bitmap bitmap = null;
		if (format == ImageFormat.NV21)
		{
			Logger.debug("ImageFormat.NV21");
			int[] rgbPixels = new int[size.width * size.height];
			decodeYUV420SP(rgbPixels, frameBytes, size.width, size.height);
			bitmap = Bitmap.createBitmap(rgbPixels, size.width, size.height, Bitmap.Config.RGB_565);
		}
		else if (format == ImageFormat.JPEG || format == ImageFormat.RGB_565)
		{
			if (format == ImageFormat.JPEG )
			{
				Logger.debug("ImageFormat.JPEG");
			}
			else
			{
				Logger.debug("ImageFormat.RGB_565");
			}
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inDither = true;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeByteArray(frameBytes, 0, frameBytes.length, opts);
		}
		Logger.end(started);
		return bitmap;
	}

	/**
	 * decodeYUV420SP
	 * @param rgb
	 * @param yuv_array
	 * @param width
	 * @param height
	 */
	public static void decodeYUV420SP(int[] rgb, byte[] yuv_array, int width, int height)
	{
		final long started = Logger.start();
		final int length = width * height;

		int yp = 0;
		int x, y;

		for (y = 0; y < height; y++)
		{
			int uvp = length + (y >> 1) * width, u1 = 0, v1 = 0;
			for (x = 0; x < width; x++)
			{
				int y1 = (0xff & ((int) yuv_array[yp])) - 16;

				if (y1 < 0)
				{
					y1 = 0;
				}

				if ((x & 1) == 0)
				{
					v1 = (0xff & yuv_array[uvp++]) - 128;
					u1 = (0xff & yuv_array[uvp++]) - 128;
				}

				int y1192 = 1192 * y1;
				int r1 = (y1192 + 1634 * v1);
				int g1 = (y1192 - 833 * v1 - 400 * u1);
				int b1 = (y1192 + 2066 * u1);

				if (r1 < 0)
				{
					r1 = 0;
				}
				else if (r1 > 262143)
				{
					r1 = 262143;
				}
				if (g1 < 0)
				{
					g1 = 0;
				}
				else if (g1 > 262143)
				{
					g1 = 262143;
				}
				if (b1 < 0)
				{
					b1 = 0;
				}
				else if (b1 > 262143)
				{
					b1 = 262143;
				}
				rgb[yp] = 0xff000000 | ((r1 << 6) & 0xff0000) | ((g1 >> 2) & 0xff00) | ((b1 >> 10) & 0xff);
				yp++;
			}
		}
		Logger.end(started);
	}
}
