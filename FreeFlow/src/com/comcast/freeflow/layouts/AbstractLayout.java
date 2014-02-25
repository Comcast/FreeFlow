/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.comcast.freeflow.layouts;

import java.util.HashMap;

import com.comcast.freeflow.animations.LayoutAnimator;
import com.comcast.freeflow.core.ItemProxy;
import com.comcast.freeflow.core.SectionedAdapter;

/**
 * The base class for all custom layouts. The Layout is responsible for figuring
 * out all the positions for all the views created by the Container based on the
 * <code>SectionedAdapter</code> supplied to it.
 * 
 */
public abstract class AbstractLayout {

	protected LayoutAnimator layoutAnimator = null;

	/**
	 * Called whenever Container's onMeasure is triggered Note: We don't support
	 * margin and padding yet, so the dimensions are the entire actual of the
	 * Container
	 * 
	 * @param measuredWidth
	 *            The width of the Container
	 * @param measuredHeight
	 *            The height of the Container
	 */
	public abstract void setDimensions(int measuredWidth, int measuredHeight);

	public abstract void setAdapter(SectionedAdapter adapter);

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
	public abstract HashMap<? extends Object, ItemProxy> getItemProxies(
			int viewPortLeft, int viewPortTop);

	protected FreeFlowLayoutParams layoutParams;

	public void setLayoutParams(FreeFlowLayoutParams params) {
		if (layoutParams != params) {
			params = layoutParams;
		}
	}

	public abstract ItemProxy getItemProxyForItem(Object item);

	public boolean horizontalScrollEnabled() {
		return true;
	}

	public boolean verticalScrollEnabled() {
		return true;
	}

	public abstract int getContentWidth();

	public abstract int getContentHeight();

	public abstract ItemProxy getItemAt(float x, float y);

	public static class FreeFlowLayoutParams {

	}

}
