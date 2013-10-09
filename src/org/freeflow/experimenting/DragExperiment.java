package org.freeflow.experimenting;

import android.R.anim;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

public class DragExperiment extends ViewGroup {

	private static final String TAG = "DragExperiment";
	private Viewport viewport;
	private VelocityTracker mVelocityTracker = null;

	public DragExperiment(Context context) {
		super(context);
		init();
	}

	public DragExperiment(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	public DragExperiment(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setBackgroundColor(Color.LTGRAY);
		this.viewport = new Viewport();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(1000, 500);

		int freespec = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY);
		for (int i = 0; i < getChildCount(); i++)
			getChildAt(i).measure(freespec, freespec);

	}

	float deltaX = -1f;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();

		mVelocityTracker.addMovement(event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			deltaX = event.getX();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			viewport.setLeft(viewport.getLeft() + (event.getX() - deltaX));

			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				child.setTranslationX(getChildAt(i).getTranslationX() + (event.getX() - deltaX));
				if (child.getX() + child.getMeasuredWidth() > 0)
					child.setVisibility(VISIBLE);
			}

			deltaX = event.getX();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			return true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {

			mVelocityTracker.computeCurrentVelocity(1000);

			if (Math.abs(mVelocityTracker.getXVelocity()) > 100) {
				final float velocity = mVelocityTracker.getXVelocity();
				ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int translateX = (int) ((1 - animation.getAnimatedFraction()) * velocity / 500);
						for (int i = 0; i < getChildCount(); i++) {
							View child = getChildAt(i);
							child.setTranslationX(child.getTranslationX() + translateX);
						}
					}
				});

				animator.setDuration(500);
				animator.start();

			} else {
				requestLayout();
			}
			return true;
		}

		return false;

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int leftStart = 0;

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setTranslationX(0);

			if (((int) viewport.getLeft()) + leftStart + child.getMeasuredWidth() > 0) {
				child.layout(leftStart + (int) viewport.getLeft(), 100, leftStart + child.getMeasuredWidth()
						+ (int) viewport.getLeft(), 100 + child.getMeasuredHeight());
				child.setVisibility(VISIBLE);
			} else {
				Log.d(TAG, "skip");
				child.setVisibility(GONE);
			}

			leftStart += child.getMeasuredWidth();
		}

	}

	public class Viewport {
		private float left = 0, top = 0;

		public void setLeft(float left) {
			this.left = left;
		}

		public void setTop(float top) {
			this.top = top;
		}

		public float getLeft() {
			return left;
		}

		public float getTop() {
			return top;
		}
	}

}
