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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import org.freeflow.BuildConfig;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;

import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.LayoutChangeset;

public class DefaultLayoutAnimator implements FreeFlowLayoutAnimator {
	
	protected LayoutChangeset changeSet;

	public static final String TAG = "DefaultLayoutAnimator";

	/**
	 * The duration of the "Appear" animation per new cell being added. Note
	 * that the total time of the "Appear" animation will be based on the
	 * cumulative total of this animation playing on each cell
	 */
	public int newCellsAdditionAnimationDurationPerCell = 200;

	public int newCellsAdditionAnimationStartDelay = 0;

	/**
	 * The duration of the "Disappearing" animation per old cell being removed.
	 * Note that the total time of the "Disappearing" animation will be based on
	 * the cumulative total of this animation playing on each cell being removed
	 */
	public int oldCellsRemovalAnimationDuration = 200;

	public int oldCellsRemovalAnimationStartDelay = 0;

	public int cellPositionTransitionAnimationDuration = 250;

	/**
	 * If set to true, this forces the animation sets to animate in the
	 * following sequence: delete then add then move
	 * 
	 * If set to false, all sets will animate in parallel
	 */
	public boolean animateAllSetsSequentially = false;

	/**
	 * If set to true, this forces each view in a set to animate sequentially
	 * 
	 * If set to false, all views for a set will animate in parallel
	 */
	public boolean animateIndividualCellsSequentially = false;

	protected FreeFlowContainer callback;
	protected AnimatorSet disappearingSet = null;
	protected AnimatorSet appearingSet = null;
	protected AnimatorSet movingSet = null;

	public DefaultLayoutAnimator() {
	}

	@Override
	public void cancel() {

		if (disappearingSet != null)
			disappearingSet.cancel();

		if (appearingSet != null)
			appearingSet.cancel();

		if (movingSet != null)
			movingSet.cancel();
		
		for (FreeFlowItem item : changeSet.getAdded()) {
			item.view.setAlpha(1f);
		} 
		for (FreeFlowItem item : changeSet.getRemoved()) {
			item.view.setAlpha(1f);
		} 
		
		mIsRunning = false;
		
		
		

	}
	
	protected boolean mIsRunning = false;

	@Override
	public void animateChanges(LayoutChangeset changeSet, final FreeFlowContainer callback) {
		this.changeSet = changeSet;
		this.callback = callback;
		cancel();

		mIsRunning = true;
		
		disappearingSet = null;
		appearingSet = null;
		movingSet = null;

		Comparator<FreeFlowItem> cmp = new Comparator<FreeFlowItem>() {

			@Override
			public int compare(FreeFlowItem lhs, FreeFlowItem rhs) {
				return (lhs.itemSection * 1000 + lhs.itemIndex) - (rhs.itemSection * 1000 + rhs.itemIndex);
			}
		};

		List<FreeFlowItem> removed = changeSet.getRemoved();
		if (removed.size() > 0) {
			Collections.sort(removed, cmp);
			disappearingSet = getItemsRemovedAnimation(changeSet.getRemoved());
		}

		List<FreeFlowItem> added = changeSet.getAdded();
		if (added.size() > 0) {
			Collections.sort(added, cmp);
			appearingSet = getItemsAddedAnimation(added);
		}

		if (changeSet.getMoved().size() > 0) {
			movingSet = getItemsMovedAnimation(changeSet.getMoved());
		}

		AnimatorSet all = getAnimationSequence();
		if (all == null) {
			mIsRunning = false;
			callback.onLayoutChangeAnimationsCompleted(this);
		} else {

			all.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mIsRunning = false;
					callback.onLayoutChangeAnimationsCompleted(DefaultLayoutAnimator.this);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}

			});

			all.start();
		}
	}

	/**
	 * The animation to run on the items being removed
	 * 
	 * @param removed
	 *            An ArrayList of <code>FreeFlowItems</code> removed
	 * @return The AnimatorSet of the removed objects
	 */
	protected AnimatorSet getItemsRemovedAnimation(List<FreeFlowItem> removed) {
		AnimatorSet disappearingSet = new AnimatorSet();
		ArrayList<Animator> fades = new ArrayList<Animator>();
		for (FreeFlowItem proxy : removed) {
			fades.add(ObjectAnimator.ofFloat(proxy.view, "alpha", 0));
		}
		disappearingSet.setDuration(oldCellsRemovalAnimationDuration);
		disappearingSet.setStartDelay(oldCellsRemovalAnimationStartDelay);

		if (animateIndividualCellsSequentially)
			disappearingSet.playSequentially(fades);
		else
			disappearingSet.playTogether(fades);

		return disappearingSet;
	}

	/**
	 * 
	 */
	protected AnimatorSet getItemsAddedAnimation(List<FreeFlowItem> added) {
		AnimatorSet appearingSet = new AnimatorSet();
		ArrayList<Animator> fadeIns = new ArrayList<Animator>();
		for (FreeFlowItem proxy : added) {
			proxy.view.setAlpha(0);
			fadeIns.add(ObjectAnimator.ofFloat(proxy.view, "alpha", 1));
		}

		if (animateIndividualCellsSequentially)
			appearingSet.playSequentially(fadeIns);
		else
			appearingSet.playTogether(fadeIns);

		appearingSet.setStartDelay(newCellsAdditionAnimationStartDelay);
		appearingSet.setDuration(newCellsAdditionAnimationDurationPerCell);
		return appearingSet;
	}

	protected AnimatorSet getAnimationSequence() {

		if (disappearingSet == null && appearingSet == null && movingSet == null)
			return null;

		AnimatorSet allAnim = new AnimatorSet();

		ArrayList<Animator> all = new ArrayList<Animator>();

		if (disappearingSet != null)
			all.add(disappearingSet);

		if (appearingSet != null)
			all.add(appearingSet);

		if (movingSet != null)
			all.add(movingSet);

		if (animateAllSetsSequentially)
			allAnim.playSequentially(all);
		else
			allAnim.playTogether(all);

		return allAnim;
	}

	protected AnimatorSet getItemsMovedAnimation(List<Pair<FreeFlowItem, Rect>> moved) {

		AnimatorSet anim = new AnimatorSet();
		ArrayList<Animator> moves = new ArrayList<Animator>();
		for (Pair<FreeFlowItem, Rect> item : moved) {
			FreeFlowItem proxy = FreeFlowItem.clone(item.first);
			View v = proxy.view;

			proxy.frame.left -= callback.getViewportLeft();
			proxy.frame.top -= callback.getViewportTop();
			proxy.frame.right -= callback.getViewportLeft();
			proxy.frame.bottom -= callback.getViewportTop();

			moves.add(transitionToFrame(item.second, proxy, v));

		}

		anim.playTogether(moves);
		return anim;
	}

	// @Override
	public ValueAnimator transitionToFrame(final Rect of, final FreeFlowItem nf, final View v) {
		ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		anim.setDuration(cellPositionTransitionAnimationDuration);

		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

				try {

					int itemWidth = of.width()
							+ (int) ((nf.frame.width() - of.width()) * animation.getAnimatedFraction());
					int itemHeight = of.height()
							+ (int) ((nf.frame.height() - of.height()) * animation.getAnimatedFraction());
					int widthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);
					int heightSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY);

					v.measure(widthSpec, heightSpec);

					Rect frame = new Rect();
					Rect nff = nf.frame;

					frame.left = (int) (of.left + (nff.left - of.left) * animation.getAnimatedFraction());
					frame.top = (int) (of.top + (nff.top - of.top) * animation.getAnimatedFraction());
					frame.right = frame.left
							+ (int) (of.width() + (nff.width() - of.width()) * animation.getAnimatedFraction());
					frame.bottom = frame.top
							+ (int) (of.height() + (nff.height() - of.height()) * animation.getAnimatedFraction());

					v.layout(frame.left, frame.top, frame.right, frame.bottom);

					// v.layout(nf.frame.left, nf.frame.top, nf.frame.right,
					// nf.frame.bottom);

					// v.setAlpha((1 - alpha) * animation.getAnimatedFraction()
					// + alpha);
				} catch (NullPointerException e) {
					// if(BuildConfig.DEBUG){
					// 	Log.e(TAG, "Nullpointer exception");
					// }
					e.printStackTrace();
					animation.cancel();
				}
			}

		});

		anim.setInterpolator(new DecelerateInterpolator(2.0f));
		return anim;
	}

	public void setDuration(int duration) {
		this.cellPositionTransitionAnimationDuration = duration;
	}

	@Override
	public LayoutChangeset getChangeSet() {
		return changeSet;
	}

	@Override
	public boolean isRunning() {
		return mIsRunning;
	}

	@Override
	public void onContainerTouchDown(MotionEvent event) {
		cancel();
	}

}
