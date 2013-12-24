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

public class Container extends AbsLayoutContainer{

	private static final String TAG = "Container";
	
	// Classes for pooling views and headers
	protected ArrayList<View> viewpool;
	protected ArrayList<View> headerViewpool;
	protected Class itemViewsClass;
	protected Class headerViewsClass;
	
	// Not used yet, but we'll probably need to 
	// prevent layout in <code>layout()</code> method
	private boolean preventLayout = false;
	
	protected BaseSectionedAdapter itemAdapter;
	protected AbstractLayout layout;
	
	public int viewPortX = 0;
	public int viewPortY = 0;

	protected View headerView = null;

	private LayoutChangeSet changeSet = null;

	private VelocityTracker mVelocityTracker = null;
	private float deltaX = -1f;
	private float deltaY = -1f;
	
	private int maxFlingVelocity;
	private int touchSlop;
	
	private LayoutParams params = new LayoutParams(0, 0);

	private LayoutAnimator layoutAnimator = new DefaultLayoutAnimator();
	
	private ItemProxy beginTouchAt;

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

		viewpool = new ArrayList<View>();
		headerViewpool = new ArrayList<View>();
		frames = new HashMap<Object, ItemProxy>();

		maxFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		
		
	}

	
	private int currentWidth;
	private int currentHeight;
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		Log.d(TAG,  "Measure: "+w+", "+h);
		
		if(currentWidth != w || currentHeight != h || markLayoutDirty){
			onMeasureCalled(w, h);
		}
		
	}

	public void onMeasureCalled(int w, int h) {
		setMeasuredDimension(w, h);
		Log.d(TAG, "=== On Measure called==== ");
		if (layout != null) {
			
			layout.setDimensions(getMeasuredWidth(), getMeasuredHeight());
			
			if (this.itemAdapter != null)
				layout.setItems(itemAdapter);

			computeViewPort(layout);
			HashMap<? extends Object, ItemProxy> oldFrames = frames;
			
			if(markLayoutDirty){
				markLayoutDirty = false;
				if(mOnLayoutChangeListener != null){
					mOnLayoutChangeListener.onLayoutChanging(oldLayout, layout);
				}
			}

			// Create a copy of the incoming values because the source
			// Layout
			// may change the map inside its own class
			frames = new HashMap<Object, ItemProxy>(layout.getItemProxies(viewPortX, viewPortY));

			changeSet = getViewChanges(oldFrames, frames);

			animateChanges();
			//
			// for (ItemProxy frameDesc : changeSet.added) {
			// addAndMeasureViewIfNeeded(frameDesc);
			// }
		}
	}

	private void addAndMeasureViewIfNeeded(ItemProxy frameDesc) {
		View view;
		if (frameDesc.view == null) {
			if (frameDesc.isHeader) {
				view = itemAdapter.getHeaderViewForSection(frameDesc.itemSection,
						headerViewpool.size() > 0 ? headerViewpool.remove(0) : null, this);
			} else {
				view = itemAdapter.getViewForSection(frameDesc.itemSection, frameDesc.itemIndex,
						viewpool.size() > 0 ? viewpool.remove(0) : null, this);
			}

			if (view instanceof Container)
				throw new IllegalStateException("A container cannot be a direct child view to a container");

			frameDesc.view = view;
			prepareViewForAddition(view);
			addViewInLayout(view, -1, params);
		}

		view = frameDesc.view;

		int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height, MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
		if (view instanceof StateListener)
			((StateListener) view).ReportCurrentState(frameDesc.state);
	}
	
	private void prepareViewForAddition(View view){
		//view.setOnTouchListener(this);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		if (layout == null || frames == null || changeSet == null) {
			return;
		}

		// animateChanges();
	}

	private void doLayout(ItemProxy proxy) {
		View view = proxy.view;
		Frame frame = proxy.frame;
		view.layout(frame.left - viewPortX, frame.top - viewPortY, frame.left + frame.width - viewPortX, frame.top
				+ frame.height - viewPortY);

		if (view instanceof StateListener)
			((StateListener) view).ReportCurrentState(proxy.state);

	}
	
	private boolean markLayoutDirty = false;
	private AbstractLayout oldLayout;
	public void setLayout(AbstractLayout lc) {

		if (lc == layout) {
			return;
		}
		oldLayout = layout;
		layout = lc;
		markLayoutDirty = true;
		Log.d(TAG, "=== setting layout ===");
		requestLayout();

	}
	
	public AbstractLayout getLayout(){
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

		Frame vpFrame = proxy.frame;

		viewPortX = vpFrame.left;
		viewPortY = vpFrame.top;

		if (viewPortX > newLayout.getContentWidth())
			viewPortX = newLayout.getContentWidth();

		if (viewPortY > newLayout.getContentHeight())
			viewPortY = newLayout.getContentHeight();

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
		View v = proxy.view;
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
	
	/**
	 * TODO:::: This should be renamed to
	 * layoutInvalidated, since the layout 
	 * isn't changed
	 */
	public void layoutChanged() {
		requestLayout();
	}
	
	private void animateChanges(){
		Log.d(TAG, "== animating changes");
		layoutAnimator.animateChanges(changeSet, this);
	}
	
	public void onLayoutChangeAnimationsCompleted(LayoutAnimator anim){
		Log.d(TAG, "=== layout changes complete");
		for (ItemProxy proxy : changeSet.getRemoved()) {
			View v = proxy.view;
			removeViewInLayout(v);
			returnItemToPoolIfNeeded(proxy);			
		}

		for (ItemProxy proxy : changeSet.getAdded()) {
			addAndMeasureViewIfNeeded(proxy);
			doLayout(proxy);
		}
		
		changeSet = null;

	}

	public LayoutChangeSet getViewChanges(HashMap<? extends Object, ItemProxy> oldFrames,
			HashMap<? extends Object, ItemProxy> newFrames) {

		// cleanupViews();
		LayoutChangeSet change = new LayoutChangeSet();

		if (oldFrames == null) {
			Log.d(TAG, "old frames is null");
			for (ItemProxy proxy : newFrames.values()) {
				change.addToAdded(proxy);
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
				change.addToMoved(proxy, getActualFrame(proxy));
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

		Log.d(TAG, "setting adapter");
		markLayoutDirty = true;
		
		this.itemAdapter = adapter;
		
		// If the new adapter uses different types of views for display, flush 
		// the view pools
		View sampleItem = itemAdapter.getViewForSection(0, 0, null, this);
		if(sampleItem != null && sampleItem.getClass() != itemViewsClass){
			viewpool.clear();
			itemViewsClass = sampleItem.getClass();
		}
		viewpool.add(sampleItem);
		View sampleHeader  = itemAdapter.getHeaderViewForSection(0, null, this);
		if( sampleHeader!= null  && sampleHeader.getClass() != headerViewsClass){
			headerViewpool.clear();
			headerViewsClass = sampleHeader.getClass();
		}
		headerViewpool.add(sampleHeader);
		
		requestLayout();
	}

	public AbstractLayout getLayoutController() {
		return layout;
	}
	
	/**
     * Indicates that we are not in the middle of a touch gesture
     */
    static final int TOUCH_MODE_REST = -1;

    /**
     * Indicates we just received the touch event and we are waiting to see if the it is a tap or a
     * scroll gesture.
     */
    static final int TOUCH_MODE_DOWN = 0;

    /**
     * Indicates the touch has been recognized as a tap and we are now waiting to see if the touch
     * is a longpress
     */
    static final int TOUCH_MODE_TAP = 1;

    /**
     * Indicates we have waited for everything we can wait for, but the user's finger is still down
     */
    static final int TOUCH_MODE_DONE_WAITING = 2;

    /**
     * Indicates the touch gesture is a scroll
     */
    static final int TOUCH_MODE_SCROLL = 3;

    /**
     * Indicates the view is in the process of being flung
     */
    static final int TOUCH_MODE_FLING = 4;

    /**
     * Indicates the touch gesture is an overscroll - a scroll beyond the beginning or end.
     */
    static final int TOUCH_MODE_OVERSCROLL = 5;

    /**
     * Indicates the view is being flung outside of normal content bounds
     * and will spring back.
     */
    static final int TOUCH_MODE_OVERFLING = 6;

    /**
     * One of TOUCH_MODE_REST, TOUCH_MODE_DOWN, TOUCH_MODE_TAP, TOUCH_MODE_SCROLL, or
     * TOUCH_MODE_DONE_WAITING
     */
    int mTouchMode = TOUCH_MODE_REST;
    
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (layout == null)
			return false;
		if (!layout.horizontalDragEnabled() && !layout.verticalDragEnabled())
			return false;

		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();

		mVelocityTracker.addMovement(event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			beginTouchAt = layout.getItemAt(viewPortX + event.getX(), viewPortY+event.getY());
			
			deltaX = event.getX();
			deltaY = event.getY();
			
			mTouchMode = TOUCH_MODE_DOWN;

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			
			float xDiff = event.getX() - deltaX;
			float yDiff = event.getY() - deltaY;
			
			double distance = Math.sqrt( xDiff*xDiff + yDiff*yDiff);
			
			if(mTouchMode == TOUCH_MODE_DOWN){
				if(distance > touchSlop){
					mTouchMode = TOUCH_MODE_SCROLL;
				}
			}
			if(mTouchMode == TOUCH_MODE_SCROLL){
				moveScreen(event.getX() - deltaX, event.getY() - deltaY);
				deltaX = event.getX();
				deltaY = event.getY();
			}
			return true;

		} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			
			mTouchMode = TOUCH_MODE_REST;
			
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			// requestLayout();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.d(TAG, "Action Up");
			if(mTouchMode == TOUCH_MODE_SCROLL){
				Log.d(TAG, "Scroll....");
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
							int translateX = (int) ((1 - animation.getAnimatedFraction()) * velocityX / 350);
							int translateY = (int) ((1 - animation.getAnimatedFraction()) * velocityY / 350);

							moveScreen(translateX, translateY);

						}
					});

					animator.setDuration(500);
					animator.start();

				}
				mTouchMode = TOUCH_MODE_REST;
				Log.d(TAG, "Setting to rest");
			}
			
			else{
				Log.d(TAG, "Select");
				selectedItemProxy = beginTouchAt;
				if(mOnItemSelectedListener != null){
					mOnItemSelectedListener.onItemSelected(this, selectedItemProxy);
				}
				
				mTouchMode = TOUCH_MODE_REST;
			}

			return true;
		}

		return false;

	}

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

		if (viewPortX < 0)
			viewPortX = 0;
		else if (viewPortX > layout.getContentWidth())
			viewPortX = layout.getContentWidth();

		if (viewPortY < 0)
			viewPortY = 0;
		else if (viewPortY > layout.getContentHeight())
			viewPortY = layout.getContentHeight();

		HashMap<? extends Object, ItemProxy> oldFrames = frames;

		frames = new HashMap<Object, ItemProxy>(layout.getItemProxies(viewPortX, viewPortY));

		layoutAnimator.clear();
		changeSet = getViewChanges(oldFrames, frames);

		for (ItemProxy proxy : changeSet.added) {
			addAndMeasureViewIfNeeded(proxy);
			doLayout(proxy);
		}

		for (Pair<ItemProxy, Frame> proxyPair : changeSet.moved) {
			doLayout(proxyPair.first);
		}

		for (ItemProxy proxy : changeSet.removed) {
			removeViewInLayout(proxy.view);
			returnItemToPoolIfNeeded(proxy);
		}

	}
	
	protected void returnItemToPoolIfNeeded(ItemProxy proxy){
		View v = proxy.view;
		v.getMatrix().reset();
		v.setAlpha(1);
		if (proxy.isHeader) {
			if(proxy.getClass() == headerViewsClass){
				headerViewpool.add(v);
			}
		} else {
			if(proxy.getClass() == itemViewsClass){
				viewpool.add(v);
			}
		}
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
	
	protected OnLayoutChangeListener mOnLayoutChangeListener;
	
	/**
	 * Interface that all listeners interested in layout change
	 * events must implement
	 *
	 */
	public interface OnLayoutChangeListener{
		/**
		 * Called when the layout is about to change. Measurements based 
		 * on the current data provider and current size have been completed.
		 * @param oldLayout
		 * @param newLayout
		 */
		public void onLayoutChanging(AbstractLayout oldLayout, AbstractLayout newLayout);
	}

}
