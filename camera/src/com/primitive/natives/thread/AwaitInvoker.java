package com.primitive.natives.thread;

import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

public class AwaitInvoker implements Runnable
{
	private Runnable[] runnables;
	private CountDownLatch signal;

	@Override
	public void run() {
		for (Runnable r : this.runnables) {
			try {
				r.run();
			} finally {
				this.signal.countDown();
			}
		}
	}

	public void invokeAndWait(Handler handler, Runnable... runnables) {
		this.runnables = runnables;

		if (Looper.myLooper() == handler.getLooper()) {
			for (Runnable r : this.runnables) {
				r.run();
			}
			return;
		}
		this.signal = new CountDownLatch(runnables.length);
		handler.post(this);
		try {
			this.signal.await(); //ブロック
		} catch (InterruptedException e) {
			//省略
		}
	}
}
