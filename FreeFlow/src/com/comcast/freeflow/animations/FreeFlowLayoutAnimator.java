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
package com.comcast.freeflow.animations;

import android.view.MotionEvent;

import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.LayoutChangeset;

public interface FreeFlowLayoutAnimator {

	/**
	 * Returns the <code>LayoutChangeSet</code> object thats currently being
	 * animated
	 * 
	 * @return
	 */
	public LayoutChangeset getChangeSet();

	/**
	 * Cancels the currently running layout change animation
	 */
	public void cancel();

	/**
	 * Start the animation on all the changes that are wrapped in the
	 * <code>LayoutChangeset</code> object. These animations can span as much
	 * time as they want, but you are responsible for telling the
	 * <code>Container</code> when the animations are done by calling the
	 * <code>onLayoutChangeAnimationsCompleted</code> method on it.
	 * 
	 * @see FreeFlowContainer#onLayoutChangeAnimationsCompleted(LayoutAnimator)
	 * 
	 * @param changes
	 *            The Changeset to be animated
	 * @param callback
	 *            The Container instance to be informed when your animations are
	 *            complete
	 */
	public void animateChanges(LayoutChangeset changes,
			FreeFlowContainer callback);

	/**
	 * @return Whether the layout animation is currently playing
	 */
	public boolean isRunning();

	/**
	 * Called when a touch down event occurs while the layoutAnimator is
	 * animating. It gives you the first chance to exit or complete the
	 * animation since the Container might be about to be scrolled
	 * 
	 * @param event The MotionEvent received by the Container
	 */
	public void onContainerTouchDown(MotionEvent event);

}
