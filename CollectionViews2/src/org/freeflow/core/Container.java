package org.freeflow.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.freeflow.layouts.AbstractLayout;
import org.freeflow.layouts.animations.DefaultLayoutAnimator;
import org.freeflow.layouts.animations.LayoutAnimator;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class Container extends ViewGroup {

	private static final String TAG = "Container";
	protected HashMap<Object, View> usedViews;
	protected HashMap<Object, View> usedHeaderViews;
	protected ArrayList<View> viewpool;
	protected ArrayList<View> headerViewpool;
	protected HashMap<? extends Object, ItemProxy> frames = null;
	private boolean preventLayout = false;
	protected BaseSectionedAdapter itemAdapter;
	protected AbstractLayout layout;
	public int viewPortX = 0;
	public int viewPortY = 0;

	private VelocityTracker mVelocityTracker = null;
	private float deltaX = -1f;
	private float deltaY = -1f;
	private int maxFlingVelocity;

	private LayoutAnimator layoutAnimator = new DefaultLayoutAnimator();

	public Container(Context context) {
		super(context);
		init(context);
	}

	public Container(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public Container(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		usedViews = new HashMap<Object, View>();
		viewpool = new ArrayList<View>();
		usedHeaderViews = new HashMap<Object, View>();
		headerViewpool = new ArrayList<View>();
		frames = new HashMap<Object, ItemProxy>();

		maxFlingVelocity = ViewConfiguration.get(context)
				.getScaledMaximumFlingVelocity();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		onMeasureCalled(w, h);
	}

	public void onMeasureCalled(int w, int h) {
		setMeasuredDimension(w, h);
		if (layout != null) {
			layout.setDimensions(getMeasuredWidth(), getMeasuredHeight());
			frames = layout.getItemProxies(viewPortX, viewPortY);

			for (ItemProxy frameDesc : frames.values()) {
				addAndMeasureViewIfNeeded(frameDesc);
			}

			cleanupViews();
		}
	}

	private void addAndMeasureViewIfNeeded(ItemProxy frameDesc) {
		View view;
		if (frameDesc.isHeader) {
			view = usedHeaderViews.get(frameDesc.data);
			if (view == null) {
				view = itemAdapter.getHeaderViewForSection(
						frameDesc.itemSection,
						headerViewpool.size() > 0 ? headerViewpool.remove(0)
								: null, this);
				usedHeaderViews.put(frameDesc.data, view);
				addView(view);
			}

		} else {
			view = usedViews.get(frameDesc.data);
			if (view == null) {
				view = itemAdapter.getViewForSection(frameDesc.itemSection,
						frameDesc.itemIndex,
						viewpool.size() > 0 ? viewpool.remove(0) : null, this);
				usedViews.put(frameDesc.data, view);
				addView(view);
			}
		}

		int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width,
				MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height,
				MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
		if (view instanceof StateListener)
			((StateListener) view).ReportCurrentState(frameDesc.state);
	}

//	@Override
//	public void addView(View v) {
//		super.addView(v);
//		Log.d(TAG, "New child added...count: " + this.getChildCount());
//	}

	
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

		if (layout == null || frames == null) {
			return;
		}

		Iterator it = usedViews.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			View v = (View) m.getValue();

			ItemProxy desc = frames.get(m.getKey());

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

			ItemProxy desc = frames.get(m.getKey());

			if (desc == null)
				continue;

			Frame frame = desc.frame;

			if (v == null || frame == null)
				continue;

			doLayout(v, frame);
		}

	}

	private void doLayout(View view, Frame frame) {

		view.layout(frame.left - viewPortX, frame.top - viewPortY, frame.left
				+ frame.width - viewPortX, frame.top + frame.height - viewPortY);

	}

	public void setLayout(AbstractLayout lc) {

		if (lc == layout) {
			return;
		}

		boolean shouldReturn = layout == null;

		layout = lc;

		HashMap<? extends Object, ItemProxy> oldFrames = frames;

		if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0)
			layout.setDimensions(getMeasuredWidth(), getMeasuredHeight());
		
		if (this.itemAdapter != null) {
			layout.setItems(itemAdapter);
		}

		if (shouldReturn)
			return;

		if (frames != null && frames.size() > 0) {

			Object data = null;
			int lowestSection = 99999;
			int lowestPosition = 99999;
			for (ItemProxy fd : frames.values()) {
				if (fd.itemSection < lowestSection
						|| (fd.itemSection == lowestSection && fd.itemIndex < lowestPosition)) {
					data = fd.data;
					lowestSection = fd.itemSection;
					lowestPosition = fd.itemIndex;
				}
			}

			Frame vpFrame = layout.getItemProxyForItem(data).frame;

			viewPortX = vpFrame.left;
			viewPortY = vpFrame.top;

			if (viewPortX > layout.getContentWidth())
				viewPortX = layout.getContentWidth();

			if (viewPortY > layout.getContentHeight())
				viewPortY = layout.getContentHeight();

			Log.d(TAG, viewPortX + ", " + viewPortY);

			if (oldFrames != null) {
				// Create a copy of the incoming values because the source
				// Layout
				// may change the map inside its own class
				HashMap<Object, ItemProxy> newFrames = new HashMap<Object, ItemProxy>(
						layout.getItemProxies(viewPortX, viewPortY));
				LayoutChangeSet changeSet = layoutChanged(oldFrames, newFrames);
				animateChanges(changeSet);

			}

		} else {
			requestLayout();
		}
		
	}

	/**
	 * Returns the actual frame for a view as its on stage. The ItemProxy's
	 * frame object always represents the position it wants to be in but actual
	 * frame may be different based on animation etc.
	 * 
	 * @param proxy
	 *            The proxy to get the <code>Frame</code> for
	 * @return The Frame for the proxy or null if that view doesn't exist
	 */
	public Frame getActualFrame(final ItemProxy proxy) {
		View v = proxy.isHeader ? usedHeaderViews.get(proxy.data) : usedViews
				.get(proxy.data);
		if (v == null) {
			return null;
		}

		Frame of = new Frame();
		of.left = (int) (v.getLeft() + v.getTranslationX());
		of.top = (int) (v.getTop() + v.getTranslationY());
		of.width = v.getWidth();
		of.height = v.getHeight();

		return of;

	}

	// protected void transitionToFrame(final ItemProxy proxy) {
	//
	// boolean newFrame = false;
	// if (proxy.isHeader) {
	// if (usedHeaderViews.get(proxy.data) == null) {
	// addAndMeasureViewIfNeeded(proxy);
	// newFrame = true;
	// }
	// } else {
	// if (usedViews.get(proxy.data) == null) {
	// addAndMeasureViewIfNeeded(proxy);
	// newFrame = true;
	// }
	// }
	//
	// View v = proxy.isHeader ? usedHeaderViews.get(proxy.data) :
	// usedViews.get(proxy.data);
	//
	// Frame of = new Frame();
	// if (newFrame) {
	// of = layout.getOffScreenStartFrame();
	// } else {
	// of.left = (int) (v.getLeft() + v.getTranslationX());
	// of.top = (int) (v.getTop() + v.getTranslationY());
	// of.width = v.getWidth();
	// of.height = v.getHeight();
	// }
	//
	// if (v instanceof StateListener)
	// ((StateListener) v).ReportCurrentState(proxy.state);
	// if (proxy.frame.equals(of)) {
	// return;
	// }
	//
	// layoutAnimator.transitionToFrame(of, proxy, v);
	//
	// }

	public void layoutChanged() {
		HashMap<Object, ItemProxy> newFrames = new HashMap<Object, ItemProxy>(
				layout.getItemProxies(viewPortX, viewPortY));
		LayoutChangeSet changeSet = layoutChanged(frames, newFrames);
		animateChanges(changeSet);
	}

	private void animateChanges(LayoutChangeSet changeSet) {
		
		for (ItemProxy proxy : changeSet.getAdded()) {
			addAndMeasureViewIfNeeded(proxy);
		}
		
		ArrayList<Pair<ItemProxy, Frame>> moved = changeSet.getMoved();
		for (Pair<ItemProxy, Frame> item : moved) {
			ItemProxy proxy = item.first;
			View v = proxy.isHeader ? usedHeaderViews.get(proxy.data)
					: usedViews.get(proxy.data);
			layoutAnimator.transitionToFrame(item.second, item.first, v);
		}

		for (ItemProxy proxy : changeSet.removed) {
			View v = proxy.isHeader ? usedHeaderViews.get(proxy.data)
					: usedViews.get(proxy.data);
			if (proxy.isHeader) {
				// usedHeaderViews.remove(v);
				// headerViewpool.add(v);
			} else {
				// usedViews.remove(v);
				// viewpool.add(v);
			}
			removeView(v);
		}
	}

	private LayoutChangeSet layoutChanged(
			HashMap<? extends Object, ItemProxy> oldFrames,
			HashMap<? extends Object, ItemProxy> newFrames) {
		layoutAnimator.clear();
		// cleanupViews();
		LayoutChangeSet change = new LayoutChangeSet();

		Iterator<?> it = newFrames.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			ItemProxy proxy = ItemProxy.clone((ItemProxy) m.getValue());

			proxy.frame.left -= viewPortX;
			proxy.frame.top -= viewPortY;

			if (oldFrames.get(m.getKey()) != null){
				oldFrames.remove(m.getKey());
				change.addToMoved(proxy, getActualFrame(proxy));
			}
			else{
				change.addToAdded(proxy);
			}
				

			// transitionToFrame(proxy);
			

		}

		it = oldFrames.keySet().iterator();
		while (it.hasNext()) {
			ItemProxy proxy = layout.getItemProxyForItem(it.next());
			proxy.frame.left -= viewPortX;
			proxy.frame.top -= viewPortY;
			// transitionToFrame(nf);
			change.addToDeleted(proxy);
		}

		// layoutAnimator.start();
		frames = newFrames;
		return change;
	}

	@Override
	public void requestLayout() {

		if (preventLayout)
			return;

		super.requestLayout();
	}

	/**
	 * Sets the adapter for the this CollectionView.All view pools will be
	 * cleared at this point and all views on the stage will be cleared
	 * 
	 * @param adapter
	 *            The {@link BaseSectionedAdapter} that will populate this
	 *            Collection
	 */
	public void setAdapter(BaseSectionedAdapter adapter) {
		this.itemAdapter = adapter;
		// reset all view caches etc
		viewpool.clear();
		headerViewpool.clear();
		usedHeaderViews = new HashMap<Object, View>();
		usedViews = new HashMap<Object, View>();
		removeAllViews();
		if (layout != null) {
			layout.setItems(adapter);
		}
	}

	public AbstractLayout getLayoutController() {
		return layout;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (layout == null)
			return false;
		if (!layout.horizontalDragEnabled() && !layout.verticalDragEnabled())
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

			mVelocityTracker.computeCurrentVelocity(maxFlingVelocity);

			// frames = layoutController.getFrameDescriptors(viewPortX,
			// viewPortY);

			if (Math.abs(mVelocityTracker.getXVelocity()) > 100) {
				final float velocityX = mVelocityTracker.getXVelocity();
				final float velocityY = mVelocityTracker.getYVelocity();
				ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int translateX = (int) ((1 - animation
								.getAnimatedFraction()) * velocityX / 350);
						int translateY = (int) ((1 - animation
								.getAnimatedFraction()) * velocityY / 350);

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

		if (layout.horizontalDragEnabled())
			viewPortX = (int) (viewPortX - movementX);

		if (layout.verticalDragEnabled())
			viewPortY = (int) (viewPortY - movementY);

		if (viewPortX < 0)
			viewPortX = 0;
		else if (viewPortX > layout.getContentWidth())
			viewPortX = layout.getContentWidth();

		if (viewPortY < 0)
			viewPortY = 0;
		else if (viewPortY > layout.getContentHeight())
			viewPortY = layout.getContentHeight();

		frames = layout.getItemProxies(viewPortX, viewPortY);

		Iterator it = frames.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry m = (Map.Entry) it.next();

			ItemProxy desc = (ItemProxy) m.getValue();

			preventLayout = true;
			if (usedViews.get(desc.data) == null
					&& usedHeaderViews.get(desc.data) == null)
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

	public void setLayoutAnimator(LayoutAnimator anim) {
		layoutAnimator = anim;
	}

	public LayoutAnimator getLayoutAnimator() {
		return layoutAnimator;
	}

}
