package org.freeflow.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.freeflow.layouts.AbstractLayout;
import org.freeflow.layouts.animations.DefaultLayoutAnimator;
import org.freeflow.layouts.animations.LayoutAnimator;
import org.freeflow.utils.ViewUtils;

import android.content.Context;
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
import android.widget.Scroller;

public class Container extends AbsLayoutContainer {

	private static final String TAG = "Container";

	// ViewPool class
	protected ViewPool viewpool;

	// Not used yet, but we'll probably need to
	// prevent layout in <code>layout()</code> method
	private boolean preventLayout = false;

	protected BaseSectionedAdapter itemAdapter;
	protected AbstractLayout layout;

	public int viewPortX = 0;
	public int viewPortY = 0;

	protected int scrollableWidth;
	protected int scrollableHeight;

	protected View headerView = null;

	private VelocityTracker mVelocityTracker = null;
	private float deltaX = -1f;
	private float deltaY = -1f;

	private int scrollDeltaX = 0;
	private int scrollDeltaY = 0;

	private int maxFlingVelocity;
	private int touchSlop;

	private Runnable mTouchModeReset;
	private Runnable mPerformClick;
	private Runnable mPendingCheckForTap;
	private Runnable mPendingCheckForLongPress;

	private Scroller scroller;
	private boolean flingStarted = false;

	// This flag controls whether onTap/onLongPress/onTouch trigger
	// the ActionMode
	// private boolean mDataChanged = false;

	private ContextMenuInfo mContextMenuInfo = null;

	private SimpleArrayMap<Position2D, Boolean> mCheckStates = null;

	ActionMode mChoiceActionMode;
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

	int mChoiceMode = CHOICE_MODE_NONE;

	private LayoutParams params = new LayoutParams(0, 0);

	private LayoutAnimator layoutAnimator = new DefaultLayoutAnimator();

	private ItemProxy beginTouchAt;

	private boolean markLayoutDirty = false;
	private boolean markAdapterDirty = false;
	private AbstractLayout oldLayout;

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

		viewpool = new ViewPool();
		frames = new HashMap<Object, ItemProxy>();

		maxFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		scroller = new Scroller(context);
		// scroller.setFriction(ViewConfiguration.getScrollFriction());

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int beforeWidth = getWidth();
		int beforeHeight = getHeight();

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int afterWidth = MeasureSpec.getSize(widthMeasureSpec);
		int afterHeight = MeasureSpec.getSize(heightMeasureSpec);

		if (beforeWidth != afterWidth || beforeHeight != afterHeight || markLayoutDirty) {
			computeLayout(afterWidth, afterHeight);
		}
	}

	public void computeLayout(int w, int h) {

		Log.d(TAG, "=== Computing layout ==== ");

		if (layout != null) {

			layout.setDimensions(w, h);

			if (this.itemAdapter != null)
				layout.setItems(itemAdapter);

			computeViewPort(layout);
			HashMap<? extends Object, ItemProxy> oldFrames = frames;

			if (markLayoutDirty) {
				markLayoutDirty = false;
			}

			// Create a copy of the incoming values because the source
			// Layout
			// may change the map inside its own class
			frames = new HashMap<Object, ItemProxy>(layout.getItemProxies(viewPortX, viewPortY));

			dispatchLayoutComputed();

			animateChanges(getViewChanges(oldFrames, frames));
			//
			// for (ItemProxy frameDesc : changeSet.added) {
			// addAndMeasureViewIfNeeded(frameDesc);
			// }
		}
	}

	private void addAndMeasureViewIfNeeded(ItemProxy frameDesc) {
		View view;
		if (frameDesc.view == null) {

			View convertView = viewpool.getViewFromPool(itemAdapter.getViewType(frameDesc));

			if (frameDesc.isHeader) {
				view = itemAdapter.getHeaderViewForSection(frameDesc.itemSection, convertView, this);
			} else {
				view = itemAdapter.getViewForSection(frameDesc.itemSection, frameDesc.itemIndex, convertView, this);
			}

			if (view instanceof Container)
				throw new IllegalStateException("A container cannot be a direct child view to a container");

			frameDesc.view = view;
			prepareViewForAddition(view, frameDesc);
			addView(view, getChildCount(), params);
		}

		view = frameDesc.view;

		int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width(), MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height(), MeasureSpec.EXACTLY);

		view.measure(widthSpec, heightSpec);
		if (view instanceof StateListener)
			((StateListener) view).ReportCurrentState(frameDesc.state);
	}

	private void prepareViewForAddition(View view, ItemProxy proxy) {
		if (view instanceof Checkable) {
			((Checkable) view).setChecked(isChecked(proxy.itemSection, proxy.itemIndex));
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// Log.d(TAG, "== onLayout ==");
		// mDataChanged = false;
		dispatchLayoutComplete();
	}

	private void doLayout(ItemProxy proxy) {
		View view = proxy.view;
		Rect frame = proxy.frame;

		view.layout(frame.left - viewPortX, frame.top - viewPortY, frame.right - viewPortX, frame.bottom - viewPortY);

		if (view instanceof StateListener)
			((StateListener) view).ReportCurrentState(proxy.state);

	}

	public void setLayout(AbstractLayout lc) {

		if (lc == layout) {
			return;
		}

		oldLayout = layout;
		layout = lc;

		dispatchLayoutChanging(oldLayout, lc);
		dispatchDataChanged();

		markLayoutDirty = true;

		Log.d(TAG, "=== setting layout ===");
		requestLayout();

	}

	public AbstractLayout getLayout() {
		return layout;
	}

	private void computeViewPort(AbstractLayout newLayout) {
		if (layout == null || frames == null || frames.size() == 0) {
			viewPortX = 0;
			viewPortY = 0;
			return;
		}

		Object data = null;
		int lowestSection = 99999;
		int lowestPosition = 99999;
		for (ItemProxy fd : frames.values()) {
			if (fd.itemSection < lowestSection || (fd.itemSection == lowestSection && fd.itemIndex < lowestPosition)) {
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

		scrollableWidth = layout.getContentWidth() - getWidth();
		scrollableHeight = layout.getContentHeight() - getHeight();

		if (viewPortX > scrollableWidth)
			viewPortX = scrollableWidth;

		if (viewPortY > scrollableHeight)
			viewPortY = scrollableHeight;

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
	 * TODO:::: This should be renamed to layoutInvalidated, since the layout
	 * isn't changed
	 */
	public void layoutChanged() {
		Log.d(TAG, "== layoutChanged");
		markLayoutDirty = true;
		dispatchDataChanged();
		requestLayout();
	}

	protected boolean isAnimatingChanges = false;

	private void animateChanges(LayoutChangeSet changeSet) {

		if (changeSet.added.size() == 0 && changeSet.removed.size() == 0 && changeSet.moved.size() == 0) {
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

		Log.d(TAG, "== animating changes: " + changeSet.toString());

		dispatchAnimationsStarted();

		layoutAnimator.animateChanges(changeSet, this);

	}

	public void onLayoutChangeAnimationsCompleted(LayoutAnimator anim) {
		// preventLayout = false;
		isAnimatingChanges = false;
		Log.d(TAG, "=== layout changes complete");
		for (ItemProxy proxy : anim.getChangeSet().getRemoved()) {
			View v = proxy.view;
			removeView(v);
			returnItemToPoolIfNeeded(proxy);
		}

		dispatchAnimationsComplete();

		// changeSet = null;

	}

	public LayoutChangeSet getViewChanges(HashMap<? extends Object, ItemProxy> oldFrames,
			HashMap<? extends Object, ItemProxy> newFrames) {
		return getViewChanges(oldFrames, newFrames, false);
	}

	public LayoutChangeSet getViewChanges(HashMap<? extends Object, ItemProxy> oldFrames,
			HashMap<? extends Object, ItemProxy> newFrames, boolean moveEvenIfSame) {

		// cleanupViews();
		LayoutChangeSet change = new LayoutChangeSet();

		if (oldFrames == null) {
			markAdapterDirty = false;
			Log.d(TAG, "old frames is null");
			for (ItemProxy proxy : newFrames.values()) {
				change.addToAdded(proxy);
			}

			return change;
		}

		if (markAdapterDirty) {
			Log.d(TAG, "old frames is null");
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

				if (moveEvenIfSame || !old.frame.equals(((ItemProxy) m.getValue()).frame)) {

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
	 *            The {@link BaseSectionedAdapter} that will populate this
	 *            Collection
	 */
	public void setAdapter(BaseSectionedAdapter adapter) {

		Log.d(TAG, "setting adapter");
		markLayoutDirty = true;
		markAdapterDirty = true;

		this.itemAdapter = adapter;
		if (adapter != null)
			viewpool.initializeViewPool(adapter.getViewTypes());

		requestLayout();
	}

	public AbstractLayout getLayoutController() {
		return layout;
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		super.onTouchEvent(event);
		if (layout == null)
			return false;

		boolean canScroll = false;

		if (layout.horizontalDragEnabled() && this.layout.getContentWidth() > getWidth()) {
			canScroll = true;
		}
		if (layout.verticalDragEnabled() && layout.getContentHeight() > getHeight()) {
			canScroll = true;
		}

		if (mVelocityTracker == null && canScroll) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		if (mVelocityTracker != null) {
			mVelocityTracker.addMovement(event);
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			scroller.abortAnimation();
			
			beginTouchAt = ViewUtils.getItemAt(frames, (int) (viewPortX + event.getX()),
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

				if (mTouchMode == TOUCH_MODE_DOWN && distance > touchSlop) {
					mTouchMode = TOUCH_MODE_SCROLL;

					if (mPendingCheckForTap != null) {
						removeCallbacks(mPendingCheckForTap);
						mPendingCheckForTap = null;
					}

				}

				if (mTouchMode == TOUCH_MODE_SCROLL) {
					moveScreen(event.getX() - deltaX, event.getY() - deltaY);
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

				// frames = layoutController.getFrameDescriptors(viewPortX,
				// viewPortY);

				Log.d(TAG,
						"vel x = " + (int) mVelocityTracker.getXVelocity() + " vel Y = "
								+ (int) mVelocityTracker.getYVelocity());

				if (Math.abs(mVelocityTracker.getXVelocity()) > 100 || Math.abs(mVelocityTracker.getYVelocity()) > 100) {

					// TODO: add scroller call here...
//					scroller.forceFinished(true);
//					scroller.abortAnimation();

					flingStarted = true;
					scroller.fling(viewPortX, viewPortY, -(int) mVelocityTracker.getXVelocity(),
							-(int) mVelocityTracker.getYVelocity(), 0, layout.getContentWidth() - getWidth(), 0,
							layout.getContentHeight() - getHeight());

					post(scrollRunnable);

					// final float velocityX = mVelocityTracker.getXVelocity();
					// final float velocityY = mVelocityTracker.getYVelocity();
					// ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
					// animator.addUpdateListener(new AnimatorUpdateListener() {
					//
					// @Override
					// public void onAnimationUpdate(ValueAnimator animation) {
					// int translateX = (int) ((1 -
					// animation.getAnimatedFraction()) * velocityX / 350);
					// int translateY = (int) ((1 -
					// animation.getAnimatedFraction()) * velocityY / 350);
					//
					// moveScreen(translateX, translateY);
					//
					// }
					// });
					//
					// animator.setDuration(500);
					// animator.start();

				}
				mTouchMode = TOUCH_MODE_REST;
				Log.d(TAG, "Setting to rest");
			} else if (mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_DONE_WAITING) {
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
							if (beginTouchAt != null && beginTouchAt.view != null) {
								Log.d(TAG, "setting pressed back to false in reset");
								beginTouchAt.view.setPressed(false);
							}
							if (mOnItemSelectedListener != null) {
								mOnItemSelectedListener.onItemSelected(Container.this, selectedItemProxy);
							}

							// setPressed(false);
							// if (!mDataChanged) {
							mPerformClick = new PerformClick();
							mPerformClick.run();
							// }
						}
					};
					selectedItemProxy = beginTouchAt;
					postDelayed(mTouchModeReset, ViewConfiguration.getPressedStateDuration());

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

	//TODO: scroll runnable
	private Runnable scrollRunnable = new Runnable() {

		@Override
		public void run() {
			if (scroller.isFinished()) {
				Log.d("scrolling", "scroller is finished, done with fling");
				return;
			}

			boolean more = scroller.computeScrollOffset();

			if (flingStarted) {
				flingStarted = false;
				scrollDeltaX = scroller.getCurrX();
				scrollDeltaY = scroller.getCurrY();
			}

			Log.d("scrolling", "vel = " + scroller.getCurrVelocity() + ", cur x = " + scroller.getCurrX()
					+ ", cur y = " + scroller.getCurrY() + ", vp x = " + viewPortX);
			int x = scroller.getCurrX();
			int y = scroller.getCurrY();

			int diffx = x - scrollDeltaX;
			int diffy = y - scrollDeltaY;

			scrollDeltaX = x;
			scrollDeltaY = y;

			moveScreen(-diffx, -diffy);

			if (more) {
				post(scrollRunnable);
			}
		}
	};

	private void moveScreen(float movementX, float movementY) {

		if (layout.horizontalDragEnabled()) {
			viewPortX = (int) (viewPortX - movementX);
		} else {
			movementX = 0;
		}

		if (layout.verticalDragEnabled()) {
			viewPortY = (int) (viewPortY - movementY);
		} else {
			movementY = 0;
		}

		scrollableWidth = layout.getContentWidth() - getWidth();
		scrollableHeight = layout.getContentHeight() - getHeight();

		if (viewPortX < 0)
			viewPortX = 0;
		else if (viewPortX > scrollableWidth)
			viewPortX = scrollableWidth;

		if (viewPortY < 0)
			viewPortY = 0;
		else if (viewPortY > scrollableHeight)
			viewPortY = scrollableHeight;

		HashMap<? extends Object, ItemProxy> oldFrames = frames;

		frames = new HashMap<Object, ItemProxy>(layout.getItemProxies(viewPortX, viewPortY));

		LayoutChangeSet changeSet = getViewChanges(oldFrames, frames, true);

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

	public BaseSectionedAdapter getAdapter() {
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

	public void clearChoices() {
		mCheckStates.clear();
	}

	public void setChoiceMode(int choiceMode) {
		mChoiceMode = choiceMode;
		if (mChoiceActionMode != null) {
			mChoiceActionMode.finish();
			mChoiceActionMode = null;
		}
		if (mChoiceMode != CHOICE_MODE_NONE) {
			if (mCheckStates == null) {
				Log.d(TAG, "Creating mCheckStates");
				mCheckStates = new SimpleArrayMap<Container.Position2D, Boolean>();
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
				final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
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
			if (mChoiceActionMode == null && (mChoiceActionMode = startActionMode(mMultiChoiceModeCallback)) != null) {
				setItemChecked(beginTouchAt.itemSection, beginTouchAt.itemIndex, true);
				updateOnScreenCheckedViews();
				performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
			}
			return true;
		}

		boolean handled = false;
		final long longPressId = itemAdapter.getItemId(beginTouchAt.itemSection, beginTouchAt.itemSection);
		if (mOnItemLongClickListener != null) {
			handled = mOnItemLongClickListener.onItemLongClick(this, beginTouchAt.view, beginTouchAt.itemSection,
					beginTouchAt.itemIndex, longPressId);
		}
		if (!handled) {
			mContextMenuInfo = createContextMenuInfo(beginTouchAt.view, beginTouchAt.itemSection,
					beginTouchAt.itemIndex, longPressId);
			handled = super.showContextMenuForChild(this);
		}
		if (handled) {
			updateOnScreenCheckedViews();
			performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
		}
		return handled;
	}

	ContextMenuInfo createContextMenuInfo(View view, int sectionIndex, int positionInSection, long id) {
		return new AbsLayoutContainerContextMenuInfo(view, sectionIndex, positionInSection, id);
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
		public void onItemCheckedStateChanged(ActionMode mode, int section, int position, long id, boolean checked) {
			mWrapped.onItemCheckedStateChanged(mode, section, position, id, checked);

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
		public void onItemCheckedStateChanged(ActionMode mode, int section, int position, long id, boolean checked);
	}

	public void setItemChecked(int sectionIndex, int positionInSection, boolean value) {
		if (mChoiceMode == CHOICE_MODE_NONE) {
			return;
		}

		// Start selection mode if needed. We don't need to if we're unchecking
		// something.
		if (value && mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL && mChoiceActionMode == null) {
			if (mMultiChoiceModeCallback == null || !mMultiChoiceModeCallback.hasWrappedCallback()) {
				throw new IllegalStateException("Container: attempted to start selection mode "
						+ "for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was "
						+ "supplied. Call setMultiChoiceModeListener to set a callback.");
			}
			mChoiceActionMode = startActionMode(mMultiChoiceModeCallback);
		}

		if (mChoiceMode == CHOICE_MODE_MULTIPLE || mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {

			Log.d(TAG, "Setting checked: " + sectionIndex + "/" + positionInSection + ": " + value);
			setCheckedValue(sectionIndex, positionInSection, value);
			if (mChoiceActionMode != null) {
				final long id = itemAdapter.getItemId(sectionIndex, positionInSection);
				mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode, sectionIndex, positionInSection,
						id, value);
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

	class Position2D {
		public int section;
		public int positionInSection;

		public Position2D(int section, int position) {
			this.section = section;
			this.positionInSection = position;
		}

		@Override
		public boolean equals(Object o) {
			if (o.getClass() != Position2D.class) {
				return false;
			}
			Position2D p = (Position2D) o;
			return ((p.section == section) && (p.positionInSection == positionInSection));
		}

		@Override
		public String toString() {
			return "Section: " + section + " index: " + positionInSection;
		}

	}

	@Override
	public boolean performItemClick(View view, int section, int position, long id) {
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
					mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode, section, position, id,
							checked);
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
				performItemClick(view, beginTouchAt.itemSection, beginTouchAt.itemIndex,
						itemAdapter.getItemId(beginTouchAt.itemSection, beginTouchAt.itemIndex));
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
			boolean isChecked = isChecked(pairs.getValue().itemSection, pairs.getValue().itemIndex);
			if (child instanceof Checkable) {
				Log.d(TAG, "Setting checked UI : " + pairs.getValue().itemSection + ", " + pairs.getValue().itemIndex
						+ ": " + isChecked);
				((Checkable) child).setChecked(isChecked);
			} else {
				child.setActivated(isChecked);
			}
		}
	}

	public boolean isChecked(int sectionIndex, int positionInSection) {
		for (int i = 0; i < mCheckStates.size(); i++) {
			Position2D p = mCheckStates.keyAt(i);
			if (p.section == sectionIndex && p.positionInSection == positionInSection) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the internal ArrayMap keeping track of checked states. Will not
	 * update the check UI.
	 */
	protected void setCheckedValue(int sectionIndex, int positionInSection, boolean val) {
		int foundAtIndex = -1;
		for (int i = 0; i < mCheckStates.size(); i++) {
			Position2D p = mCheckStates.keyAt(i);
			if (p.section == sectionIndex && p.positionInSection == positionInSection) {
				foundAtIndex = i;
				break;
			}
		}
		if (foundAtIndex > -1 && val == false) {
			mCheckStates.removeAt(foundAtIndex);
		} else if (foundAtIndex == -1 && val == true) {
			Position2D pos = new Position2D(sectionIndex, positionInSection);
			mCheckStates.put(pos, true);
		}

	}

}
