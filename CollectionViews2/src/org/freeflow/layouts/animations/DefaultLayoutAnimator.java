package org.freeflow.layouts.animations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.freeflow.core.Container;
import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.LayoutChangeSet;
import org.freeflow.core.StateListener;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;

public class DefaultLayoutAnimator extends LayoutAnimator {

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

	private int cellPositionTransitionAnimationDuration = 250;

	/**
	 * If set to true, this forces the animation sets to animate in the
	 * following sequence: delete then add then move
	 * 
	 * If set to false, all sets will animate in parallel
	 */
	public boolean animateAllSetsSequentially = true;

	/**
	 * If set to true, this forces each view in a set to animate sequentially
	 * 
	 * If set to false, all views for a set will animate in parallel
	 */
	public boolean animateIndividualCellsSequentially = true;

	protected Container callback;
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

	}

	@Override
	public void animateChanges(LayoutChangeSet changeSet, final Container callback) {
		this.changeSet = changeSet;
		this.callback = callback;

		cancel();

		disappearingSet = null;
		appearingSet = null;
		movingSet = null;

		Comparator<ItemProxy> cmp = new Comparator<ItemProxy>() {

			@Override
			public int compare(ItemProxy lhs, ItemProxy rhs) {
				return (lhs.itemSection * 1000 + lhs.itemIndex) - (rhs.itemSection * 1000 + rhs.itemIndex);
			}
		};

		ArrayList<ItemProxy> removed = changeSet.getRemoved();
		if (removed.size() > 0) {
			Collections.sort(removed, cmp);
			disappearingSet = getItemsRemovedAnimation(changeSet.getRemoved());
		}

		ArrayList<ItemProxy> added = changeSet.getAdded();
		if (added.size() > 0) {
			Collections.sort(added, cmp);
			appearingSet = getItemsAddedAnimation(added);
		}

		if (changeSet.getMoved().size() > 0) {
			movingSet = getItemsMovedAnimation(changeSet.getMoved());
		}

		AnimatorSet all = getAnimationSequence();
		if (all == null) {
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
	 *            An ArrayList of <code>ItemProxys</code> removed
	 * @return The AnimatorSet of the removed objects
	 */
	protected AnimatorSet getItemsRemovedAnimation(ArrayList<ItemProxy> removed) {
		AnimatorSet disappearingSet = new AnimatorSet();
		ArrayList<Animator> fades = new ArrayList<Animator>();
		for (ItemProxy proxy : removed) {
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
	protected AnimatorSet getItemsAddedAnimation(ArrayList<ItemProxy> added) {
		AnimatorSet appearingSet = new AnimatorSet();
		ArrayList<Animator> fadeIns = new ArrayList<Animator>();
		for (ItemProxy proxy : added) {
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

	protected AnimatorSet getItemsMovedAnimation(ArrayList<Pair<ItemProxy, Frame>> moved) {

		AnimatorSet anim = new AnimatorSet();
		ArrayList<Animator> moves = new ArrayList<Animator>();
		for (Pair<ItemProxy, Frame> item : moved) {
			ItemProxy proxy = ItemProxy.clone(item.first);
			View v = proxy.view;

			if (v instanceof StateListener)
				((StateListener) v).ReportCurrentState(proxy.state);

			proxy.frame.left -= callback.viewPortX;
			proxy.frame.top -= callback.viewPortY;

			// Log.d(TAG, "vpx = " + callback.viewPortX + ", vpy = " +
			// callback.viewPortY);

			moves.add(transitionToFrame(item.second, proxy, v));

		}

		anim.playTogether(moves);
		return anim;
	}

	// @Override
	public ValueAnimator transitionToFrame(final Frame of, final ItemProxy nf, final View v) {
		ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		anim.setDuration(cellPositionTransitionAnimationDuration);
		final float alpha = v.getAlpha();
		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

				try {

					int itemWidth = of.width + (int) ((nf.frame.width - of.width) * animation.getAnimatedFraction());
					int itemHeight = of.height
							+ (int) ((nf.frame.height - of.height) * animation.getAnimatedFraction());
					int widthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);
					int heightSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY);

					v.measure(widthSpec, heightSpec);

					Frame frame = new Frame();
					Frame nff = nf.frame;

					frame.left = (int) (of.left + (nff.left - of.left) * animation.getAnimatedFraction());
					frame.top = (int) (of.top + (nff.top - of.top) * animation.getAnimatedFraction());
					frame.width = (int) (of.width + (nff.width - of.width) * animation.getAnimatedFraction());
					frame.height = (int) (of.height + (nff.height - of.height) * animation.getAnimatedFraction());

					v.layout(frame.left, frame.top, frame.left + frame.width, frame.top + frame.height);

					v.setAlpha((1 - alpha) * animation.getAnimatedFraction() + alpha);
				} catch (NullPointerException e) {
					e.printStackTrace();
					animation.cancel();
				}
			}

		});

		anim.setInterpolator(new DecelerateInterpolator(2.0f));

		return anim;

	}

	@Override
	public void start() {
	}

	public void setDuration(int duration) {
		this.cellPositionTransitionAnimationDuration = duration;
	}

}
