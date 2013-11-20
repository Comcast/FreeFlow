package org.freeflow.core;

import java.util.HashMap;

import org.freeflow.layouts.animations.DefaultLayoutAnimator;

public abstract class LayoutController {

	protected LayoutControllerAnimator layoutAnimator = null;

	/**
	 * Called whenever Container's onMeasure is triggered
	 * 
	 * @param measuredWidth
	 * @param measuredHeight
	 */
	public abstract void setDimensions(int measuredWidth, int measuredHeight);

	public abstract void setItems(BaseSectionedAdapter adapter);

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
	public abstract HashMap<? extends Object, FrameDescriptor> getFrameDescriptors(int viewPortLeft, int viewPortTop);

	public abstract FrameDescriptor getFrameDescriptorForItem(Object item);

	public abstract Frame getOffScreenStartFrame();

	protected abstract void generateFrameDescriptors();

	public LayoutControllerAnimator getLayoutAnimator() {
		if (layoutAnimator == null)
			layoutAnimator = new DefaultLayoutAnimator();
		return layoutAnimator;
	}

	public void setLayoutAnimator(LayoutControllerAnimator layoutAnimator) {
		this.layoutAnimator = layoutAnimator;
	}

	public abstract boolean horizontalDragEnabled();

	public abstract boolean verticalDragEnabled();

	public abstract int getMinimumViewPortX();

	public abstract int getMinimumViewPortY();

	public abstract int getMaximumViewPortX();

	public abstract int getMaximumViewPortY();

	public abstract void setHeaderItemDimensions(int hWidth, int hHeight);

}
