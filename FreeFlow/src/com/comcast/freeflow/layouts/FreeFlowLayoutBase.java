package com.comcast.freeflow.layouts;

import com.comcast.freeflow.core.SectionedAdapter;

public abstract class FreeFlowLayoutBase implements FreeFlowLayout {
	
	protected int width = -1;
	protected int height = -1;
	
	protected SectionedAdapter itemsAdapter;
	
	@Override
	public void setDimensions(int measuredWidth, int measuredHeight) {
		if (measuredHeight == height && measuredWidth == width) {
			return;
		}
		this.width = measuredWidth;
		this.height = measuredHeight;
	}
	
	
	@Override
	public void setAdapter(SectionedAdapter adapter) {
		this.itemsAdapter = adapter;
	}
	
}
