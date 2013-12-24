package org.freeflow.layouts.animations;

import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;

public class DefaultLayoutAnimator extends LayoutAnimator {

	private int duration = 250;

	public DefaultLayoutAnimator() {
	}

	@Override
	public void clear() {

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
