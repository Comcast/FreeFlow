package org.freeflow.core;

import java.util.ArrayList;

import android.view.View;

public class ViewPool {

	private ArrayList<View>[] viewPool;

	public ViewPool() {
	}

	@SuppressWarnings("unchecked")
	public void initializeViewPool(int viewTypeCount) {
		viewPool = new ArrayList[viewTypeCount];
		for (int i = 0; i < viewTypeCount; i++) {
			viewPool[i] = new ArrayList<View>();
		}
	}

	public void returnViewToPool(View view, int viewType) {
		viewPool[viewType].add(view);
	}

	public View getViewFromPool(int viewType) {
		if (viewPool[viewType].size() == 0)
			return null;

		return viewPool[viewType].remove(0);
	}

}
