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
package com.comcast.freeflow.core;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.util.Pair;

import com.comcast.freeflow.animations.FreeFlowLayoutAnimator;

public class LayoutChangeset {
	protected List<Pair<FreeFlowItem, Rect>> moved;
	protected List<FreeFlowItem> removed;
	protected List<FreeFlowItem> added;

	public LayoutChangeset() {
		moved = new ArrayList<Pair<FreeFlowItem, Rect>>();
		removed = new ArrayList<FreeFlowItem>();
		added = new ArrayList<FreeFlowItem>();
	}

	public void addToMoved(FreeFlowItem proxy, Rect oldFrame) {
		moved.add(new Pair<FreeFlowItem, Rect>(proxy, oldFrame));
	}

	public void addToDeleted(FreeFlowItem proxy) {
		removed.add(proxy);
	}

	public void addToAdded(FreeFlowItem proxy) {
		added.add(proxy);
	}

	/**
	 * Returns the list of FreeFlowItems that will be added because they are
	 * present in the new Layout that the FreeFlowContainer is transitioning to.
	 * The views representing these items are already placed on the stage before
	 * the LayoutAnimator is called.
	 * 
	 */
	public List<FreeFlowItem> getAdded() {
		return added;
	}

	/**
	 * Returns the list of FreeFlowItems that are not present in the current
	 * viewport of the layout that the FreeFlowContainer is transitioning to,
	 * and needs to be removed.
	 * 
	 * The views represnting the FreeFlowItems in this list are still on the
	 * stage when the LayoutAnimator is given control if you want to animate the
	 * removal. You don't have to call removeView on these items, the
	 * FreeFlowContainer will do that when the animator relinquishes control by
	 * calling the
	 * {@link com.comcast.freeflow.core.FreeFlowContainer#onLayoutChangeAnimationsCompleted(FreeFlowLayoutAnimator anim)}
	 * 
	 * @return
	 */
	public List<FreeFlowItem> getRemoved() {
		return removed;
	}

	/**
	 * Returns all the items that will move when the layouts change (not added
	 * or removed). The returned item is a Pair: the first item representing the
	 * FreeFlowItem that represents the view being moved and the second the Rect
	 * it is moving from.
	 * 
	 * To get a reference to the view thats moving, you can call item.view on
	 * the FreeFlowItem. To get a reference to the Rect its moving to, call
	 * item.frame.
	 */
	public List<Pair<FreeFlowItem, Rect>> getMoved() {
		return moved;
	}

	@Override
	public String toString() {
		return "Added: " + added.size() + "," + "Removed: " + removed.size()
				+ "," + "Moved: " + moved.size();
	}

	public boolean isEmpty() {
		return (added.size() == 0 && removed.size() == 0 && moved.size() == 0);
	}

}
