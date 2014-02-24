package com.comcast.freeflow.core;

import com.comcast.freeflow.layouts.AbstractLayout;

/**
 * Interface that all listeners interested in layout change events must
 * implement
 * 
 */
public interface FreeFlowEventListener {

	public void animationsStarted();

	public void animationsComplete();

	public void layoutComputed();
	
	/**
	 * Dispatched when the underlying data has been changed
	 */
	public void dataChanged();

	/**
	 * Called when the layout is about to change. Measurements based on the
	 * current data provider and current size have been completed.
	 * 
	 * @param oldLayout
	 * @param newLayout
	 */
	public void onLayoutChanging(AbstractLayout oldLayout, AbstractLayout newLayout);
	
	/**
	 * Dispatched when onLayout is called and views are laid out. Note that onLayout
	 * could be called because of views moving to new positions during a transition 
	 * animation, so you are passed that value as a boolean. 
	 * 
	 * @param areTransitionAnimationsPlaying Whether layout transition animations are 
	 * playing
	 */
	public void layoutComplete(boolean areTransitionAnimationsPlaying);

	

}
