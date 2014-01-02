package org.freeflow.core;

import org.freeflow.layouts.AbstractLayout;

/**
 * Interface that all listeners interested in layout change events must
 * implement
 * 
 */
public interface FreeFlowEventListener {

	public void animationsStarted();

	public void animationsComplete();

	public void layoutComplete();

	public void layoutComputed();

	public void dataChanged();

	/**
	 * Called when the layout is about to change. Measurements based on the
	 * current data provider and current size have been completed.
	 * 
	 * @param oldLayout
	 * @param newLayout
	 */
	public void onLayoutChanging(AbstractLayout oldLayout, AbstractLayout newLayout);

	// Not implemented yet
	// public void beginningScroll();
	// public void scrollEnded();

}
