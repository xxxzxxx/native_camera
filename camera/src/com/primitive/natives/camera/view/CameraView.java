package com.primitive.natives.camera.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.primitive.natives.helper.CameraOptimaizerHelper;
import com.primitive.natives.helper.Logger;
import com.primitive.natives.helper.RotationHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback
{
	public interface TakePreviewPictureListner
	{
		public void take_frames(CameraView sender, Camera.Size previewSize, byte[][] buffers);
	}

	private TakePreviewPictureListner takePreviewPictureListner = null;

	public void setTakePreviewPictureListner(TakePreviewPictureListner listner)
	{
		this.takePreviewPictureListner = listner;
	}

	private Camera camera;
	private SurfaceHolder holder;
	public boolean takePicture = false;
	private Camera.Size previewSize = null;
	private int previewFormat = 0;

	public Camera.Size getPreviewSize()
	{
		return previewSize;
	}

	public CameraView(Context context)
	{
		super(context);
		final long started = Logger.start();
		commonInitilize();
		Logger.end(started);
	}

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		final long started = Logger.start();
		commonInitilize();
		Logger.end(started);
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final long started = Logger.start();
		commonInitilize();
		Logger.end(started);
	}

	private void commonInitilize()
	{
		final long started = Logger.start();
		this.holder = this.getHolder();
		this.holder.addCallback(this);
		this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		Logger.end(started);
	}

	private List<byte[]> buffers = Collections.synchronizedList(new ArrayList<byte[]>());

	public void onPreviewFrame(byte[] data, Camera camera)
	{
		synchronized (this)
		{
			if (takePicture && takePreviewPictureListner != null)
			{
				byte[] bytes = new byte[data.length];
				System.arraycopy(data, 0, bytes, 0, data.length);

				if (this.buffers.size() >= 5)
				{
					this.buffers = Collections.synchronizedList(new ArrayList<byte[]>());
				}

				List<byte[]> buffers = this.buffers;
				buffers.add(bytes);
				if (this.buffers.size() >= 5)
				{
					byte[][] arrays = (byte[][]) buffers.toArray(new byte[0][0]);

					Logger.debug("arrays:%d", arrays.length);

					/*
					int i = 0;
					for (byte[] b : arrays)
					{
						Logger.debug("arrays[%d]:%d",i,b.length);
						i++;
					}
					*/
					if (takePreviewPictureListner != null)
					{
						takePreviewPictureListner.take_frames(
								CameraView.this
								, CameraView.this.previewSize
								, arrays
								);
					}
				}
			}
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		final long started = Logger.start();
		try
		{
			this.camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);

			Camera.Parameters parameters = this.camera.getParameters();

			int fpsMin = 0;
			int fpsMax = 0;
			for (int n = 0; n < parameters.getSupportedPreviewFpsRange().size(); ++n) {
				if (fpsMin < parameters.getSupportedPreviewFpsRange().get(n)[0]) {
					fpsMin = parameters.getSupportedPreviewFpsRange().get(n)[0];
					fpsMax = parameters.getSupportedPreviewFpsRange().get(n)[1];
				}
			}
			Logger.debug("fpsMin:[%d] fpsMax:[%d]", fpsMin, fpsMax);
			parameters.setPreviewFpsRange(fpsMin, fpsMax);

			parameters.setWhiteBalance("fluorescent");

			this.camera.setPreviewDisplay(this.holder);
		} catch (Throwable ex)
		{
			Logger.err(ex);
			if (this.camera != null)
			{
				this.camera.setPreviewCallback(null);
				this.camera.release();
			}
			this.camera = null;
		} finally
		{
			Logger.end(started);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		final long started = Logger.start();
		this.camera.stopPreview();
		this.camera.setPreviewCallback(null);
		this.camera.release();
		this.camera = null;
		Logger.end(started);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		final long started = Logger.start();
		if (this.camera != null)
		{
			this.camera.stopPreview();

			CameraView.setCameraDisplayOrientation(getContext(), 0, this.camera);

			Camera.Parameters parameters = this.camera.getParameters();

			final List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

			previewFormat = CameraOptimaizerHelper.getOptimalPreviewFormat(parameters);

			if (previewFormat != 0)
			{
				parameters.setPreviewFormat(previewFormat);
			}
			else
			{
				Logger.warm("previewFormat unknown format.");
			}

			final Size pictureSize = CameraOptimaizerHelper.getOptimalPictureSize(pictureSizes);

			int preview_width = (pictureSize.width > pictureSize.height ? 640 : 480);
			int preview_height = (pictureSize.width > pictureSize.height ? 480 : 640);

			previewSize = camera.new Size(preview_width, preview_height);

			parameters.setPreviewSize(previewSize.width, previewSize.height);

			final ViewGroup.LayoutParams layoutParams = getLayoutParams();
			final double preview_raito = (double) previewSize.width / (double) previewSize.height;
			if (width > height)
			{
				// 横長
				final int new_height = (int) (width / preview_raito);
				if (new_height <= height)
				{
					layoutParams.height = height;
				}
				else
				{
					final int new_width = (int) (height * preview_raito);
					layoutParams.width = new_width;
				}
			}
			else
			{
				// 縦長
				final int new_width = (int) (height / preview_raito);
				if (new_width <= width)
				{
					layoutParams.width = new_width;
				}
				else
				{
					layoutParams.height = (int) (width * preview_raito);
				}
			}
			setLayoutParams(layoutParams);
			this.camera.setParameters(parameters);
			int imgformat = parameters.getPreviewFormat();
			int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
			Camera.Size camerasize = parameters.getPreviewSize();
			int frame_size = ((camerasize.width * camerasize.height) * bitsperpixel) / 8;

			byte[] callbackBuffer = new byte[frame_size];
			camera.setPreviewCallback(this);
			camera.addCallbackBuffer(callbackBuffer);
			this.camera.startPreview();
		}
		Logger.end(started);
	}

	public static void setCameraDisplayOrientation(Context context, int cameraId, android.hardware.Camera camera)
	{
		camera.setDisplayOrientation(RotationHelper.getCameraDisplayOrientation(context));
	}

	public static void makeRotateImage(Context context, byte[] data, int maxPixel)
	{
		final long started = Logger.start();
		// オリジナルのBMP
		Bitmap bitmapSrc = CameraView.makeTargetPixelImage(data, maxPixel);

		// 回転角を取り出す
		int degree = RotationHelper.getCameraDisplayOrientation(context);
		int destWidth = 0;
		int destHeight = 0;

		// 反転、もしくはそのままの場合
		if (degree % 180 == 0) {
			destWidth = bitmapSrc.getWidth();
			destHeight = bitmapSrc.getHeight();
		} else {
			destWidth = bitmapSrc.getHeight();
			destHeight = bitmapSrc.getWidth();
		}

		// 新しくBitmapを作る
		Bitmap bitmapDest = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);

		// キャンバスを作る
		Canvas canvas = new Canvas(bitmapDest);
		canvas.save();

		// キャンバスを使ってBMPを回転やら移動やらさせてデバイスの回転に合わせた画像を作る
		canvas.rotate(degree, destWidth / 2, destHeight / 2);
		int offset = (destHeight - destWidth) / 2 * ((degree - 180) % 180) / 90;
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
	private static Bitmap makeTargetPixelImage(byte[] data, int maxPixel)
	{
		final long started = Logger.start();
		try
		{
			BitmapFactory.Options option = new BitmapFactory.Options();
			int samplingSize = 0;

			// 作成する予定のBMPの情報を取り出す
			option.inJustDecodeBounds = true; // 情報のみ取り出す
			option.inSampleSize = 0; // 等角

			// 情報だけ取り出す
			option.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, option);

			// 指定されたピクセル数より多いBMPの場合は小さくする
			if (maxPixel < option.outWidth * option.outHeight) {

				// オーバーしてしまっている分を計算
				double overPixel = (double) (option.outWidth * option.outHeight) / maxPixel;
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