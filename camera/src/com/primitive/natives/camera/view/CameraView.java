package com.primitive.natives.camera.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.primitive.natives.helper.CameraOptimaizerHelper;
import com.primitive.natives.helper.RotationHelper;
import com.primitive.natives.thread.AwaitInvoker;
import com.universal.robot.core.CaptureFrame;
import com.universal.robot.core.helper.Logger;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback
{
	enum PreviewGAMode
	{
		QVGA /* 320*240 */
		, VGA /* 640*480 */
		, SVGA /* 800*600 */
		, XGA /* 1024*768 */
	};

	public interface TakePreviewPictureListner
	{
		public boolean takePicture();
		public int takePictureCount();
		public void onTakePreview(Object sender, CaptureFrame[] frames);
	}

	private TakePreviewPictureListner takePreviewPictureListner = null;

	public void setTakePreviewPictureListner(final TakePreviewPictureListner listner)
	{
		this.takePreviewPictureListner = listner;
	}

	private Camera camera;
	private SurfaceHolder holder;
	private Camera.Size previewSize = null;
	private int previewFormat = 0;

	/**  */
	private int previewParamMax = 640;
	/**  */
	private int previewParamMin = 480;

	public Camera.Size getPreviewSize()
	{
		return this.previewSize;
	}

	public CameraView(final Context context)
	{
		super(context);
		final long started = Logger.start();
		this.commonInitilize();
		Logger.end(started);
	}

	public CameraView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		final long started = Logger.start();
		this.commonInitilize();
		Logger.end(started);
	}

	public CameraView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final long started = Logger.start();
		this.commonInitilize();
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

	private List<CaptureFrame> buffers = Collections.synchronizedList(new ArrayList<CaptureFrame>());

	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera)
	{
		Logger.debug("data.size:[%d]", data.length);
		if (this.takePreviewPictureListner == null)
		{
		}
		else if (!this.takePreviewPictureListner.takePicture())
		{
		}
		else
		{
			if (this.buffers.size() >= this.takePreviewPictureListner.takePictureCount())
			{
				this.buffers = Collections.synchronizedList(new ArrayList<CaptureFrame>());
			}

			final byte[] copy = new byte[data.length];
			System.arraycopy(data, 0, copy, 0, data.length);
			this.buffers.add(new CaptureFrame(CameraView.this.previewFormat,CameraView.this.previewSize,data));

			final List<CaptureFrame> buffers = this.buffers;
			Handler handler = new Handler();
			Runnable runner = new Runnable()
			{
				@Override
				public void run()
				{
					if (buffers.size() >= CameraView.this.takePreviewPictureListner.takePictureCount())
					{
						final CaptureFrame[] arrays = buffers.toArray(new CaptureFrame[0]);
						Runnable runner = new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									if (CameraView.this.takePreviewPictureListner != null)
									{
										CameraView.this.takePreviewPictureListner.onTakePreview(
												CameraView.this
												, arrays
												);
									}
								}
								catch (Throwable ex)
								{
									Logger.err(ex);
								}
							}
						};
						runner.run();
					}
				}
			};
			AwaitInvoker invoke = new AwaitInvoker();
			invoke.invokeAndWait(handler,runner);
			invoke.run();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(final SurfaceHolder holder)
	{
		final long started = Logger.start();
		try
		{
			this.camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);

			final Camera.Parameters parameters = this.camera.getParameters();

			final int fpsMinIndex = Camera.Parameters.PREVIEW_FPS_MIN_INDEX;
			final int fpsMaxIndex = Camera.Parameters.PREVIEW_FPS_MAX_INDEX;

			int fpsMin = 0;
			int fpsMax = 0;

			for (int[] renge : parameters.getSupportedPreviewFpsRange())
			{
				if (fpsMin < renge[fpsMinIndex]) {
					fpsMin = renge[fpsMinIndex];
					fpsMax = renge[fpsMaxIndex];
				}
			}

			Logger.debug("fpsMin:[%d] fpsMax:[%d]", fpsMin, fpsMax);
			parameters.setPreviewFpsRange(fpsMin, fpsMax);

			parameters.setWhiteBalance("fluorescent");

			this.camera.setPreviewDisplay(this.holder);
		} catch (final Throwable ex)
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
	public void surfaceDestroyed(final SurfaceHolder holder)
	{
		final long started = Logger.start();
		this.camera.stopPreview();
		this.camera.setPreviewCallback(null);
		this.camera.release();
		this.camera = null;
		Logger.end(started);
	}

	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height)
	{
		final long started = Logger.start();
		if (this.camera != null)
		{
			this.camera.stopPreview();

			RotationHelper.setCameraDisplayOrientation(this.getContext(), 0, this.camera);

			final Camera.Parameters parameters = this.camera.getParameters();

			final List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

			this.previewFormat = CameraOptimaizerHelper.getOptimalPreviewFormat(parameters);

			if (this.previewFormat != 0)
			{
				parameters.setPreviewFormat(this.previewFormat);
			}
			else
			{
				Logger.warm("previewFormat unknown format.");
			}

			final Size pictureSize = CameraOptimaizerHelper.getOptimalPictureSize(pictureSizes);

			final int preview_width = (pictureSize.width > pictureSize.height ? previewParamMax : previewParamMin);
			final int preview_height = (pictureSize.width > pictureSize.height ? previewParamMin : previewParamMax);

			this.previewSize = this.camera.new Size(preview_width, preview_height);

			parameters.setPreviewSize(this.previewSize.width, this.previewSize.height);

			final ViewGroup.LayoutParams layoutParams = this.getLayoutParams();

			final double preview_raito = (double) this.previewSize.width / (double) this.previewSize.height;
			if (width > height)
			{
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
			Logger.debug("layoutParams.width:[%d].height:[%d]",layoutParams.width,layoutParams.height);
			this.setLayoutParams(layoutParams);
			this.camera.setParameters(parameters);
			final int imgformat = parameters.getPreviewFormat();

			final int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
			final Camera.Size camerasize = parameters.getPreviewSize();
			final int frame_size = ((camerasize.width * camerasize.height) * bitsperpixel);
			final int frame_buffer_size = frame_size / 8;

			Logger.debug("bitsperpixel:[%d]",bitsperpixel);
			Logger.debug("camerasize.width:[%d] height:[%d] ",camerasize.width,camerasize.height);
			Logger.debug("frame_size:[%d]",frame_size);
			Logger.debug("frame_buffer_size:[%d]",frame_buffer_size);

			final byte[] callbackBuffer = new byte[frame_buffer_size];
			this.camera.setPreviewCallback(this);
			this.camera.addCallbackBuffer(callbackBuffer);
			this.camera.startPreview();
		}
		Logger.end(started);
	}
}