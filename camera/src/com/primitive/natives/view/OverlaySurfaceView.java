package com.primitive.natives.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.primitive.natives.helper.Logger;

public abstract class OverlaySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
	/**
	 *
	 * @author xxx
	 *
	 */
	public class FPS
	{
		/** one frame time */
		public final static int ONE_FRAME_TICK = 1000 / 25;
		/** remaining skip frame */
		public final static int MAX_FRAME_SKIPS = 5;
		public int frameCount = 0;
		public int workerFrameCount = 0;
		public long beforeTick = 0;
		public long currTime = 0;
	};

	/**
	 *
	 * @author xxx
	 *
	 */
	public interface FPSListner
	{
		/**
		 *
		 * @param sender
		 * @param fps
		 * @param canvas
		 */
		public void draw(OverlaySurfaceView sender, FPS fps, Canvas canvas);
		/**
		 *
		 * @param sender
		 * @param canvas
		 */
		public void skip(OverlaySurfaceView sender, Canvas canvas);
	}

	/**
	 *
	 * @author xxx
	 *
	 */
	public interface DrawingListner
	{
		public void drawing(OverlaySurfaceView sender, Canvas canvas);
	}

	/** FPSListner */
	protected FPSListner fpsListner = null;
	/** DrawingListner  */
	protected DrawingListner drawingListner = null;
	/**  */
	protected int scrWidth;
	/**  */
	protected int scrHeight;
	/**  */
	protected SurfaceHolder holder;
	/**  */
	private Thread threadMove;

	/**
	 *
	 * @param context
	 */
	public OverlaySurfaceView(Context context)
	{
		super(context);
		commonInitilize();
	}

	/**
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public OverlaySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		commonInitilize();
	}

	/**
	 *
	 * @param context
	 * @param attrs
	 */
	public OverlaySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		commonInitilize();
	}

	/**
	 *
	 */
	private void commonInitilize()
	{
		this.holder = this.getHolder();
		this.holder.setFormat(PixelFormat.TRANSPARENT);
		this.holder.addCallback(this);
		this.setZOrderMediaOverlay(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.scrWidth = width;
		this.scrHeight = height;
	}

	/**
	 * サーフェイスが作られた
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// 更新用スレッドの開始
		this.threadMove = new Thread(drawable);
		this.threadMove.start();
	}

	/**
	 * サーフェイスが破棄された
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.threadMove = null;
	}

	private Runnable drawable = new Runnable()
	{
		@Override
		public void run()
		{
			Canvas canvas;
			// 処理開始時間
			long beginTime;
			// 経過時間
			long pastTick;
			int sleep = 0;
			// 何フレーム分スキップしたか
			int frameSkipped;

			// 文字書いたり

			FPS buffer = new FPS();

			while (OverlaySurfaceView.this.threadMove != null)
			{
				canvas = null;

				// フレームレートの表示
				buffer.workerFrameCount++;
				buffer.currTime = System.currentTimeMillis();
				if (buffer.beforeTick + 1000 < buffer.currTime) {
					buffer.beforeTick = buffer.currTime;
					buffer.frameCount = buffer.workerFrameCount;
					buffer.workerFrameCount = 0;
				}

				try
				{
					synchronized (OverlaySurfaceView.this.holder)
					{
						canvas = OverlaySurfaceView.this.holder.lockCanvas();
						if (canvas != null)
						{
							canvas.drawColor(0, Mode.CLEAR);

							beginTime = System.currentTimeMillis();
							frameSkipped = 0;

							fpsListner.skip(OverlaySurfaceView.this, canvas);
							canvas.save();
							OverlaySurfaceView.this.draw(canvas);
							canvas.restore();

							pastTick = System.currentTimeMillis() - beginTime;

							sleep = (int) (FPS.ONE_FRAME_TICK - pastTick);

							if (0 < sleep)
							{
								try
								{
									Thread.sleep(sleep);
								}
								catch (Throwable ex)
								{
									Logger.err(ex);
								}
							}

							while (sleep < 0 && frameSkipped < FPS.MAX_FRAME_SKIPS)
							{
								fpsListner.skip(OverlaySurfaceView.this, canvas);
								sleep += FPS.ONE_FRAME_TICK;
								frameSkipped++;
							}
							if (fpsListner != null)
							{
								fpsListner.draw(OverlaySurfaceView.this, buffer, canvas);
							}
						}
					}
				}
				finally
				{
					if (canvas != null)
					{
						OverlaySurfaceView.this.holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	};

	public void onDraw()
	{
		Canvas canvas = this.holder.lockCanvas();
		draw(canvas);
		this.holder.unlockCanvasAndPost(canvas);
	}

	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		drawingListner.drawing(this, canvas);
	}
}
