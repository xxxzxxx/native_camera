package com.primitive.natives.camera;

import com.primitive.natives.camera.view.CameraOverlaySurfaceView;
import com.primitive.natives.camera.view.CameraView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public final class CameraActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		createSurface();
	}

	protected static final int FP = ViewGroup.LayoutParams.FILL_PARENT;
	protected static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

	private void createSurface()
	{
		LinearLayout lilayout =new LinearLayout(this.getApplicationContext());
		lilayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		lilayout.setBackgroundColor(Color.GRAY);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WC,WC);
		final CameraView cameraView = new CameraView(this);
		cameraView.setZOrderMediaOverlay(false);
		cameraView.setZOrderOnTop(false);
		lilayout.addView(cameraView, params);
		this.setContentView(lilayout);

		final RelativeLayout controlOverlayView = new RelativeLayout(this.getApplicationContext());
		controlOverlayView.addView(new CameraOverlaySurfaceView(this.getApplicationContext()));
		this.addContentView(controlOverlayView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		controlOverlayView.setOnClickListener(menu_open_listner);
	}

	private final OnClickListener menu_open_listner = new OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
			builder.setTitle("メニュー").setMessage("選択してください");
			AlertDialog alertDialog = builder.create();
			alertDialog.setButton(Dialog.BUTTON1,"登録", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt)
				{
					//登録
				}
			});
			alertDialog.setButton(Dialog.BUTTON2,"認証", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt)
				{
					//登録
				}
			});
			alertDialog.setButton(Dialog.BUTTON3,"切り替え", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt)
				{
					//切り替え
				}
			});
			alertDialog.show();
		}
	};
}