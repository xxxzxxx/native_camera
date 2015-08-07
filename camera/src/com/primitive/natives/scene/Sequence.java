package com.primitive.natives.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Handler;

import com.primitive.natives.interfaces.Cancellable;

public abstract class Sequence implements Cancellable
{
	public enum Command
	{
		/** 中断 */
		cancell,
		/** 成功 */
		success,
		/** 次のシーンへ */
		nextScene,
		/** 一つ前のシーンへ */
		prevScene;
	}

	public interface CallbackListner
	{
		public void callback(final Sequence sender, final Scene scene, final Action action, final Command command);
	};

	public interface Observer
	{
		public void notify(final Sequence sender, final Scene scene, final Action action, final Command command);
	};

	protected List<Scene> sequence;
	protected Scene currentScene = null;
	/**  */
	protected final CallbackListner callback;
	protected final CallbackListner self_callback = new CallbackListner()
	{
		@Override
		public void callback(final Sequence sender, final Scene scene, final Action action, final Command command)
		{
			for (final Sequence.Observer observer : observers)
			{
				Handler observeHandler = new Handler();

				observeHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						observer.notify(Sequence.this, scene, action, command);
					}
				});
			}
		}
	};;

	private List<Sequence.Observer> observers = Collections.synchronizedList(new ArrayList<Sequence.Observer>());
	/** Template UUID */
	protected final String identity;
	/** Process timeout time (seconds) */
	protected final int timeout;

	public Sequence(String identity, int timeout, Sequence.CallbackListner callback)
	{
		this.identity = identity;
		this.timeout = timeout;
		this.callback = callback;
		createSequence();
		;
	}

	/** タイムアウト処理ハンドラー */
	Handler timeoutHandler = new Handler();
	/** アクションハンドラー */
	Handler actionHandler = new Handler();

	protected abstract void createSequence();

	public void run()
	{
		//		timeoutHandler.postDelayed(r, delayMillis);
	}

	@Override
	public boolean cancel() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean isCancelled()
	{
		return (currentScene != null);
	}

	@Override
	public boolean isDone() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}


}
