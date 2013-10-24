package org.freeflow.layouts;

import org.freeflow.core.FrameDescriptor;

import android.util.SparseArray;
import android.widget.BaseAdapter;

public interface LayoutController {
	
	/**
	 * Called whenever Container's onMeasure is triggered
	 * @param measuredWidth
	 * @param measuredHeight
	 */
	public void setDimensions(int measuredWidth, int measuredHeight);
	public void setItems(BaseAdapter adapter);
	
	public SparseArray<FrameDescriptor> getFrameDescriptors(int viewPortLeft, int viewPortTop);

}
