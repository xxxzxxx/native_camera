package com.primitive.natives.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.primitive.natives.view.OverlaySurfaceView;

public final class CameraOverlaySurfaceView extends OverlaySurfaceView
{
	public CameraOverlaySurfaceView(Context context)
	{
		super(context);
		commonInitilize();
	}

	public CameraOverlaySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		commonInitilize();
	}

	public CameraOverlaySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		commonInitilize();
	}

	private void commonInitilize()
	{
		super.drawingListner = this.drawingListner;
		super.fpsListner = this.fpsListner;
	}

	private static final int LINE_COUNT = 50;
	private static final int LINE_STEP = 4;
	private int lineY = 0;
	private void move()
	{
		this.lineY += 20;
		if (this.scrHeight + (LINE_COUNT * LINE_STEP) < this.lineY) {
			this.lineY = 0;
		}
	}

	OverlaySurfaceView.FPSListner fpsListner =
			new OverlaySurfaceView.FPSListner()
			{
				Paint paint = new Paint();
				public void draw(OverlaySurfaceView sender, FPS fps, Canvas canvas)
				{
					paint.setColor(Color.WHITE);
					paint.setAntiAlias(true);
					paint.setTextSize(60);
					canvas.drawText(String.format("FPS:%3d", fps.frameCount), 10, 60, paint);
				}

				public void skip(OverlaySurfaceView sender, Canvas canvas)
				{
					move();
				}
			};
	OverlaySurfaceView.DrawingListner drawingListner =
			new OverlaySurfaceView.DrawingListner()
			{
				final float scale = (getResources().getDisplayMetrics().densityDpi / 160f);
				Paint line = new Paint();
				private void write_line(Canvas canvas)
				{
					line.setStrokeWidth(1);
					for (int i = 0; i < LINE_COUNT; i++)
					{
						line.setColor(Color.argb(0xff - (0xff / LINE_COUNT) * i, 0x7f, 0xff, 0x7f));
						canvas.drawLine(0, lineY - i * 4, scrWidth, lineY - i * LINE_STEP, line);
					}
				}

				Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
				private void write_text(Canvas canvas)
				{
					final int centerH = scrHeight / 2;
					final int centerW = scrWidth / 2;
					{
						float font_size = 50 * scale;
						for (int i = 0; i < 3; i++)
						{
							text.setTextAlign(Paint.Align.CENTER);
							text.setTextSize(font_size * 1.0f);
							text.setColor(Color.WHITE);
							text.setAntiAlias(true);
							String word = "Hello,World!";
							int x = centerW;
							int y = centerH + (int) (font_size / 3);
							switch (i)
							{
							case 0:
								y -= (int) font_size;
								break;
							case 1:
								y += (int) font_size;
								break;
							}
							canvas.drawText(word
									, x
									, y
									, text);
						}
					}
				}

				Paint circle = new Paint();
				private void write_circle(Canvas canvas)
				{
					final int centerH = scrHeight / 2;
					final int centerW = scrWidth / 2;
					circle.setColor(Color.GREEN);
					circle.setStyle(Paint.Style.STROKE);
					canvas.drawCircle(centerW, centerH, 150 * scale, circle);
				}
				public void drawing(OverlaySurfaceView sender, Canvas canvas)
				{
					{
//						canvas.drawARGB(128, 128, 32, 16);
						int color = 32;
						canvas.drawARGB((int)(256 /1.5f), color, color, color);
					}
					write_line(canvas);
					write_text(canvas);
					write_circle(canvas);
				}
			};
}
