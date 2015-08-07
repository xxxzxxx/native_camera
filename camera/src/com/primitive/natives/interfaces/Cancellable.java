package com.primitive.natives.interfaces;

public interface Cancellable
{
	public boolean cancel();
	public boolean isCancelled();
	public boolean isDone();
}
