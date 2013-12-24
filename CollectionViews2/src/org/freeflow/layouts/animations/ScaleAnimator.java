package org.freeflow.layouts.animations;

import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver.OnPreDrawListener;

public class ScaleAnimator extends DefaultLayoutAnimator {
	
	
	private int duration = 250;
	
	public ScaleAnimator() {
	}

	@Override
	public void transitionToFrame(final Frame of, final ItemProxy nf, final View v) {

		if(v == null)
			return;
		
		int wSpec = MeasureSpec.makeMeasureSpec(nf.frame.width, MeasureSpec.EXACTLY);
		int hSpec = MeasureSpec.makeMeasureSpec(nf.frame.height, MeasureSpec.EXACTLY);

		final Frame nff = nf.frame;

		v.measure(wSpec, hSpec);
		v.layout(nff.left, nff.top, nff.left + nff.width, nff.top + nff.height);

		v.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {

				v.getViewTreeObserver().removeOnPreDrawListener(this);

				float scaleX = (float) of.width / (float) nff.width;
				float scaleY = (float) of.height / (float) nff.height;

				int tx = nff.left - of.left + (nff.width - of.width) / 2;
				int ty = nff.top - of.top + (nff.height - of.height) / 2;

				v.setScaleX(scaleX);
				v.setScaleY(scaleY);
				v.setTranslationX(-tx);
				v.setTranslationY(-ty);

				v.animate().translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(duration)
						.withEndAction(new Runnable() {

							@Override
							public void run() {
								int wSpec = MeasureSpec.makeMeasureSpec(nff.width, MeasureSpec.EXACTLY);
								int hSpec = MeasureSpec.makeMeasureSpec(nff.height, MeasureSpec.EXACTLY);

								v.setTranslationX(0);
								v.setTranslationY(0);
								v.measure(wSpec, hSpec);
								v.layout(nf.frame.left, nf.frame.top, nf.frame.left + nf.frame.width, nf.frame.top
										+ nf.frame.height);
							}
						});

				return true;
			}
		});

	}

	@Override
	public void clear() {
	}

	@Override
	public void start() {
	}

}
