package com.primitive.natives.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;

import com.universal.robot.core.helper.Logger;

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
		switch (format) {
		case JPEG:
			extension = "jpg";
			break;
		case PNG:
			extension = "png";
			break;
		}
		;
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
	public static void saveBitmapToStrageExternal(Bitmap bitmap, String folderName, String fileName,
			CompressFormat format, int quality) throws IOException
	{
		final long started = Logger.start();
		String extension = getFileExtension(format);
		File root = Environment.getExternalStorageDirectory();
		File target = new File(root, String.format("%s/%s.%s", folderName, fileName, extension));
		saveBitmap(target, bitmap, folderName, fileName, format, quality);
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
	public static void saveBitmap(File target, Bitmap bitmap, String folderName, String fileName,
			CompressFormat format, int quality) throws IOException
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
			if (format == ImageFormat.JPEG)
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
	public static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height)
	{
		final int frameSize = width * height;

		for (int y = 0, yp = 0; y < height; y++)
		{
			int uvp = frameSize + (y >> 1) * width, yuv_u = 0, yuv_v = 0;
			for (int x = 0; x < width; x++, yp++)
			{
				int yuv_y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (yuv_y < 0)
				{
					yuv_y = 0;
				}
				if ((x & 1) == 0)
				{
					yuv_v = (0xff & yuv420sp[uvp++]) - 128;
					yuv_u = (0xff & yuv420sp[uvp++]) - 128;
				}

				final int y1192 = 1192 * yuv_y;
				int r = (y1192 + 1634 * yuv_v);
				int g = (y1192 - 833 * yuv_v - 400 * yuv_u);
				int b = (y1192 + 2066 * yuv_u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000
						| ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00)
						| ((b >> 10) & 0xff);
			}
		}
	}

	public static void makeRotateImage(final Context context, final byte[] data, final int maxPixel)
	{
		final long started = Logger.start();
		// オリジナルのBMP
		Bitmap bitmapSrc = makeTargetPixelImage(data, maxPixel);

		// 回転角を取り出す
		final int degree = RotationHelper.getCameraDisplayOrientation(context);
		int destWidth = 0;
		int destHeight = 0;

		// 反転、もしくはそのままの場合
		if ((degree % 180) == 0) {
			destWidth = bitmapSrc.getWidth();
			destHeight = bitmapSrc.getHeight();
		} else {
			destWidth = bitmapSrc.getHeight();
			destHeight = bitmapSrc.getWidth();
		}

		// 新しくBitmapを作る
		final Bitmap bitmapDest = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);

		// キャンバスを作る
		final Canvas canvas = new Canvas(bitmapDest);
		canvas.save();

		// キャンバスを使ってBMPを回転やら移動やらさせてデバイスの回転に合わせた画像を作る
		canvas.rotate(degree, destWidth / 2, destHeight / 2);
		final int offset = (((destHeight - destWidth) / 2) * ((degree - 180) % 180)) / 90;
		canvas.translate(offset, -offset);
		canvas.drawBitmap(bitmapSrc, 0, 0, null);
		canvas.restore();
		bitmapSrc.recycle();
		bitmapSrc = null;
		Logger.end(started);
	}

	/**
	 * 指定されたピクセル内に収まるようなBMPイメージを作る
	 * @param data
	 * @param maxPixel
	 * @return
	 */
	public static Bitmap makeTargetPixelImage(final byte[] data, final int maxPixel)
	{
		final long started = Logger.start();
		try
		{
			final BitmapFactory.Options option = new BitmapFactory.Options();
			int samplingSize = 0;

			// 作成する予定のBMPの情報を取り出す
			option.inJustDecodeBounds = true; // 情報のみ取り出す
			option.inSampleSize = 0; // 等角

			// 情報だけ取り出す
			option.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, option);

			// 指定されたピクセル数より多いBMPの場合は小さくする
			if (maxPixel < (option.outWidth * option.outHeight)) {

				// オーバーしてしまっている分を計算
				final double overPixel = (double) (option.outWidth * option.outHeight) / maxPixel;
				samplingSize = (int) (Math.sqrt(overPixel) + 1);

				// 指定されたサイズより下のものだった
			}
			else
			{
				// 等角で。
				samplingSize = 1;
			}

			// 実際の画像を読み込む
			//

			// データまで読み込み
			option.inJustDecodeBounds = false;

			// サンプリング係数
			option.inSampleSize = samplingSize;

			// 指定サイズの画像を作る
			return BitmapFactory.decodeByteArray(data, 0, data.length, option);
		} finally
		{
			Logger.end(started);
		}
	}

}
