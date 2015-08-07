package com.primitive.natives.camera;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.primitive.natives.camera.view.CameraOverlaySurfaceView;
import com.primitive.natives.camera.view.CameraView;
import com.universal.robot.core.Session;
import com.universal.robot.core.Session.Receiver;

public final class CameraActivity extends Activity
{
	Session session = null;
	Receiver reciver = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		createSurface();
//		AlertDialog alert = createMenuAlert(err);
//		alert.show();
	}

	protected static final int FP = ViewGroup.LayoutParams.FILL_PARENT;
	protected static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	protected CameraOverlaySurfaceView overlaySurfaceView;

//	private AuthenticationController authenticationController = new AuthenticationController();

	private void createSurface()
	{
		LinearLayout lilayout = new LinearLayout(this.getApplicationContext());
		lilayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		lilayout.setBackgroundColor(Color.GRAY);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WC, WC);

		final CameraView cameraView = new CameraView(this);
//		cameraView.setTakePreviewPictureListner(authenticationController);
		cameraView.setZOrderMediaOverlay(false);
		cameraView.setZOrderOnTop(false);
		lilayout.addView(cameraView, params);
		this.setContentView(lilayout);

		final RelativeLayout controlOverlayView = new RelativeLayout(this.getApplicationContext());
		overlaySurfaceView = new CameraOverlaySurfaceView(this.getApplicationContext());
		controlOverlayView.addView(overlaySurfaceView);
		this.addContentView(controlOverlayView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		controlOverlayView.setOnClickListener(menu_open_listner);
	}

	private AlertDialog createMenuAlert(final Throwable ex)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
		AlertDialog alertDialog = null;
		if (ex == null)
		{
			builder.setTitle("メニュー").setMessage("選択してください");
			alertDialog = builder.create();

			alertDialog.setButton(Dialog.BUTTON1, "登録", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
					builder.setTitle("登録");
					builder.setMessage("Enter Registration ID");

					AlertDialog alertDialog = builder.create();
					alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1)
						{
						}
					});
					alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1)
						{
						}
					});

					final EditText input = new EditText(CameraActivity.this);
					input.setSingleLine(true);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					input.setLayoutParams(lp);
					alertDialog.setView(input);
					alertDialog.show();
				}
			});
			alertDialog.setButton(Dialog.BUTTON2, "認証", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt)
				{
					//登録
				}
			});
			if (session != null)
			{
				alertDialog.setButton(Dialog.BUTTON3, "切り替え", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface, int paramInt)
					{
						//切り替え
					}
				});
			}
		}
		else
		{
			final String message = String.format("エラーが発生しました。\n%s", ex.getMessage());
			builder.setTitle("エラー").setMessage(message);
			alertDialog = builder.create();

			alertDialog.setButton(Dialog.BUTTON1, "了解", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt)
				{
					CameraActivity.this.finish();
				}
			});
		}
		return alertDialog;
	}

	private final OnClickListener menu_open_listner = new OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			AlertDialog alert = createMenuAlert(null);
			alert.show();
		}
	};
}