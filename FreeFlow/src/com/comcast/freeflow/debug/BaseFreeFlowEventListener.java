package com.comcast.freeflow.debug;

import com.comcast.freeflow.core.FreeFlowEventListener;
import com.comcast.freeflow.layouts.FreeFlowLayout;

/**
 * Just a helper class so you can extend this for event management if you only
 * care for one or two specific events.
 * 
 * @author Arpit Mathur
 * 
 */
public class BaseFreeFlowEventListener implements FreeFlowEventListener {

	@Override
	public void layoutChangeAnimationsStarting() {
	}

	@Override
	public void layoutChangeAnimationsComplete() {
	}

	@Override
	public void layoutComputed() {
	}

	@Override
	public void dataChanged() {
	}

	@Override
	public void onLayoutChanging(FreeFlowLayout oldLayout,
			FreeFlowLayout newLayout) {
	}

	@Override
	public void layoutComplete(boolean areTransitionAnimationsPlaying) {
	}

}
