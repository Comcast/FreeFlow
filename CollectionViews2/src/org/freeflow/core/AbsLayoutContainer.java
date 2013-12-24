package org.freeflow.core;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

public abstract class AbsLayoutContainer extends ViewGroup implements
		OnTouchListener {

	protected HashMap<? extends Object, ItemProxy> frames = null;

	public AbsLayoutContainer(Context context) {
		super(context);
		init(context);
	}

	public AbsLayoutContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AbsLayoutContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	protected void init(Context c) {

	}

	// //////////////////CLICK BEHAVIOR //////
	protected ItemProxy selectedItemProxy;
	
	public interface OnItemClickListener {

		/**
		 * Callback method to be invoked when an item in this AdapterView has
		 * been clicked.
		 * <p>
		 * Implementers can call getItemAtPosition(position) if they need to
		 * access the data associated with the selected item.
		 * 
		 * @param parent
		 *            The AdapterView where the click happened.
		 * @param view
		 *            The view within the AdapterView that was clicked (this
		 *            will be a view provided by the adapter)
		 * @param position
		 *            The position of the view in the adapter.
		 * @param id
		 *            The row id of the item that was clicked.
		 */
		void onItemClick(AbsLayoutContainer parent, ItemProxy proxy);
	}

	protected OnItemClickListener mOnItemClickListener;

	/**
	 * Register a callback to be invoked when an item in this AdapterView has
	 * been clicked.
	 * 
	 * @param listener
	 *            The callback that will be invoked.
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	/**
	 * @return The callback to be invoked with an item in this AdapterView has
	 *         been clicked, or null id no callback has been set.
	 */
	public final OnItemClickListener getOnItemClickListener() {
		return mOnItemClickListener;
	}

	/**
	 * Call the OnItemClickListener, if it is defined. Performs all normal
	 * actions associated with clicking: reporting accessibility event, playing
	 * a sound, etc.
	 * 
	 * @param view
	 *            The view within the AdapterView that was clicked.
	 * @param position
	 *            The position of the view in the adapter.
	 * @param id
	 *            The row id of the item that was clicked.
	 * @return True if there was an assigned OnItemClickListener that was
	 *         called, false otherwise is returned.
	 */
	public boolean performItemClick(View view, ItemProxy proxy) {
		if (mOnItemClickListener != null) {
			// playSoundEffect(SoundEffectConstants.CLICK);
			if (view != null) {
				view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
			}
			mOnItemClickListener.onItemClick(this, proxy);
			return true;
		}

		return false;
	}
	
	 OnItemSelectedListener mOnItemSelectedListener;
	
	/**
     * Interface definition for a callback to be invoked when
     * an item in this view has been selected.
     */
    public interface OnItemSelectedListener {
        /**
         * <p>Callback method to be invoked when an item in this view has been
         * selected. This callback is invoked only when the newly selected
         * position is different from the previously selected position or if
         * there was no selected item.</p>
         *
         * Impelmenters can call getItemAtPosition(position) if they need to access the
         * data associated with the selected item.
         *
         * @param parent The AdapterView where the selection happened
         * @param proxy The ItemProxy instance representing the item selected
         * @param id The row id of the item that is selected
         */
        void onItemSelected(AbsLayoutContainer parent, ItemProxy proxy);

        /**
         * Callback method to be invoked when the selection disappears from this
         * view. The selection can disappear for instance when touch is activated
         * or when the adapter becomes empty.
         *
         * @param parent The AdapterView that now contains no selected item.
         */
        void onNothingSelected(AbsLayoutContainer parent);
    }

	
	/**
     * Register a callback to be invoked when an item in this AdapterView has
     * been selected.
     *
     * @param listener The callback that will run
     */
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public final OnItemSelectedListener getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			for (ItemProxy proxy : frames.values()) {
				if (proxy.view == v) {
					v.setPressed(false);
					if(proxy != selectedItemProxy){
						selectedItemProxy = proxy;
						if(mOnItemSelectedListener != null){
							mOnItemSelectedListener.onItemSelected(AbsLayoutContainer.this, proxy);
						}
					}
					performItemClick(v, proxy);
				}
			}
		}
		return true;
	}

}
