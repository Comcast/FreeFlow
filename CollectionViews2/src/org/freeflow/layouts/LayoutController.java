package org.freeflow.layouts;

import org.freeflow.core.Frame;
import org.freeflow.core.FrameDescriptor;

import android.util.SparseArray;
import android.widget.BaseAdapter;

public interface LayoutController {

	/**
	 * Called whenever Container's onMeasure is triggered
	 * 
	 * @param measuredWidth
	 * @param measuredHeight
	 */
	public void setDimensions(int measuredWidth, int measuredHeight);

	public void setItems(BaseAdapter adapter);

	/**
	 * Generate the frame descriptors of all views in the given viewport, you
	 * provide the left and top, the width and height are resolved from the
	 * dimensions passed earlier in the setDimensions call
	 * 
	 * @param viewPortLeft
	 *            The left bound of the viewport
	 * @param viewPortTop
	 *            the top bound of the viewport
	 * @return
	 */
	public SparseArray<FrameDescriptor> getFrameDescriptors(int viewPortLeft, int viewPortTop);

	public Frame getViewportFrameForItemIndex(int index);

}
