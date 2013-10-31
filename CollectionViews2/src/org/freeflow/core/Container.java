package org.freeflow.core;

import java.util.ArrayList;

import org.freeflow.layouts.LayoutController;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class Container extends ViewGroup {

	private static final String TAG = "Container";
	private SparseArray<View> usedViews;
	private ArrayList<View> viewpool;
	private SparseArray<FrameDescriptor> frames = null;
	private boolean inMeasure = false;
	private BaseAdapter itemAdapter;
	private LayoutController layoutController;
	public int viewPortX = 0;
	public int viewPortY = 0;

	private VelocityTracker mVelocityTracker = null;
	private float deltaX = -1f;
	private float deltaY = -1f;

	public Container(Context context) {
		super(context);
		init();
	}

	public Container(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Container(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		usedViews = new SparseArray<View>();
		viewpool = new ArrayList<View>();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec) - 100);
		if (layoutController != null) {
			inMeasure = true;
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());
			frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);

			for (int i = 0; i < frames.size(); i++) {
				FrameDescriptor frameDesc = frames.get(frames.keyAt(i));
				addAndMeasureViewIfNeeded(frameDesc);
			}
			cleanupViews();
		}
		inMeasure = false;

	}

	private void addAndMeasureViewIfNeeded(FrameDescriptor frameDesc) {
		if (usedViews.get(frameDesc.itemIndex) == null) {
			View view = itemAdapter.getView(frameDesc.itemIndex, viewpool.size() > 0 ? viewpool.remove(0) : null, this);
			usedViews.append(frameDesc.itemIndex, view);
			addView(view);
			doMeasure(frameDesc);
		} else {
			doMeasure(frameDesc);
		}
	}

	private void doMeasure(FrameDescriptor frameDesc) {

		int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height, MeasureSpec.EXACTLY);

		usedViews.get(frameDesc.itemIndex).measure(widthSpec, heightSpec);

	}

	private void cleanupViews() {
		if (usedViews == null)
			return;

		for (int i = usedViews.size() - 1; i >= 0; i--) {
			if (frames.get(usedViews.keyAt(i)) != null)
				continue;

			View view = usedViews.get(usedViews.keyAt(i));
			viewpool.add(view);
			usedViews.remove(usedViews.keyAt(i));
			removeView(view);
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (layoutController == null || frames == null)
			return;
		for (int i = 0; i < usedViews.size(); i++) {

			View v = usedViews.get(usedViews.keyAt(i));

			FrameDescriptor desc = frames.get(usedViews.keyAt(i));

			if (desc == null)
				continue;

			Frame frame = desc.frame;

			if (v == null || frame == null)
				continue;

			doLayout(v, frame);

		}

	}

	private void doLayout(View view, Frame frame) {
		view.layout(frame.left, frame.top, frame.left + frame.width, frame.top + frame.height);
	}

	public void setLayout(LayoutController lc) {

		layoutController = lc;

		SparseArray<FrameDescriptor> oldFrames = frames;

		if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0)
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());

		if (this.itemAdapter != null) {
			layoutController.setItems(itemAdapter);
		}

		if (frames != null) {
			int index = frames.keyAt(0);

			Frame vpFrame = layoutController.getViewportFrameForItemIndex(index);

			viewPortX = vpFrame.left;
			viewPortY = vpFrame.top;

			if (oldFrames != null) {

				frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);
				cleanupViews();

				for (int i = 0; i < frames.size(); i++) {
					FrameDescriptor of = oldFrames.get(frames.keyAt(i));

					int itemIndex = frames.keyAt(i);
					final FrameDescriptor nf = frames.get(itemIndex);

					getAnimationForLayoutTransition(itemIndex,
							of == null ? layoutController.getOffScreenStartFrame() : of.frame, nf).start();
				}

			}

		} else {
			requestLayout();
		}

	}

	protected ValueAnimator getAnimationForLayoutTransition(final int itemIndex, final Frame of,
			final FrameDescriptor nf) {

		if (usedViews.get(nf.itemIndex) == null) {
			addAndMeasureViewIfNeeded(nf);
		}

		return layoutController.getAnimationForLayoutTransition(itemIndex, of, nf, usedViews.get(itemIndex));

	}

	@Override
	public void requestLayout() {

		if (inMeasure)
			return;

		super.requestLayout();
	}

	public void setAdapter(BaseAdapter adapter) {
		this.itemAdapter = adapter;
		if (layoutController != null) {
			layoutController.setItems(adapter);
		}
	}

	public LayoutController getLayoutController() {
		return layoutController;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// Log.d(TAG, "on touch");

		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();

		mVelocityTracker.addMovement(event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			deltaX = event.getX();
			deltaY = event.getY();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			moveScreen(event.getX() - deltaX, event.getY() - deltaY);

			deltaX = event.getX();
			deltaY = event.getY();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			return true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {

			mVelocityTracker.computeCurrentVelocity(1000);

			// frames = layoutController.getFrameDescriptors(viewPortX,
			// viewPortY);

			if (Math.abs(mVelocityTracker.getXVelocity()) > 100) {
				final float velocityX = mVelocityTracker.getXVelocity();
				final float velocityY = mVelocityTracker.getYVelocity();
				ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int translateX = (int) ((1 - animation.getAnimatedFraction()) * velocityX / 350);
						int translateY = (int) ((1 - animation.getAnimatedFraction()) * velocityY / 350);

						moveScreen(translateX, translateY);

						if (animation.getAnimatedFraction() == 1f)
							requestLayout();
					}
				});

				animator.setDuration(500);
				animator.start();

			}

			return true;
		}

		return false;

	}

	private void moveScreen(float movementX, float movementY) {

		if (layoutController.horizontalDragEnabled())
			viewPortX = (int) (viewPortX - movementX);

		if (layoutController.verticalDragEnabled())
			viewPortY = (int) (viewPortY - movementY);

		if (viewPortX < layoutController.getMinimumViewPortX())
			viewPortX = layoutController.getMinimumViewPortX();
		else if (viewPortX > layoutController.getMaximumViewPortX())
			viewPortX = layoutController.getMaximumViewPortX();

		if (viewPortY < layoutController.getMinimumViewPortY())
			viewPortY = layoutController.getMinimumViewPortY();
		else if (viewPortY > layoutController.getMaximumViewPortY())
			viewPortY = layoutController.getMaximumViewPortY();

		frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);

		for (int i = 0; i < frames.size(); i++) {
			FrameDescriptor desc = frames.get(frames.keyAt(i));

			if (usedViews.get(desc.itemIndex) == null)
				addAndMeasureViewIfNeeded(desc);

			View view = usedViews.get(desc.itemIndex);
			doLayout(view, desc.frame);

		}

		cleanupViews();

	}

}
