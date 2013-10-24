package org.freeflow.layouts;

import java.util.ArrayList;

import org.freeflow.core.FrameDescriptor;

import android.widget.BaseAdapter;

public class HLayout implements LayoutController{

	private int itemWidth = -1;
	private int width = -1 ;
	private int height = -1;
	private BaseAdapter itemsAdapter; 
	private ArrayList<FrameDescriptor> frameDescriptors = new ArrayList<FrameDescriptor>();
	
	public void setItemWidth(int i) {
		this.itemWidth = i;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDimensions(int measuredWidth, int measuredHeight) {
		if(measuredHeight == height && measuredWidth == width){
			return;
		}
		this.width = measuredWidth;
		this.height = measuredHeight;
		if(itemsAdapter != null){
			generateFrameDescriptors();
		}
	}


	@Override
	public void setItems(BaseAdapter adapter) {
		this.itemsAdapter = adapter;
	}
	
	/**
	 * TODO: Future optimization: can we avoid object allocation here?
	 */
	public void generateFrameDescriptors(){
		if(itemWidth < 0){
			throw new IllegalStateException("itemWidth not set");
		}
		frameDescriptors.clear();
		for(int i=0; i<itemsAdapter.getCount(); i++){
			FrameDescriptor descriptor = new FrameDescriptor();
			// Complete this
		}
	}

	@Override
	public ArrayList<FrameDescriptor> getFrameDescriptors() {
		return frameDescriptors;
	}
	
	

}
