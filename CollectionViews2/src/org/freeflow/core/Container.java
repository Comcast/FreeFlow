package org.freeflow.core;

import java.util.ArrayList;

import org.freeflow.layouts.HLayout;
import org.freeflow.layouts.LayoutController;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class Container extends ViewGroup {

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
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(layoutController != null){
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(layoutController == null) return;
		ArrayList<FrameDescriptor> desc = layoutController.getFrameDescriptors();
		
	}

	private LayoutController layoutController;
	public void setLayout(LayoutController lc) {
		layoutController = lc;
		requestLayout();
	}
	
	private BaseAdapter itemAdapter;
	public void setAdapter(BaseAdapter adapter) {
		this.itemAdapter = adapter;
		if(layoutController != null){
			layoutController.setItems(adapter);
		}
	}

}
