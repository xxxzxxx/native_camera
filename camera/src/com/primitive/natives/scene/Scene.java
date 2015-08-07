package com.primitive.natives.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;

import com.primitive.natives.interfaces.Cancellable;

public abstract class Scene implements Cancellable
{
	protected Map<Integer,Action> actionMap = new HashMap<Integer,Action>();

	private List<Integer> commandSequence = Collections.synchronizedList(new ArrayList<Integer>());
	private Action currentAction = null;

	Handler actionHandler = new Handler();

	@Override
	public boolean cancel() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
