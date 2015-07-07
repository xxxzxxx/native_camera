package com.primitive.natives.camera.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.primitive.natives.helper.Logger;

public class ImageCalculatorService extends Service
{
	private ImageCalculatorServiceInterface.Stub stab = new ImageCalculatorServiceInterface.Stub()
	{
		private RemoteCallbackList<ImageCalculatorCallbackInterface> observers = new RemoteCallbackList<ImageCalculatorCallbackInterface>();

		@Override
		public void register_callback(ImageCalculatorCallbackInterface callback) throws RemoteException {
			final long started = Logger.start();
			observers.register(callback);
			Logger.end(started);
		}

		@Override
		public void unregister_callback(ImageCalculatorCallbackInterface callback) throws RemoteException {
			final long started = Logger.start();
			observers.unregister(callback);
			Logger.end(started);
		}

		@Override
		public void setup(byte[] license) throws RemoteException {
			final long started = Logger.start();
			Logger.end(started);
		}

		@Override
		public void setup_enroll() throws RemoteException
		{
			final long started = Logger.start();
			for (int index = 0,count = this.observers.beginBroadcast(); index<count; index++)
			{
				this.observers.getBroadcastItem(index).callback("session", 1, 1);
			}
			this.observers.finishBroadcast();
			Logger.end(started);
		}

		@Override
		public void setup_authenticate() throws RemoteException {
		}

		@Override
		public void setup_verification() throws RemoteException {
			final long started = Logger.start();
			Logger.end(started);
		}

		@Override
		public void setup_capture() throws RemoteException {
			final long started = Logger.start();
			Logger.end(started);
		}

		@Override
		public void cancel() throws RemoteException {
			final long started = Logger.start();
			Logger.end(started);
		}

		@Override
		public void command_enroll(int operation) throws RemoteException {
			final long started = Logger.start();
			Logger.end(started);
		}

	};
	@Override
	public IBinder onBind(Intent intent)
	{
		final long started = Logger.start();
		try
		{
			if (ImageCalculatorServiceInterface.class.getName().equals(intent.getAction()))
			{
				return stab;
			}
			return null;
		}
		finally
		{
			Logger.end(started);
		}
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}

}
