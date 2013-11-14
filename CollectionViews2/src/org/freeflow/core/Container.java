package org.freeflow.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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

	private static boolean DEBUG = false;

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

	private int animationDuration = 500;

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
		long start = System.currentTimeMillis();
		if (DEBUG)
			Log.d("DEBUG", "OnMeasure Start " + start);

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		if (layoutController != null) {
			preventLayout = true;
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());
			frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);

			for (FrameDescriptor frameDesc : frames.values()) {
				addAndMeasureViewIfNeeded(frameDesc);
			}

			cleanupViews();
		}
		preventLayout = false;

		if (DEBUG)
			Log.d("DEBUG", "OnMeasure End " + (System.currentTimeMillis() - start));

	}

	private void addAndMeasureViewIfNeeded(FrameDescriptor frameDesc) {
		long start = System.currentTimeMillis();
		if (DEBUG)
			Log.d("DEBUG", "addAndMeasureViewIfNeeded Start " + start);

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

		if (DEBUG)
			Log.d("DEBUG", "addAndMeasureViewIfNeeded End " + (System.currentTimeMillis() - start));

	}

	private void doMeasure(FrameDescriptor frameDesc) {
		long start = System.currentTimeMillis();
		if (DEBUG)
			Log.d("DEBUG", "doMeasure Start " + start);

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

		if (DEBUG)
			Log.d("DEBUG", "doMeasure End " + (System.currentTimeMillis() - start));

	}

	private void cleanupViews() {
		long start = System.currentTimeMillis();
		if (DEBUG)
			Log.d("DEBUG", "cleanupViews Start " + start);

		if (usedViews == null) {
			if (DEBUG)
				Log.d("DEBUG", "cleanupViews End " + (System.currentTimeMillis() - start));

			return;
		}

		Iterator it = usedViews.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();

			if (frames.get(m.getKey()) != null)
				continue;

			final View view = (View) m.getValue();
			it.remove();

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

		it = usedHeaderViews.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();

			if (frames.get(m.getKey()) != null)
				continue;

			final View view = (View) m.getValue();
			it.remove();

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

		if (DEBUG)
			Log.d("DEBUG", "cleanupViews End " + (System.currentTimeMillis() - start));

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		long start = System.currentTimeMillis();
		if (DEBUG)
			Log.d("DEBUG", "onLayout Start " + start);

		if (layoutController == null || frames == null) {
			if (DEBUG)
				Log.d("DEBUG", "onLayout End " + (System.currentTimeMillis() - start));
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

		if (DEBUG)
			Log.d("DEBUG", "onLayout End " + (System.currentTimeMillis() - start));

	}

	private void doLayout(View view, Frame frame) {
		long start = System.currentTimeMillis();
		if (DEBUG)
			Log.d("DEBUG", "doLayout start " + start);

		view.layout(frame.left, frame.top, frame.left + frame.width, frame.top + frame.height);

		if (DEBUG)
			Log.d("DEBUG", "doLayout End " + (System.currentTimeMillis() - start));

	}

	public void setLayout(LayoutController lc) {
		long start = System.currentTimeMillis();
		if (DEBUG)
			Log.d("DEBUG", " setLayout start " + start);

		if (lc == layoutController) {
			if (DEBUG)
				Log.d("DEBUG", "setLayout End " + (System.currentTimeMillis() - start));
			return;
		}

		layoutController = lc;

		HashMap<Object, FrameDescriptor> oldFrames = frames;

		if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0)
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());

		if (this.itemAdapter != null) {
			layoutController.setItems(itemAdapter);
		}

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

			Frame vpFrame = layoutController.getViewportFrameForItem(data);

			viewPortX = vpFrame.left;
			viewPortY = vpFrame.top;

			if (oldFrames != null) {
				layoutChanged(oldFrames);
			}

		} else {
			requestLayout();
		}

		if (DEBUG)
			Log.d("DEBUG", "setLayout End " + (System.currentTimeMillis() - start));

	}

	protected void transitionToFrame(final FrameDescriptor nf) {
		long start = System.currentTimeMillis();
		if (DEBUG)
			Log.d("DEBUG", " transitionToFrame start " + start);

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
		layoutController.getLayoutAnimator().transitionToFrame(of, nf, v, animationDuration);

		if (DEBUG)
			Log.d("DEBUG", "transitionToFrame End " + (System.currentTimeMillis() - start));

	}

	public void layoutChanged() {
		HashMap<Object, FrameDescriptor> oldFrames = frames;

		layoutChanged(oldFrames);
	}

	public void layoutChanged(HashMap<Object, FrameDescriptor> oldFrames) {

		long start = System.currentTimeMillis();

		if (DEBUG)
			Log.d("DEBUG", " layoutChanged Start " + start);

		layoutController.getLayoutAnimator().clear();

		layoutController.generateFrameDescriptors();
		frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);
		preventLayout = true;
		// cleanupViews();

		Iterator it = frames.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			final FrameDescriptor nf = (FrameDescriptor) m.getValue();

			if (oldFrames.get(m.getKey()) != null)
				oldFrames.remove(m.getKey());

			transitionToFrame(nf);

		}

		it = oldFrames.keySet().iterator();
		while (it.hasNext()) {
			FrameDescriptor nf = layoutController.getFrameDescriptorForItemAndViewport(it.next(), viewPortX, viewPortY);
			transitionToFrame(nf);

		}

		layoutController.getLayoutAnimator().start(animationDuration);

		preventLayout = false;

		if (DEBUG)
			Log.d("DEBUG", "layoutChanged End " + (System.currentTimeMillis() - start));

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

		long start = System.currentTimeMillis();

		if (DEBUG)
			Log.d("DEBUG", "moveScreen start " + start);

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

		if (DEBUG)
			Log.d("DEBUG", "moveScreen End " + (System.currentTimeMillis() - start));

	}

	public BaseSectionedAdapter getAdapter() {
		return itemAdapter;
	}

	public void setAnimationDuration(int animationDuration) {
		this.animationDuration = animationDuration;
	}

}
