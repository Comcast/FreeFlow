package org.freeflow.layouts;

import java.util.ArrayList;

import org.freeflow.core.FrameDescriptor;

import android.widget.BaseAdapter;

public interface LayoutController {
	
	/**
	 * Called whenever Container's onMeasure is triggered
	 * @param measuredWidth
	 * @param measuredHeight
	 */
	public void setDimensions(int measuredWidth, int measuredHeight);
	public void setItems(BaseAdapter adapter);
	public ArrayList<FrameDescriptor> getFrameDescriptors();

}
