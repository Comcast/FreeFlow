package com.comcast.freeflow.core;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;

public class ViewPool {

	private HashMap<Class, ArrayList<View>> viewPool;

	public ViewPool() {
	}

	public void initializeViewPool(Class[] viewTypes) {
		viewPool = new HashMap<Class, ArrayList<View>>();
		for (int i = 0; i < viewTypes.length; i++) {
			viewPool.put(viewTypes[i], new ArrayList<View>());
		}
	}

	public void returnViewToPool(View view) {
		if (viewPool.containsKey(view.getClass()))
			viewPool.get(view.getClass()).add(view);
	}

	public View getViewFromPool(Class viewType) {
		if (viewPool.get(viewType) == null || viewPool.get(viewType).size() == 0)
			return null;

		return viewPool.get(viewType).remove(0);
	}

}
