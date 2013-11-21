package org.freeflow.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
	protected HashMap<? extends Object, FrameDescriptor> frames = null;
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
		frames = new HashMap<Object, FrameDescriptor>();

		((HashMap<Object, FrameDescriptor>) frames).put(new Object(), new FrameDescriptor());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		if (layoutController != null) {
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());
			frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);

			for (FrameDescriptor frameDesc : frames.values()) {
				addAndMeasureViewIfNeeded(frameDesc);
			}

			cleanupViews();
		}
	}

	private void addAndMeasureViewIfNeeded(FrameDescriptor frameDesc) {

		if (frameDesc.isHeader && usedHeaderViews.get(frameDesc.data) == null) {

			View view = itemAdapter.getHeaderViewForSection(frameDesc.itemSection,
					headerViewpool.size() > 0 ? headerViewpool.remove(0) : null, this);
			usedHeaderViews.put(frameDesc.data, view);
			addView(view);
			doMeasure(frameDesc);
		} else if (!frameDesc.isHeader && usedViews.get(frameDesc.data) == null) {

			View view = itemAdapter.getViewForSection(frameDesc.itemSection, frameDesc.itemIndex,
					viewpool.size() > 0 ? viewpool.remove(0) : null, this);
			usedViews.put(frameDesc.data, view);
			addView(view);
			doMeasure(frameDesc);

		} else {
			doMeasure(frameDesc);
		}

		// if (DEBUG)
		// Log.d(TAG, "addAndMeasureViewIfNeeded End " +
		// (System.currentTimeMillis() - start));

	}

	private void doMeasure(FrameDescriptor frameDesc) {

		int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height, MeasureSpec.EXACTLY);

		View v = null;
		if (frameDesc.isHeader)
			v = usedHeaderViews.get(frameDesc.data);
		else
			v = usedViews.get(frameDesc.data);

		v.measure(widthSpec, heightSpec);
		if (v instanceof StateListener)
			((StateListener) v).ReportCurrentState(frameDesc.state);

	}

	private void cleanupViews() {

		if (usedViews == null) {
			return;
		}

		Iterator it = usedViews.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();

			if (frames.get(m.getKey()) != null)
				continue;

			final View view = (View) m.getValue();
			it.remove();
			viewpool.add(view);
			removeView(view);

		}

		it = usedHeaderViews.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();

			if (frames.get(m.getKey()) != null)
				continue;

			final View view = (View) m.getValue();
			it.remove();

			headerViewpool.add(view);
			removeView(view);

		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		if (layoutController == null || frames == null) {
			// if (DEBUG)
			// Log.d(TAG, "onLayout End " + (System.currentTimeMillis() -
			// start));
			return;
		}

		Iterator it = usedViews.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			View v = (View) m.getValue();

			FrameDescriptor desc = frames.get(m.getKey());

			if (desc == null)
				continue;

			Frame frame = desc.frame;

			if (v == null || frame == null)
				continue;

			doLayout(v, frame);

		}

		it = usedHeaderViews.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();

			View v = (View) m.getValue();

			FrameDescriptor desc = frames.get(m.getKey());

			if (desc == null)
				continue;

			Frame frame = desc.frame;

			if (v == null || frame == null)
				continue;

			doLayout(v, frame);
		}

	}

	private void doLayout(View view, Frame frame) {

		view.layout(frame.left - viewPortX, frame.top - viewPortY, frame.left + frame.width - viewPortX, frame.top
				+ frame.height - viewPortY);

	}

	public void setLayout(LayoutController lc) {

		if (lc == layoutController) {
			return;
		}

		boolean shouldReturn = layoutController == null;

		layoutController = lc;

		HashMap<? extends Object, FrameDescriptor> oldFrames = frames;

		if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0)
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());

		if (this.itemAdapter != null) {
			layoutController.setItems(itemAdapter);
		}

		if (shouldReturn)
			return;

		if (frames != null && frames.size() > 0) {

			Object data = null;
			int lowestSection = 99999;
			int lowestPosition = 99999;
			for (FrameDescriptor fd : frames.values()) {
				if (fd.itemSection < lowestSection
						|| (fd.itemSection == lowestSection && fd.itemIndex < lowestPosition)) {
					data = fd.data;
					lowestSection = fd.itemSection;
					lowestPosition = fd.itemIndex;
				}
			}

			Frame vpFrame = layoutController.getFrameDescriptorForItem(data).frame;

			viewPortX = vpFrame.left;
			viewPortY = vpFrame.top;

			if (viewPortX > layoutController.getMaximumViewPortX())
				viewPortX = layoutController.getMaximumViewPortX();

			if (viewPortY > layoutController.getMaximumViewPortY())
				viewPortY = layoutController.getMaximumViewPortY();

			Log.d(TAG, viewPortX + ", " + viewPortY);

			if (oldFrames != null) {
				layoutChanged(oldFrames);
			}

		} else {
			requestLayout();
		}

	}

	protected void transitionToFrame(final FrameDescriptor nf) {

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
			of.left = (int) (v.getLeft() + v.getTranslationX());
			of.top = (int) (v.getTop() + v.getTranslationY());
			of.width = v.getWidth();
			of.height = v.getHeight();
		}

		if (v instanceof StateListener)
			((StateListener) v).ReportCurrentState(nf.state);
		if (nf.frame.equals(of)) {
			return;
		}

		layoutController.getLayoutAnimator().transitionToFrame(of, nf, v);

	}

	public void layoutChanged() {
		HashMap<? extends Object, FrameDescriptor> oldFrames = frames;

		layoutChanged(oldFrames);
	}

	public void layoutChanged(HashMap<? extends Object, FrameDescriptor> oldFrames) {

		layoutController.getLayoutAnimator().clear();

		layoutController.generateFrameDescriptors();
		frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);
		preventLayout = true;
		// cleanupViews();

		Iterator it = frames.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			FrameDescriptor nf = FrameDescriptor.clone((FrameDescriptor) m.getValue());
			
			nf.frame.left -= viewPortX;
			nf.frame.top -= viewPortY;
			
			if (oldFrames.get(m.getKey()) != null)
				oldFrames.remove(m.getKey());

			transitionToFrame(nf);

		}

		it = oldFrames.keySet().iterator();
		while (it.hasNext()) {
			FrameDescriptor nf = layoutController.getFrameDescriptorForItem(it.next());
			nf.frame.left -= viewPortX;
			nf.frame.top -= viewPortY;
			transitionToFrame(nf);
		}

		layoutController.getLayoutAnimator().start();

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
		if (layoutController == null)
			return false;
		if (!layoutController.horizontalDragEnabled() && !layoutController.verticalDragEnabled())
			return false;

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

		Iterator it = frames.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry m = (Map.Entry) it.next();

			FrameDescriptor desc = (FrameDescriptor) m.getValue();

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
