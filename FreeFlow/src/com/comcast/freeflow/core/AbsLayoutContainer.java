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

import com.comcast.freeflow.layouts.FreeFlowLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

public abstract class AbsLayoutContainer extends ViewGroup {

	protected Map<Object, FreeFlowItem> frames = null;

	protected ArrayList<FreeFlowEventListener> listeners = new ArrayList<FreeFlowEventListener>();

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
	protected FreeFlowItem selectedFreeFlowItem;

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
		void onItemClick(AbsLayoutContainer parent, FreeFlowItem proxy);
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
	public boolean performItemClick(View view, int sectionIndex, int positionInSection, long id) {
		if (mOnItemClickListener != null) {
			// playSoundEffect(SoundEffectConstants.CLICK);
			if (view != null) {
				view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
			}
			mOnItemClickListener.onItemClick(this, getFreeFlowItemForVisibleItemAt(sectionIndex, positionInSection));
			return true;
		}

		return false;
	}

	OnItemSelectedListener mOnItemSelectedListener;

	/**
	 * Interface definition for a callback to be invoked when an item in this
	 * view has been selected.
	 */
	public interface OnItemSelectedListener {
		/**
		 * <p>
		 * Callback method to be invoked when an item in this view has been
		 * selected. This callback is invoked only when the newly selected
		 * position is different from the previously selected position or if
		 * there was no selected item.
		 * </p>
		 * 
		 * Impelmenters can call getItemAtPosition(position) if they need to
		 * access the data associated with the selected item.
		 * 
		 * @param parent
		 *            The AdapterView where the selection happened
		 * @param proxy
		 *            The FreeFlowItem instance representing the item selected
		 * @param id
		 *            The row id of the item that is selected
		 */
		void onItemSelected(AbsLayoutContainer parent, FreeFlowItem proxy);

		/**
		 * Callback method to be invoked when the selection disappears from this
		 * view. The selection can disappear for instance when touch is
		 * activated or when the adapter becomes empty.
		 * 
		 * @param parent
		 *            The AdapterView that now contains no selected item.
		 */
		void onNothingSelected(AbsLayoutContainer parent);
	}

	/**
	 * Register a callback to be invoked when an item in this AdapterView has
	 * been selected.
	 * 
	 * @param listener
	 *            The callback that will run
	 */
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelectedListener = listener;
	}

	public final OnItemSelectedListener getOnItemSelectedListener() {
		return mOnItemSelectedListener;
	}

	public void addFreeFlowEventListener(FreeFlowEventListener listener) {
		if(listeners.indexOf(listener) != -1) return;
		listeners.add(listener);
	}

	public void removeFreeFlowEventListener(FreeFlowEventListener listener) {
		listeners.remove(listener);
	}

	protected void dispatchAnimationsStarting() {
		for (FreeFlowEventListener listener : listeners) {
			listener.layoutChangeAnimationsStarting();
		}
	}

	protected void dispatchLayoutChangeAnimationsComplete() {
		for (FreeFlowEventListener listener : listeners) {
			listener.layoutChangeAnimationsComplete();
		}
	}

	protected void dispatchLayoutComplete(boolean areTransitionAnimationsPlaying) {
		for (FreeFlowEventListener listener : listeners) {
			listener.layoutComplete(areTransitionAnimationsPlaying);
		}
	}

	protected void dispatchLayoutComputed() {
		for (FreeFlowEventListener listener : listeners) {
			listener.layoutComputed();
		}
	}

	protected void dispatchDataChanged() {
		for (FreeFlowEventListener listener : listeners) {
			listener.dataChanged();
		}
	}

	protected void dispatchLayoutChanging(FreeFlowLayout oldLayout, FreeFlowLayout newLayout) {
		for (FreeFlowEventListener listener : listeners) {
			listener.onLayoutChanging(oldLayout, newLayout);
		}
	}
	
	OnItemLongClickListener mOnItemLongClickListener;
	
	/**
     * Interface definition for a callback to be invoked when an item in this
     * view has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in this view has been
         * clicked and held.
         *
         * Implementers can call getItemAtPosition(position) if they need to access
         * the data associated with the selected item.
         *
         * @param parent The AbsListView where the click happened
         * @param view The view within the AbsListView that was clicked
         * @param position The position of the view in the list
         * @param id The row id of the item that was clicked
         *
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(AbsLayoutContainer parent, View view, int sectionIndex, int positionInSection, long id);
    }
    
    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked and held
     *
     * @param listener The callback that will run
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        mOnItemLongClickListener = listener;
    }

    /**
     * @return The callback to be invoked with an item in this AdapterView has
     *         been clicked and held, or null id no callback as been set.
     */
    public final OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public static class AbsLayoutContainerContextMenuInfo implements ContextMenu.ContextMenuInfo {
    	
        public View targetView;
        public int sectionIndex;
        public int positionInSection;
        public long id;
        
        public AbsLayoutContainerContextMenuInfo(View targetView, int sectionIndex, int positionInSection, long id) {
            this.targetView = targetView;
            this.sectionIndex = sectionIndex;
            this.positionInSection = positionInSection;
            this.id = id;
        }
    }
    
    /**
     * Returns the FreeFlowItem instance of a view at position if that
     * view is visible or null if thats not currently visible
     * @param 	section 	The section index of the item 
     * @param	position	The position of the item in the particular section
     * @return	The <code>FreeFlowItem</code> instance representing that section and index. The proxy is guaranteed to have a view associated with it 
     */
	public FreeFlowItem getFreeFlowItemForVisibleItemAt(int section, int position) {
		Iterator<?> it = frames.entrySet().iterator();
		FreeFlowItem proxy = null;
		while (it.hasNext()) {
			Map.Entry<?, FreeFlowItem> pairs = (Map.Entry<?, FreeFlowItem>) it.next();
			proxy = pairs.getValue();
			if (proxy.itemSection == section
					&& proxy.itemIndex == position) {
				return proxy;
			}
		}
		return null;
	}

}
