package org.freeflow.layouts.animations;

import java.util.ArrayList;

import org.freeflow.core.Container;
import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.LayoutChangeSet;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;

public class DefaultLayoutAnimator extends LayoutAnimator {

	public static final String TAG = "DefaultLayoutAnimator";
	private int duration = 250;

	public DefaultLayoutAnimator() {
	}

	@Override
	public void clear() {

	}
	

	@Override
	public void animateChanges(LayoutChangeSet changeSet, final Container callback) {
		AnimatorSet set = new AnimatorSet();
		ArrayList<Animator> fades = new ArrayList<Animator>();
		for (ItemProxy proxy : changeSet.getRemoved()) {
			fades.add(  ObjectAnimator.ofFloat(proxy.view, "alpha", 0) );
		}
		
		
		
		ArrayList<Pair<ItemProxy, Frame>> moved = changeSet.getMoved();

		for (Pair<ItemProxy, Frame> item : moved) {
			ItemProxy proxy = ItemProxy.clone(item.first);
			View v = proxy.view;

			proxy.frame.left -= callback.viewPortX;
			proxy.frame.top -= callback.viewPortY;

//			if (v instanceof StateListener)
//				((StateListener) v).ReportCurrentState(proxy.state);

			transitionToFrame(item.second, proxy, v);
			
		}
		
		if(fades.size() != 0){
			Log.d(TAG, "== playing seq: "+fades.size());
			set.addListener(new AnimatorListener() {
				
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
			set.setDuration(600);
			set.playSequentially(fades);
			set.start();
			
		}
		else{
			Log.d(TAG, "== calling back animations completed");
			callback.onLayoutChangeAnimationsCompleted(this);
		}
		
		
	}

	@Override
	public void transitionToFrame(final Frame of, final ItemProxy nf, final View v) {
		ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		anim.setDuration(duration);
		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

				if (v == null) {
					animation.cancel();
					return;
				}

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
				} catch (NullPointerException e) {
					e.printStackTrace();
					animation.cancel();
				}
			}

		});

		anim.setInterpolator(new DecelerateInterpolator(2.0f));

		anim.start();

	}

	@Override
	public void start() {
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
