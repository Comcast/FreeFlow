package org.freeflow.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;

public class Container extends ViewGroup {

	private static final String TAG = "Container";
	protected HashMap<Object, View> usedViews;
	protected HashMap<Object, View> usedHeaderViews;
	protected ArrayList<View> viewpool;
	protected ArrayList<View> headerViewpool;
	protected HashMap<Object, FrameDescriptor> frames = null;
	private boolean preventLayout = false;
	protected BaseSectionedAdapter itemAdapter;
	protected LayoutController layoutController;
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
		usedViews = new HashMap<Object, View>();
		viewpool = new ArrayList<View>();
		usedHeaderViews = new HashMap<Object, View>();
		headerViewpool = new ArrayList<View>();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec) - 100);
		if (layoutController != null) {
			preventLayout = true;
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());
			frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);
			Object[] keyset = frames.keySet().toArray();
			for (int i = 0; i < keyset.length; i++) {
				FrameDescriptor frameDesc = frames.get(keyset[i]);
				addAndMeasureViewIfNeeded(frameDesc);
			}
			cleanupViews();
		}
		preventLayout = false;

	}

	private void addAndMeasureViewIfNeeded(FrameDescriptor frameDesc) {
		if (frameDesc.isHeader && usedHeaderViews.get(frameDesc.data) == null) {

			View view = itemAdapter.getHeaderViewForSection(frameDesc.itemSection,
					headerViewpool.size() > 0 ? headerViewpool.remove(0) : null, this);
			view.setAlpha(1);
			usedHeaderViews.put(frameDesc.data, view);
			preventLayout = true;
			addView(view);
			preventLayout = false;
			doMeasure(frameDesc);
		} else if (!frameDesc.isHeader && usedViews.get(frameDesc.data) == null) {

			View view = itemAdapter.getViewForSection(frameDesc.itemSection, frameDesc.itemIndex,
					viewpool.size() > 0 ? viewpool.remove(0) : null, this);
			view.setAlpha(1);
			usedViews.put(frameDesc.data, view);
			preventLayout = true;
			addView(view);
			preventLayout = false;
			doMeasure(frameDesc);

		} else {
			doMeasure(frameDesc);
		}

	}

	private void doMeasure(FrameDescriptor frameDesc) {

		int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height, MeasureSpec.EXACTLY);

		if (frameDesc.isHeader)
			usedHeaderViews.get(frameDesc.data).measure(widthSpec, heightSpec);
		else
			usedViews.get(frameDesc.data).measure(widthSpec, heightSpec);

	}

	private void cleanupViews() {
		if (usedViews == null)
			return;
		Object[] keyset = usedViews.keySet().toArray();
		for (int i = usedViews.size() - 1; i >= 0; i--) {
			if (frames.get(keyset[i]) != null)
				continue;

			final View view = usedViews.get(keyset[i]);
			usedViews.remove(keyset[i]);

			view.animate().alpha(0).setDuration(250).withEndAction(new Runnable() {

				@Override
				public void run() {
					viewpool.add(view);
					preventLayout = true;
					removeView(view);
					preventLayout = false;
				}
			}).start();

		}

		keyset = usedHeaderViews.keySet().toArray();
		for (int i = usedHeaderViews.size() - 1; i >= 0; i--) {
			if (frames.get(keyset[i]) != null)
				continue;

			final View view = usedHeaderViews.get(keyset[i]);
			usedHeaderViews.remove(keyset[i]);

			view.animate().alpha(0).setDuration(250).withEndAction(new Runnable() {

				@Override
				public void run() {
					headerViewpool.add(view);
					preventLayout = true;
					removeView(view);
					preventLayout = false;
				}
			}).start();

		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (layoutController == null || frames == null)
			return;

		Object[] keyset = usedViews.keySet().toArray();
		for (int i = 0; i < usedViews.size(); i++) {

			View v = usedViews.get(keyset[i]);

			FrameDescriptor desc = frames.get(keyset[i]);

			if (desc == null)
				continue;

			Frame frame = desc.frame;

			if (v == null || frame == null)
				continue;

			doLayout(v, frame);

		}

		keyset = usedHeaderViews.keySet().toArray();
		for (int i = 0; i < usedHeaderViews.size(); i++) {

			View v = usedHeaderViews.get(keyset[i]);

			FrameDescriptor desc = frames.get(keyset[i]);

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

		HashMap<Object, FrameDescriptor> oldFrames = frames;

		if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0)
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());

		if (this.itemAdapter != null) {
			layoutController.setItems(itemAdapter);
		}

		if (frames != null && frames.size() > 0) {

			Object[] keys = frames.keySet().toArray();

			Object data = null;
			int lowestSection = 99999;
			int lowestPosition = 99999;
			for (int i = 0; i < keys.length; i++) {
				FrameDescriptor fd = frames.get(keys[i]);
				if (fd.itemSection < lowestSection
						|| (fd.itemSection == lowestSection && fd.itemIndex < lowestPosition)) {
					data = fd.data;
					lowestSection = fd.itemSection;
					lowestPosition = fd.itemIndex;
				}
			}

			Frame vpFrame = layoutController.getViewportFrameForItem(data);

			viewPortX = vpFrame.left;
			viewPortY = vpFrame.top;

			if (oldFrames != null) {
				layoutChanged(oldFrames);
			}

		} else {
			requestLayout();
		}

	}

	protected ValueAnimator getAnimationForLayoutTransition(final FrameDescriptor nf) {

		boolean newFrame = false;
		if (nf.isHeader) {
			if (usedHeaderViews.get(nf.data) == null) {
				addAndMeasureViewIfNeeded(nf);
				newFrame = true;
			}
		} else {
			if (usedViews.get(nf.data) == null) {
				addAndMeasureViewIfNeeded(nf);
				newFrame = true;
			}
		}

		View v = nf.isHeader ? usedHeaderViews.get(nf.data) : usedViews.get(nf.data);

		Frame of = new Frame();
		if (newFrame) {
			of = layoutController.getOffScreenStartFrame();
		} else {
			of.left = v.getLeft();
			of.top = v.getTop();
			of.width = v.getMeasuredWidth();
			of.height = v.getMeasuredHeight();
		}

		return layoutController.getLayoutAnimator().getFrameTransitionAnimation(of, nf, v);

	}

	protected void layoutChanged() {
		HashMap<Object, FrameDescriptor> oldFrames = frames;
		frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);

		layoutChanged(oldFrames);
	}

	protected void layoutChanged(HashMap<Object, FrameDescriptor> oldFrames) {

		frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);
		preventLayout = true;
		// cleanupViews();
		Object[] keyset = frames.keySet().toArray();
		for (int i = 0; i < frames.size(); i++) {

			final FrameDescriptor nf = frames.get(keyset[i]);

			if (oldFrames.get(keyset[i]) != null)
				oldFrames.remove(keyset[i]);

			getAnimationForLayoutTransition(nf).start();
		}

		keyset = oldFrames.keySet().toArray();
		for (int i = 0; i < oldFrames.size(); i++) {

			FrameDescriptor nf = layoutController.getFrameDescriptorForItemAndViewport(keyset[i], viewPortX, viewPortY);
			getAnimationForLayoutTransition(nf).start();
		}

		preventLayout = false;

	}

	@Override
	public void requestLayout() {

		if (preventLayout)
			return;

		super.requestLayout();
	}

	public void setAdapter(BaseSectionedAdapter adapter) {
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

		Object[] keyset = frames.keySet().toArray();
		for (int i = 0; i < frames.size(); i++) {
			FrameDescriptor desc = frames.get(keyset[i]);

			preventLayout = true;
			if (usedViews.get(desc.data) == null && usedHeaderViews.get(desc.data) == null)
				addAndMeasureViewIfNeeded(desc);
			preventLayout = false;

			View view;
			if (desc.isHeader)
				view = usedHeaderViews.get(desc.data);
			else
				view = usedViews.get(desc.data);

			doLayout(view, desc.frame);

		}

		cleanupViews();

	}

	public BaseSectionedAdapter getAdapter() {
		return itemAdapter;
	}

}
