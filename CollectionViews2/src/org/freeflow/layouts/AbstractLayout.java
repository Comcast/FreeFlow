package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.SectionedAdapter;
import org.freeflow.core.ItemProxy;
import org.freeflow.layouts.animations.LayoutAnimator;

public abstract class AbstractLayout {

	protected LayoutAnimator layoutAnimator = null;

	/**
	 * Called whenever Container's onMeasure is triggered
	 * 
	 * @param measuredWidth
	 * @param measuredHeight
	 */
	public abstract void setDimensions(int measuredWidth, int measuredHeight);

	public abstract void setItems(SectionedAdapter adapter);

	/**
	 * Generate the item proxies of all views in the given viewport, you provide
	 * the left and top, the width and height are resolved from the dimensions
	 * passed earlier in the setDimensions call. <br>
	 * <br>
	 * 
	 * NOTE: Any item proxies returned by this method will be rendered, sized,
	 * laid out, and animated, regardeless of whether they are on screen or not.
	 * So if you would like to buffer off screen items, include them in the
	 * returned hashmap. <br>
	 * <br>
	 * 
	 * 
	 * @param viewPortLeft
	 *            The left bound of the viewport
	 * @param viewPortTop
	 *            the top bound of the viewport
	 * @return HashMap of Data to itemProxies All itemProxies returned will be
	 *         renedered, sized, laid out, and animated
	 */
	public abstract HashMap<? extends Object, ItemProxy> getItemProxies(int viewPortLeft, int viewPortTop);

	public abstract ItemProxy getItemProxyForItem(Object item);

	public abstract boolean horizontalDragEnabled();

	public abstract boolean verticalDragEnabled();

	public abstract int getContentWidth();

	public abstract int getContentHeight();

	public abstract void setHeaderItemDimensions(int hWidth, int hHeight);
	
	public abstract ItemProxy getItemAt(float x, float y);

}
