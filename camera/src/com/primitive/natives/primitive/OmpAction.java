package com.primitive.natives.primitive;

public class OmpAction
{
	static final String libName = "native_authenticator";
	static
	{
		System.loadLibrary(libName);
	}
	public native void nativeExec();
}
