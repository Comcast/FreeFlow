/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.comcast.freeflow.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.comcast.freeflow.animations.DefaultLayoutAnimator;
import com.comcast.freeflow.animations.LayoutAnimator;
import com.comcast.freeflow.debug.TouchDebugUtils;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.utils.ViewUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Checkable;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

public class Container extends AbsLayoutContainer {

	private static final String TAG = "Container";
	private static final String DEBUG_CONTAINER_LIFECYCLE_TAG = "ContainerLifecycle";

	// ViewPool class
	protected ViewPool viewpool;

	// Not used yet, but we'll probably need to
	// prevent layout in <code>layout()</code> method
	private boolean preventLayout = false;

	protected SectionedAdapter itemAdapter;
	protected FreeFlowLayout layout;

	/**
	 * The X position of the active ViewPort
	 */
	protected int viewPortX = 0;

	/**
	 * The Y position of the active ViewPort
	 */
	protected int viewPortY = 0;

	/**
	 * The scrollable width in pixels. This is usually computed as the
	 * difference between the width of the container and the contentWidth as
	 * computed by the layout.
	 */
	protected int mScrollableWidth;

	/**
	 * The scrollable height in pixels. This is usually computed as the
	 * difference between the height of the container and the contentHeight as
	 * computed by the layout.
	 */
	protected int mScrollableHeight;

	private VelocityTracker mVelocityTracker = null;
	private float deltaX = -1f;
	private float deltaY = -1f;

	private int maxFlingVelocity;
	private int minFlingVelocity;
	private int overflingDistance;
	private int overscrollDistance;
	private int touchSlop;

	private Runnable mTouchModeReset;
	private Runnable mPerformClick;
	private Runnable mPendingCheckForTap;
	private Runnable mPendingCheckForLongPress;

	private OverScroller scroller;

	protected EdgeEffect mLeftEdge, mRightEdge, mTopEdge, mBottomEdge;

	private ArrayList<OnScrollListener> scrollListeners = new ArrayList<Container.OnScrollListener>();

	// This flag controls whether onTap/onLongPress/onTouch trigger
	// the ActionMode
	// private boolean mDataChanged = false;

	/**
	 * TODO: ContextMenu action on long press has not been implemented yet
	 */
	protected ContextMenuInfo mContextMenuInfo = null;

	/**
	 * Holds the checked items when the Container is in CHOICE_MODE_MULTIPLE
	 */
	protected SimpleArrayMap<IndexPath, Boolean> mCheckStates = null;

	ActionMode mChoiceActionMode;

	/**
	 * Wraps the callback for MultiChoiceMode
	 */
	MultiChoiceModeWrapper mMultiChoiceModeCallback;

	/**
	 * Normal list that does not indicate choices
	 */
	public static final int CHOICE_MODE_NONE = 0;

	/**
	 * The list allows up to one choice
	 */
	public static final int CHOICE_MODE_SINGLE = 1;

	/**
	 * The list allows multiple choices
	 */
	public static final int CHOICE_MODE_MULTIPLE = 2;

	/**
	 * The list allows multiple choices in a modal selection mode
	 */
	public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;

	/**
	 * The value of the current ChoiceMode
	 * 
	 * @see <a href=
	 *      "http://developer.android.com/reference/android/widget/AbsListView.html#attr_android:choiceMode"
	 *      >List View's Choice Mode</a>
	 */
	int mChoiceMode = CHOICE_MODE_NONE;

	private LayoutParams params = new LayoutParams(0, 0);

	private LayoutAnimator layoutAnimator = new DefaultLayoutAnimator();

	private ItemProxy beginTouchAt;

	private boolean markLayoutDirty = false;
	private boolean markAdapterDirty = false;
	private FreeFlowLayout oldLayout;

	public Container(Context context) {
		super(context);
	}

	public Container(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Container(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init(Context context) {
		// usedViews = new HashMap<Object, ItemProxy>();
		// usedHeaderViews = new HashMap<Object, ItemProxy>();

		setWillNotDraw(false);

		viewpool = new ViewPool();
		frames = new HashMap<Object, ItemProxy>();

		ViewConfiguration configuration = ViewConfiguration.get(context);
		maxFlingVelocity = configuration.getScaledMaximumFlingVelocity();
		minFlingVelocity = configuration.getScaledMinimumFlingVelocity();
		overflingDistance = configuration.getScaledOverflingDistance();
		overscrollDistance = configuration.getScaledOverscrollDistance();

		touchSlop = configuration.getScaledTouchSlop();

		scroller = new OverScroller(context);
		mLeftEdge = new EdgeEffect(context);
		mRightEdge = new EdgeEffect(context);
		mTopEdge = new EdgeEffect(context);
		mBottomEdge = new EdgeEffect(context);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int beforeWidth = getWidth();
		int beforeHeight = getHeight();

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int afterWidth = MeasureSpec.getSize(widthMeasureSpec);
		int afterHeight = MeasureSpec.getSize(heightMeasureSpec);

		if (beforeWidth != afterWidth || beforeHeight != afterHeight
				|| markLayoutDirty) {
			computeLayout(afterWidth, afterHeight);
		}

	}

	/**
	 * The heart of the system. Calls the layout to get the frames needed,
	 * decides which view should be kept in focus if view transitions are going
	 * to happen and then kicks off animation changes if things have changed
	 * 
	 * @param w
	 *            Width of the viewport. Since right now we don't support
	 *            margins and padding, this is width of the container.
	 * @param h
	 *            Height of the viewport. Since right now we don't support
	 *            margins and padding, this is height of the container.
	 */
	protected void computeLayout(int w, int h) {

		Log.d(DEBUG_CONTAINER_LIFECYCLE_TAG, "Computing layout");

		if (layout != null) {

			layout.setDimensions(w, h);

			if (this.itemAdapter != null)
				layout.setAdapter(itemAdapter);

			computeViewPort(layout);
			HashMap<? extends Object, ItemProxy> oldFrames = frames;

			if (markLayoutDirty) {
				markLayoutDirty = false;
			}

			// Create a copy of the incoming values because the source
			// layout may change the map inside its own class
			frames = new HashMap<Object, ItemProxy>(layout.getItemProxies(
					viewPortX, viewPortY));

			dispatchLayoutComputed();

			animateChanges(getViewChanges(oldFrames, frames));
		}
	}

	/**
	 * Adds a view based on the current viewport. If we can get a view from the
	 * ViewPool, we dont need to construct a new instance, else we will based on
	 * the View class returned by the <code>Adapter</code>
	 * 
	 * @param proxy
	 *            <code>ItemProxy</code> instance that determines the View being
	 *            positioned
	 */
	protected void addAndMeasureViewIfNeeded(ItemProxy proxy) {
		View view;
		if (proxy.view == null) {

			View convertView = viewpool.getViewFromPool(itemAdapter
					.getViewType(proxy));

			if (proxy.isHeader) {
				view = itemAdapter.getHeaderViewForSection(proxy.itemSection,
						convertView, this);
			} else {
				view = itemAdapter.getItemView(proxy.itemSection,
						proxy.itemIndex, convertView, this);
			}

			if (view instanceof Container)
				throw new IllegalStateException(
						"A container cannot be a direct child view to a container");

			proxy.view = view;
			prepareViewForAddition(view, proxy);
			addView(view, getChildCount(), params);
		}

		view = proxy.view;

		int widthSpec = MeasureSpec.makeMeasureSpec(proxy.frame.width(),
				MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(proxy.frame.height(),
				MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
	}

	/**
	 * Does all the necessary work right before a view is about to be laid out.
	 * 
	 * @param view
	 *            The View that will be added to the Container
	 * @param proxy
	 *            The <code>ItemProxy</code> instance that represents the view
	 *            that will be positioned
	 */
	protected void prepareViewForAddition(View view, ItemProxy proxy) {
		if (view instanceof Checkable) {
			((Checkable) view).setChecked(isChecked(proxy.itemSection,
					proxy.itemIndex));
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(DEBUG_CONTAINER_LIFECYCLE_TAG, "onLayout");
		dispatchLayoutComplete(isAnimatingChanges);
		// mDataChanged = false;

	}

	protected void doLayout(ItemProxy proxy) {
		View view = proxy.view;
		Rect frame = proxy.frame;
		view.layout(frame.left - viewPortX, frame.top - viewPortY, frame.right
				- viewPortX, frame.bottom - viewPortY);
	}

	/**
	 * Sets the layout on the Container. If a previous layout was already
	 * applied, this causes the views to animate to the new layout positions.
	 * Scroll positions will also be reset.
	 * 
	 * @see FreeFlowLayout
	 * @param lc
	 */
	public void setLayout(FreeFlowLayout lc) {

		if (lc == layout) {
			return;
		}

		oldLayout = layout;
		layout = lc;

		dispatchLayoutChanging(oldLayout, lc);

		markLayoutDirty = true;
		viewPortX = 0;
		viewPortY = 0;

		Log.d(DEBUG_CONTAINER_LIFECYCLE_TAG, "Setting layout");
		requestLayout();

	}

	/**
	 * @return The layout currently applied to the Container
	 */
	public FreeFlowLayout getLayout() {
		return layout;
	}

	/**
	 * Computes the Rectangle that defines the ViewPort. The Container tries to
	 * keep the view at the top left of the old layout visible in the new
	 * layout.
	 * 
	 * @see getViewportTop
	 * @see getViewportLeft
	 * 
	 */
	private void computeViewPort(FreeFlowLayout newLayout) {
		if (layout == null || frames == null || frames.size() == 0) {
			viewPortX = 0;
			viewPortY = 0;
			return;
		}

		Object data = null;
		int lowestSection = Integer.MAX_VALUE;
		int lowestPosition = Integer.MAX_VALUE;

		// Find the frame of of the first item in the first section in the
		// current set of frames defining the viewport
		// Changing layout will then keep this item in the viewport of the new
		// layout
		// TODO: Need to make sure this item is actually being shown in the
		// viewport and not just in some offscreen buffer
		for (ItemProxy fd : frames.values()) {
			if (fd.itemSection < lowestSection
					|| (fd.itemSection == lowestSection && fd.itemIndex < lowestPosition)) {
				data = fd.data;
				lowestSection = fd.itemSection;
				lowestPosition = fd.itemIndex;
			}
		}

		ItemProxy proxy = newLayout.getItemProxyForItem(data);

		if (proxy == null) {
			viewPortX = 0;
			viewPortY = 0;
			return;
		}

		Rect vpFrame = proxy.frame;

		viewPortX = vpFrame.left;
		viewPortY = vpFrame.top;

		mScrollableWidth = layout.getContentWidth() - getWidth();
		mScrollableHeight = layout.getContentHeight() - getHeight();

		if (mScrollableWidth < 0) {
			mScrollableWidth = 0;
		}
		if (mScrollableHeight < 0) {
			mScrollableHeight = 0;
		}

		if (viewPortX > mScrollableWidth)
			viewPortX = mScrollableWidth;

		if (viewPortY > mScrollableHeight)
			viewPortY = mScrollableHeight;

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
	public Rect getActualFrame(final ItemProxy proxy) {
		View v = proxy.view;
		if (v == null) {
			return null;
		}

		Rect of = new Rect();
		of.left = (int) (v.getLeft() + v.getTranslationX());
		of.top = (int) (v.getTop() + v.getTranslationY());
		of.right = (int) (v.getRight() + v.getTranslationX());
		of.bottom = (int) (v.getBottom() + v.getTranslationY());

		return of;

	}

	/**
	 * TODO: This should be renamed to layoutInvalidated, since the layout isn't
	 * changed
	 */
	public void layoutChanged() {
		Log.d(DEBUG_CONTAINER_LIFECYCLE_TAG, "layoutChanged");
		markLayoutDirty = true;
		dispatchDataChanged();
		requestLayout();
	}

	protected boolean isAnimatingChanges = false;

	private void animateChanges(LayoutChangeset changeSet) {
		if (changeSet.added.size() == 0 && changeSet.removed.size() == 0
				&& changeSet.moved.size() == 0) {
			return;
		}

		for (ItemProxy proxy : changeSet.getAdded()) {
			addAndMeasureViewIfNeeded(proxy);
			doLayout(proxy);
		}

		if (isAnimatingChanges) {
			layoutAnimator.cancel();
		}
		isAnimatingChanges = true;

		Log.d(DEBUG_CONTAINER_LIFECYCLE_TAG,
				"animating changes: " + changeSet.toString());

		dispatchAnimationsStarted();

		layoutAnimator.animateChanges(changeSet, this);

	}

	/**
	 * This method is called by the <code>LayoutAnimator</code> instance once
	 * all transition animations have been completed.
	 * 
	 * @param anim
	 *            The LayoutAnimator instance that reported change complete.
	 */
	public void onLayoutChangeAnimationsCompleted(LayoutAnimator anim) {
		// preventLayout = false;
		isAnimatingChanges = false;
		Log.d(DEBUG_CONTAINER_LIFECYCLE_TAG,
				"layout change animations complete");
		for (ItemProxy proxy : anim.getChangeSet().getRemoved()) {
			View v = proxy.view;
			removeView(v);
			returnItemToPoolIfNeeded(proxy);
		}

		dispatchLayoutChangeAnimationsComplete();

		// changeSet = null;

	}

	public LayoutChangeset getViewChanges(
			HashMap<? extends Object, ItemProxy> oldFrames,
			HashMap<? extends Object, ItemProxy> newFrames) {
		return getViewChanges(oldFrames, newFrames, false);
	}

	public LayoutChangeset getViewChanges(
			HashMap<? extends Object, ItemProxy> oldFrames,
			HashMap<? extends Object, ItemProxy> newFrames,
			boolean moveEvenIfSame) {

		// cleanupViews();
		LayoutChangeset change = new LayoutChangeset();

		if (oldFrames == null) {
			markAdapterDirty = false;
			for (ItemProxy proxy : newFrames.values()) {
				change.addToAdded(proxy);
			}

			return change;
		}

		if (markAdapterDirty) {
			markAdapterDirty = false;
			for (ItemProxy proxy : newFrames.values()) {
				change.addToAdded(proxy);
			}

			for (ItemProxy proxy : oldFrames.values()) {
				change.addToDeleted(proxy);
			}

			return change;
		}

		Iterator<?> it = newFrames.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			ItemProxy proxy = (ItemProxy) m.getValue();

			if (oldFrames.get(m.getKey()) != null) {

				ItemProxy old = oldFrames.remove(m.getKey());
				proxy.view = old.view;

				// if (moveEvenIfSame || !old.compareRect(((ItemProxy)
				// m.getValue()).frame)) {

				if (moveEvenIfSame
						|| !old.frame.equals(((ItemProxy) m.getValue()).frame)) {

					change.addToMoved(proxy, getActualFrame(proxy));
				}
			} else {
				change.addToAdded(proxy);
			}

		}

		for (ItemProxy proxy : oldFrames.values()) {
			change.addToDeleted(proxy);
		}

		frames = newFrames;

		return change;
	}

	@Override
	public void requestLayout() {

		if (!preventLayout) {
			// Log.d(TAG, "== requesting layout ===");
			super.requestLayout();
		}

	}

	/**
	 * Sets the adapter for the this CollectionView.All view pools will be
	 * cleared at this point and all views on the stage will be cleared
	 * 
	 * @param adapter
	 *            The {@link SectionedAdapter} that will populate this
	 *            Collection
	 */
	public void setAdapter(SectionedAdapter adapter) {

		Log.d(DEBUG_CONTAINER_LIFECYCLE_TAG, "setting adapter");
		markLayoutDirty = true;
		markAdapterDirty = true;
		viewPortX = 0;
		viewPortY = 0;

		this.itemAdapter = adapter;
		if (adapter != null)
			viewpool.initializeViewPool(adapter.getViewTypes());

		requestLayout();
	}

	public FreeFlowLayout getLayoutController() {
		return layout;
	}

	/**
	 * The Viewport defines the rectangular "window" that the container is
	 * actually showing of the entire view.
	 * 
	 * @return The left (x) of the viewport within the entire container
	 */
	public int getViewportLeft() {
		return viewPortX;
	}

	/**
	 * The Viewport defines the rectangular "window" that the container is
	 * actually showing of the entire view.
	 * 
	 * @return The top (y) of the viewport within the entire container
	 * 
	 */
	public int getViewportTop() {
		return viewPortY;
	}

	/**
	 * Indicates that we are not in the middle of a touch gesture
	 */
	public static final int TOUCH_MODE_REST = -1;

	/**
	 * Indicates we just received the touch event and we are waiting to see if
	 * the it is a tap or a scroll gesture.
	 */
	public static final int TOUCH_MODE_DOWN = 0;

	/**
	 * Indicates the touch has been recognized as a tap and we are now waiting
	 * to see if the touch is a longpress
	 */
	public static final int TOUCH_MODE_TAP = 1;

	/**
	 * Indicates we have waited for everything we can wait for, but the user's
	 * finger is still down
	 */
	public static final int TOUCH_MODE_DONE_WAITING = 2;

	/**
	 * Indicates the touch gesture is a scroll
	 */
	public static final int TOUCH_MODE_SCROLL = 3;

	/**
	 * Indicates the view is in the process of being flung
	 */
	public static final int TOUCH_MODE_FLING = 4;

	/**
	 * Indicates the touch gesture is an overscroll - a scroll beyond the
	 * beginning or end.
	 */
	public static final int TOUCH_MODE_OVERSCROLL = 5;

	/**
	 * Indicates the view is being flung outside of normal content bounds and
	 * will spring back.
	 */
	public static final int TOUCH_MODE_OVERFLING = 6;

	/**
	 * One of TOUCH_MODE_REST, TOUCH_MODE_DOWN, TOUCH_MODE_TAP,
	 * TOUCH_MODE_SCROLL, or TOUCH_MODE_DONE_WAITING
	 */
	int mTouchMode = TOUCH_MODE_REST;

	/**
	 * The duration for which the scroller will wait before deciding whether the
	 * user was actually trying to stop the scroll or swuipe again to increase
	 * the velocity
	 */
	protected final int FLYWHEEL_TIMEOUT = 40;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		super.onTouchEvent(event);
		if (layout == null)
			return false;

		boolean canScroll = false;

		if (layout.horizontalScrollEnabled()
				&& this.layout.getContentWidth() > getWidth()) {
			canScroll = true;
		}
		if (layout.verticalScrollEnabled()
				&& layout.getContentHeight() > getHeight()) {
			canScroll = true;
		}

		if (mVelocityTracker == null && canScroll) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		if (mVelocityTracker != null) {
			mVelocityTracker.addMovement(event);
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			if (mTouchMode == TOUCH_MODE_FLING) {
				// Wait for some time to see if the user is just trying
				// to speed up the scroll
				postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mTouchMode == TOUCH_MODE_DOWN) {
							if (mTouchMode == TOUCH_MODE_DOWN) {
								scroller.forceFinished(true);
							}
						}
					}
				}, FLYWHEEL_TIMEOUT);
			}

			beginTouchAt = ViewUtils.getItemAt(frames,
					(int) (viewPortX + event.getX()),
					(int) (viewPortY + event.getY()));

			if (canScroll) {
				deltaX = event.getX();
				deltaY = event.getY();
			}
			mTouchMode = TOUCH_MODE_DOWN;

			if (mPendingCheckForTap != null) {
				removeCallbacks(mPendingCheckForTap);
				mPendingCheckForLongPress = null;
			}

			if (beginTouchAt != null) {
				mPendingCheckForTap = new CheckForTap();
			}
			postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			if (canScroll) {
				float xDiff = event.getX() - deltaX;
				float yDiff = event.getY() - deltaY;

				double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
				if ((mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_REST)
						&& distance > touchSlop) {
					mTouchMode = TOUCH_MODE_SCROLL;

					if (mPendingCheckForTap != null) {
						removeCallbacks(mPendingCheckForTap);
						mPendingCheckForTap = null;
					}

				}

				if (mTouchMode == TOUCH_MODE_SCROLL) {
					moveViewportBy(event.getX() - deltaX,
							event.getY() - deltaY, false);
					invokeOnItemScrollListeners();
					deltaX = event.getX();
					deltaY = event.getY();
				}
			}

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			mTouchMode = TOUCH_MODE_REST;

			if (canScroll) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			// requestLayout();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {

			if (mTouchMode == TOUCH_MODE_SCROLL) {
				mVelocityTracker.computeCurrentVelocity(1000, maxFlingVelocity);
				if (Math.abs(mVelocityTracker.getXVelocity()) > minFlingVelocity
						|| Math.abs(mVelocityTracker.getYVelocity()) > minFlingVelocity) {

					int maxX = layout.getContentWidth() - getWidth();
					int maxY = layout.getContentHeight() - getHeight();

					scroller.fling(viewPortX, viewPortY,
							-(int) mVelocityTracker.getXVelocity(),
							-(int) mVelocityTracker.getYVelocity(), 0, maxX, 0,
							maxY, overflingDistance, overflingDistance);

					mTouchMode = TOUCH_MODE_FLING;
					post(flingRunnable);

				} else {
					mTouchMode = TOUCH_MODE_REST;
				}

			} else if (mTouchMode == TOUCH_MODE_DOWN
					|| mTouchMode == TOUCH_MODE_DONE_WAITING) {
				if (mTouchModeReset != null) {
					removeCallbacks(mTouchModeReset);
				}
				if (beginTouchAt != null && beginTouchAt.view != null) {
					beginTouchAt.view.setPressed(true);

					mTouchModeReset = new Runnable() {
						@Override
						public void run() {
							mTouchModeReset = null;
							mTouchMode = TOUCH_MODE_REST;
							if (beginTouchAt != null
									&& beginTouchAt.view != null) {
								beginTouchAt.view.setPressed(false);
							}
							if (mChoiceActionMode == null
									&& mOnItemSelectedListener != null) {
								mOnItemSelectedListener.onItemSelected(
										Container.this, selectedItemProxy);
							}

							// setPressed(false);
							// if (!mDataChanged) {
							mPerformClick = new PerformClick();
							mPerformClick.run();
							// }
						}
					};
					selectedItemProxy = beginTouchAt;
					postDelayed(mTouchModeReset,
							ViewConfiguration.getPressedStateDuration());

					mTouchMode = TOUCH_MODE_TAP;
				} else {
					mTouchMode = TOUCH_MODE_REST;
				}

			}

			return true;
		}

		return false;

	}

	public ItemProxy getSelectedItemProxy() {
		return selectedItemProxy;
	}

	private Runnable flingRunnable = new Runnable() {

		@Override
		public void run() {
			if (scroller.isFinished()) {
				mTouchMode = TOUCH_MODE_REST;
				invokeOnItemScrollListeners();
				return;
			}

			boolean more = scroller.computeScrollOffset();

			if (mLeftEdge.isFinished() && viewPortX < 0
					&& layout.horizontalScrollEnabled()) {
				mLeftEdge.onAbsorb((int) scroller.getCurrVelocity());
			}

			if (mRightEdge.isFinished()
					&& viewPortX > layout.getContentWidth()
							- getMeasuredWidth()
					&& layout.horizontalScrollEnabled()) {
				mRightEdge.onAbsorb((int) scroller.getCurrVelocity());
			}

			if (mTopEdge.isFinished() && viewPortY < 0
					&& layout.verticalScrollEnabled()) {
				mTopEdge.onAbsorb((int) scroller.getCurrVelocity());
			}

			if (mBottomEdge.isFinished()
					&& viewPortY > layout.getContentHeight()
							- getMeasuredHeight()
					&& layout.verticalScrollEnabled()) {
				mBottomEdge.onAbsorb((int) scroller.getCurrVelocity());
			}

			if (layout.horizontalScrollEnabled()) {
				viewPortX = scroller.getCurrX();
			}
			if (layout.verticalScrollEnabled()) {
				viewPortY = scroller.getCurrY();
			}

			moveViewport(true);

			if (more) {
				post(flingRunnable);
			}
		}
	};

	protected void moveViewportBy(float movementX, float movementY,
			boolean fling) {

		if (layout.horizontalScrollEnabled()) {
			viewPortX = (int) (viewPortX - movementX);
		}

		if (layout.verticalScrollEnabled()) {
			viewPortY = (int) (viewPortY - movementY);
		}
		moveViewport(fling);
	}

	protected void moveViewPort(int left, int top, boolean isInFlingMode) {
		viewPortX = left;
		viewPortY = top;
		moveViewport(isInFlingMode);
	}

	/**
	 * Will move viewport to viewPortX and viewPortY values
	 * 
	 * @param isInFlingMode
	 *            Setting this
	 */
	protected void moveViewport(boolean isInFlingMode) {

		mScrollableWidth = layout.getContentWidth() - getWidth();
		if (mScrollableWidth < 0) {
			mScrollableWidth = 0;
		}
		mScrollableHeight = layout.getContentHeight() - getHeight();
		if (mScrollableHeight < 0) {
			mScrollableHeight = 0;
		}

		if (!isInFlingMode) {
			if (viewPortX < -overflingDistance) {
				viewPortX = -overflingDistance;
			} else if (viewPortX > mScrollableWidth + overflingDistance) {
				viewPortX = (mScrollableWidth + overflingDistance);
			}

			if (viewPortY < (int) (-overflingDistance)) {
				viewPortY = (int) -overflingDistance;
			} else if (viewPortY > mScrollableHeight + overflingDistance) {
				viewPortY = (int) (mScrollableHeight + overflingDistance);
			}

			if (viewPortX <= 0) {
				mLeftEdge.onPull(viewPortX / (-overflingDistance));
				invalidate();
			} else if (viewPortX >= mScrollableWidth) {
				mRightEdge.onPull((viewPortX - mScrollableWidth)
						/ (-overflingDistance));
				invalidate();
			}

			if (viewPortY <= 0) {
				mTopEdge.onPull(viewPortY / (-overflingDistance));
				invalidate();
			} else if (viewPortY >= mScrollableHeight) {
				mBottomEdge.onPull((viewPortY - mScrollableHeight)
						/ (-overflingDistance));
				invalidate();
			}

		}
		HashMap<? extends Object, ItemProxy> oldFrames = frames;

		frames = new HashMap<Object, ItemProxy>(layout.getItemProxies(
				viewPortX, viewPortY));

		LayoutChangeset changeSet = getViewChanges(oldFrames, frames, true);

		for (ItemProxy proxy : changeSet.added) {
			addAndMeasureViewIfNeeded(proxy);
			doLayout(proxy);
		}

		for (Pair<ItemProxy, Rect> proxyPair : changeSet.moved) {
			doLayout(proxyPair.first);
		}

		for (ItemProxy proxy : changeSet.removed) {
			proxy.view.setAlpha(0.3f);
			removeViewInLayout(proxy.view);
			returnItemToPoolIfNeeded(proxy);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		boolean needsInvalidate = false;

		final int height = getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom();
		final int width = getMeasuredWidth();

		if (!mLeftEdge.isFinished()) {
			final int restoreCount = canvas.save();

			canvas.rotate(270);
			canvas.translate(-height + getPaddingTop(), 0);// width);
			mLeftEdge.setSize(height, width);

			needsInvalidate = mLeftEdge.draw(canvas);
			canvas.restoreToCount(restoreCount);
		}

		if (!mTopEdge.isFinished()) {
			final int restoreCount = canvas.save();

			mTopEdge.setSize(width, height);

			needsInvalidate = mTopEdge.draw(canvas);
			canvas.restoreToCount(restoreCount);
		}

		if (!mRightEdge.isFinished()) {
			final int restoreCount = canvas.save();

			canvas.rotate(90);
			canvas.translate(0, -width);// width);
			mRightEdge.setSize(height, width);

			needsInvalidate = mRightEdge.draw(canvas);
			canvas.restoreToCount(restoreCount);
		}

		if (!mBottomEdge.isFinished()) {
			final int restoreCount = canvas.save();

			canvas.rotate(180);
			canvas.translate(-width + getPaddingTop(), -height);

			mBottomEdge.setSize(width, height);

			needsInvalidate = mBottomEdge.draw(canvas);
			canvas.restoreToCount(restoreCount);
		}

		if (needsInvalidate)
			postInvalidateOnAnimation();

	}

	protected void returnItemToPoolIfNeeded(ItemProxy proxy) {
		View v = proxy.view;
		v.setTranslationX(0);
		v.setTranslationY(0);
		v.setRotation(0);
		v.setScaleX(1f);
		v.setScaleY(1f);

		v.setAlpha(1);
		viewpool.returnViewToPool(v);
	}

	public SectionedAdapter getAdapter() {
		return itemAdapter;
	}

	public void setLayoutAnimator(LayoutAnimator anim) {
		layoutAnimator = anim;
	}

	public LayoutAnimator getLayoutAnimator() {
		return layoutAnimator;
	}

	public HashMap<? extends Object, ItemProxy> getFrames() {
		return frames;
	}

	public void clearFrames() {
		removeAllViews();
		frames = null;
	}

	@Override
	public boolean shouldDelayChildPressedState() {
		return true;
	}

	public int getCheckedItemCount() {
		return mCheckStates.size();
	}

	public ArrayList<IndexPath> getCheckedItemPositions() {
		ArrayList<IndexPath> checked = new ArrayList<IndexPath>();
		for (int i = 0; i < mCheckStates.size(); i++) {
			checked.add(mCheckStates.keyAt(i));
		}

		return checked;
	}

	public void clearChoices() {
		mCheckStates.clear();
	}

	/**
	 * Defines the choice behavior for the Container allowing multi-select etc.
	 * 
	 * @see <a href=
	 *      "http://developer.android.com/reference/android/widget/AbsListView.html#attr_android:choiceMode"
	 *      >List View's Choice Mode</a>
	 */
	public void setChoiceMode(int choiceMode) {
		mChoiceMode = choiceMode;
		if (mChoiceActionMode != null) {
			mChoiceActionMode.finish();
			mChoiceActionMode = null;
		}
		if (mChoiceMode != CHOICE_MODE_NONE) {
			if (mCheckStates == null) {
				mCheckStates = new SimpleArrayMap<IndexPath, Boolean>();
			}
			if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
				clearChoices();
				setLongClickable(true);
			}
		}
	}

	boolean isLongClickable = false;

	@Override
	public void setLongClickable(boolean b) {
		isLongClickable = b;
	}

	@Override
	public boolean isLongClickable() {
		return isLongClickable;
	}

	public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
		if (mMultiChoiceModeCallback == null) {
			mMultiChoiceModeCallback = new MultiChoiceModeWrapper();
		}
		mMultiChoiceModeCallback.setWrapped(listener);
	}

	final class CheckForTap implements Runnable {
		@Override
		public void run() {
			if (mTouchMode == TOUCH_MODE_DOWN) {
				mTouchMode = TOUCH_MODE_TAP;
				if (beginTouchAt != null && beginTouchAt.view != null) {
					beginTouchAt.view.setPressed(true);
					// setPressed(true);
				}

				refreshDrawableState();
				final int longPressTimeout = ViewConfiguration
						.getLongPressTimeout();
				final boolean longClickable = isLongClickable();

				if (longClickable) {
					if (mPendingCheckForLongPress == null) {
						mPendingCheckForLongPress = new CheckForLongPress();
					}
					postDelayed(mPendingCheckForLongPress, longPressTimeout);
				} else {
					mTouchMode = TOUCH_MODE_DONE_WAITING;
				}
			}
		}
	}

	private class CheckForLongPress implements Runnable {
		@Override
		public void run() {
			if (beginTouchAt == null) {
				// Assuming child that was being long pressed
				// is no longer valid
				return;
			}

			mCheckStates.clear();
			final View child = beginTouchAt.view;
			if (child != null) {
				boolean handled = false;
				// if (!mDataChanged) {
				handled = performLongPress();
				// }
				if (handled) {
					mTouchMode = TOUCH_MODE_REST;
					// setPressed(false);
					child.setPressed(false);
				} else {
					mTouchMode = TOUCH_MODE_DONE_WAITING;
				}
			}
		}
	}

	boolean performLongPress() {
		// CHOICE_MODE_MULTIPLE_MODAL takes over long press.
		if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
			if (mChoiceActionMode == null
					&& (mChoiceActionMode = startActionMode(mMultiChoiceModeCallback)) != null) {
				setItemChecked(beginTouchAt.itemSection,
						beginTouchAt.itemIndex, true);
				updateOnScreenCheckedViews();
				performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
			}
			return true;
		}

		boolean handled = false;
		final long longPressId = itemAdapter.getItemId(
				beginTouchAt.itemSection, beginTouchAt.itemSection);
		if (mOnItemLongClickListener != null) {
			handled = mOnItemLongClickListener.onItemLongClick(this,
					beginTouchAt.view, beginTouchAt.itemSection,
					beginTouchAt.itemIndex, longPressId);
		}
		if (!handled) {
			mContextMenuInfo = createContextMenuInfo(beginTouchAt.view,
					beginTouchAt.itemSection, beginTouchAt.itemIndex,
					longPressId);
			handled = super.showContextMenuForChild(this);
		}
		if (handled) {
			updateOnScreenCheckedViews();
			performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
		}
		return handled;
	}

	ContextMenuInfo createContextMenuInfo(View view, int sectionIndex,
			int positionInSection, long id) {
		return new AbsLayoutContainerContextMenuInfo(view, sectionIndex,
				positionInSection, id);
	}

	class MultiChoiceModeWrapper implements MultiChoiceModeListener {

		private MultiChoiceModeListener mWrapped;

		public void setWrapped(MultiChoiceModeListener wrapped) {
			mWrapped = wrapped;
		}

		public boolean hasWrappedCallback() {
			return mWrapped != null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			if (mWrapped.onCreateActionMode(mode, menu)) {
				// Initialize checked graphic state?
				setLongClickable(false);
				return true;
			}
			return false;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return mWrapped.onPrepareActionMode(mode, menu);
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return mWrapped.onActionItemClicked(mode, item);
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mWrapped.onDestroyActionMode(mode);
			mChoiceActionMode = null;

			// Ending selection mode means deselecting everything.
			clearChoices();
			updateOnScreenCheckedViews();

			// rememberSyncState();
			requestLayout();

			setLongClickable(true);
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int section,
				int position, long id, boolean checked) {
			mWrapped.onItemCheckedStateChanged(mode, section, position, id,
					checked);

			// If there are no items selected we no longer need the selection
			// mode.
			if (getCheckedItemCount() == 0) {
				mode.finish();
			}
		}
	}

	public interface MultiChoiceModeListener extends ActionMode.Callback {
		/**
		 * Called when an item is checked or unchecked during selection mode.
		 * 
		 * @param mode
		 *            The {@link ActionMode} providing the selection mode
		 * @param section
		 *            The Section of the item that was checked
		 * @param position
		 *            Adapter position of the item in the section that was
		 *            checked or unchecked
		 * @param id
		 *            Adapter ID of the item that was checked or unchecked
		 * @param checked
		 *            <code>true</code> if the item is now checked,
		 *            <code>false</code> if the item is now unchecked.
		 */
		public void onItemCheckedStateChanged(ActionMode mode, int section,
				int position, long id, boolean checked);
	}

	public void setItemChecked(int sectionIndex, int positionInSection,
			boolean value) {
		if (mChoiceMode == CHOICE_MODE_NONE) {
			return;
		}

		// Start selection mode if needed. We don't need to if we're unchecking
		// something.
		if (value && mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL
				&& mChoiceActionMode == null) {
			if (mMultiChoiceModeCallback == null
					|| !mMultiChoiceModeCallback.hasWrappedCallback()) {
				throw new IllegalStateException(
						"Container: attempted to start selection mode "
								+ "for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was "
								+ "supplied. Call setMultiChoiceModeListener to set a callback.");
			}
			mChoiceActionMode = startActionMode(mMultiChoiceModeCallback);
		}

		if (mChoiceMode == CHOICE_MODE_MULTIPLE
				|| mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {

			setCheckedValue(sectionIndex, positionInSection, value);
			if (mChoiceActionMode != null) {
				final long id = itemAdapter.getItemId(sectionIndex,
						positionInSection);
				mMultiChoiceModeCallback.onItemCheckedStateChanged(
						mChoiceActionMode, sectionIndex, positionInSection, id,
						value);
			}
		} else {
			setCheckedValue(sectionIndex, positionInSection, value);
		}

		// if (!mInLayout && !mBlockLayoutRequests) {
		// mDataChanged = true;
		// rememberSyncState();
		requestLayout();
		// }
	}

	@Override
	public boolean performItemClick(View view, int section, int position,
			long id) {
		boolean handled = false;
		boolean dispatchItemClick = true;
		if (mChoiceMode != CHOICE_MODE_NONE) {
			handled = true;
			boolean checkedStateChanged = false;

			if (mChoiceMode == CHOICE_MODE_MULTIPLE
					|| (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL && mChoiceActionMode != null)) {
				boolean checked = isChecked(section, position);
				checked = !checked;
				setCheckedValue(section, position, checked);

				if (mChoiceActionMode != null) {
					mMultiChoiceModeCallback.onItemCheckedStateChanged(
							mChoiceActionMode, section, position, id, checked);
					dispatchItemClick = false;
				}
				checkedStateChanged = true;
			} else if (mChoiceMode == CHOICE_MODE_SINGLE) {
				boolean checked = !isChecked(section, position);
				if (checked) {
					setCheckedValue(section, position, checked);
				}
				checkedStateChanged = true;
			}

			if (checkedStateChanged) {
				updateOnScreenCheckedViews();
			}
		}

		if (dispatchItemClick) {

			handled |= super.performItemClick(view, section, position, id);
		}

		return handled;
	}

	private class PerformClick implements Runnable {
		@Override
		public void run() {
			// if (mDataChanged) return;
			View view = beginTouchAt.view;
			if (view != null) {
				performItemClick(view, beginTouchAt.itemSection,
						beginTouchAt.itemIndex, itemAdapter.getItemId(
								beginTouchAt.itemSection,
								beginTouchAt.itemIndex));
			}
			// }
		}
	}

	/**
	 * Perform a quick, in-place update of the checked or activated state on all
	 * visible item views. This should only be called when a valid choice mode
	 * is active.
	 */
	private void updateOnScreenCheckedViews() {
		Iterator<?> it = frames.entrySet().iterator();
		View child = null;
		while (it.hasNext()) {
			Map.Entry<?, ItemProxy> pairs = (Map.Entry<?, ItemProxy>) it.next();
			child = pairs.getValue().view;
			boolean isChecked = isChecked(pairs.getValue().itemSection,
					pairs.getValue().itemIndex);
			if (child instanceof Checkable) {
				((Checkable) child).setChecked(isChecked);
			} else {
				child.setActivated(isChecked);
			}
		}
	}

	public boolean isChecked(int sectionIndex, int positionInSection) {
		for (int i = 0; i < mCheckStates.size(); i++) {
			IndexPath p = mCheckStates.keyAt(i);
			if (p.section == sectionIndex
					&& p.positionInSection == positionInSection) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the internal ArrayMap keeping track of checked states. Will not
	 * update the check UI.
	 */
	protected void setCheckedValue(int sectionIndex, int positionInSection,
			boolean val) {
		int foundAtIndex = -1;
		for (int i = 0; i < mCheckStates.size(); i++) {
			IndexPath p = mCheckStates.keyAt(i);
			if (p.section == sectionIndex
					&& p.positionInSection == positionInSection) {
				foundAtIndex = i;
				break;
			}
		}
		if (foundAtIndex > -1 && val == false) {
			mCheckStates.removeAt(foundAtIndex);
		} else if (foundAtIndex == -1 && val == true) {
			IndexPath pos = new IndexPath(sectionIndex, positionInSection);
			mCheckStates.put(pos, true);
		}

	}

	public void addScrollListener(OnScrollListener listener) {
		if (!scrollListeners.contains(listener))
			scrollListeners.add(listener);
	}

	public void removeScrollListener(OnScrollListener listener) {
		scrollListeners.remove(listener);
	}

	public void scrollToItem(int sectionIndex, int itemIndex, boolean animate) {
		Section section;

		if (sectionIndex > itemAdapter.getNumberOfSections()
				|| sectionIndex < 0
				|| (section = itemAdapter.getSection(sectionIndex)) == null) {
			return;
		}

		if (itemIndex < 0 || itemIndex > section.getDataCount()) {
			return;
		}

		ItemProxy proxy = layout.getItemProxyForItem(section
				.getDataAtIndex(itemIndex));

		int newVPX = proxy.frame.left;
		int newVPY = proxy.frame.top;

		if (newVPX > layout.getContentWidth() - getMeasuredWidth())
			newVPX = layout.getContentWidth() - getMeasuredWidth();

		if (newVPY > layout.getContentHeight() - getMeasuredHeight())
			newVPY = layout.getContentHeight() - getMeasuredHeight();

		if (animate) {
			scroller.startScroll(viewPortX, viewPortY, (newVPX - viewPortX),
					(viewPortY - newVPY), 1500);
			post(flingRunnable);
		} else {
			moveViewportBy((viewPortX - newVPX), (viewPortY - newVPY), false);
			invokeOnItemScrollListeners();
		}
	}

	/**
	 * Returns the percentage of width scrolled. The values range from 0 to 1
	 * 
	 * @return
	 */
	public float getScrollPercentX() {
		if (layout == null || itemAdapter == null)
			return 0;
		float w = layout.getContentWidth();
		float scrollableWidth = w - getWidth();
		if (scrollableWidth == 0)
			return 0;
		return viewPortX / scrollableWidth;
	}

	/**
	 * Returns the percentage of height scrolled. The values range from 0 to 1
	 * 
	 * @return
	 */
	public float getScrollPercentY() {
		if (layout == null || itemAdapter == null)
			return 0;
		float ht = layout.getContentHeight();
		float scrollableHeight = ht - getHeight();
		if (scrollableHeight == 0)
			return 0;
		return viewPortY / scrollableHeight;
	}

	protected void invokeOnItemScrollListeners() {
		for (OnScrollListener l : scrollListeners) {
			l.onScroll(this);
		}
	}

	protected void reportScrollStateChange(int state) {
		// TODO:
	}

	public interface OnScrollListener {
		public int SCROLL_STATE_IDLE = 0;
		public int SCROLL_STATE_TOUCH_SCROLL = 1;
		public int SCROLL_STATE_FLING = 2;

		public void onScroll(Container container);
	}

}
