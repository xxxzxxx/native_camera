package com.primitive.natives.primitive;

import com.universal.robot.core.CaptureFrame;
import com.universal.robot.core.helper.Logger;



public class OmpAction
{
	static final String libName = "urb_vein_android";
	static
	{
		System.loadLibrary(libName);
	}

	public static native Object nativeExec();
	protected static native void nativeExec2(CaptureFrame[] array);
	public static void exec2(CaptureFrame[] array)
	{
		final long started = Logger.start();
		if (array == null)
		{
			Logger.debug("array is null");
		}
		else
		{
			for (CaptureFrame cp : array)
			{
				if (cp == null)
				{
					Logger.debug("array capture object is null");
				}
			}
		}
		nativeExec2(array);
		Logger.end(started);
	}
}