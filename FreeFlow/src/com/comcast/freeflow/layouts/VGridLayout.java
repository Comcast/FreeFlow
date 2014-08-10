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
package com.comcast.freeflow.layouts;

import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Rect;

import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.utils.ViewUtils;

public class VGridLayout extends FreeFlowLayoutBase implements FreeFlowLayout {

	private static final String TAG = "VGridLayout";
	protected int itemHeight = -1;
	protected int itemWidth = -1;
	protected int headerWidth = -1;
	protected int headerHeight = -1;
	
	protected Map<Object, FreeFlowItem> proxies = new LinkedHashMap<Object, FreeFlowItem>();

	private int cellBufferSize = 0;
	private int bufferCount = 1;
	
	/**
	 * Setting itemFrameInsetX and itemFrameInsetY shrinks the computed frames
	 * of the layout by the values specified. Use this to create gaps between 
	 * the items laid out by this class
	 */
	public int itemFrameInsetX = 0;
	
	/**
	 * Setting itemFrameInsetX and itemFrameInsetY shrinks the computed frames
	 * of the layout by the values specified. Use this to create gaps between 
	 * the items laid out by this class
	 */
	public int itemFrameInsetY = 0;
	
	protected FreeFlowLayoutParams layoutParams;
	
	@Override
	public void setLayoutParams(FreeFlowLayoutParams params){
		if(params.equals(this.layoutParams)){
			return;
		}
		LayoutParams lp = (LayoutParams)params;
		this.itemWidth = lp.itemWidth;
		this.itemHeight = lp.itemHeight;
		this.headerWidth = lp.headerWidth;
		this.headerHeight = lp.headerHeight;
		cellBufferSize = bufferCount * cellBufferSize;
	}

	public void prepareLayout() {
		
		proxies.clear();

		int cols = width / itemWidth;

		int topStart = 0;
		if(itemsAdapter == null) return;

		for (int i = 0; i < itemsAdapter.getNumberOfSections(); i++) {

			Section s = itemsAdapter.getSection(i);

			if (itemsAdapter.shouldDisplaySectionHeaders()) {

				FreeFlowItem header = new FreeFlowItem();
				Rect hframe = new Rect();
				header.itemSection = i;
				header.itemIndex = -1;
				header.isHeader = true;
				hframe.left = 0;
				hframe.top = topStart;
				hframe.right = headerWidth;
				hframe.bottom = topStart + headerHeight;
				header.frame = hframe;
				header.data = s.getHeaderData();
				proxies.put(header.data, header);
				topStart += headerHeight;
			}

			for (int j = 0; j < s.getDataCount(); j++) {
				FreeFlowItem descriptor = new FreeFlowItem();
				Rect frame = new Rect();
				descriptor.itemSection = i;
				descriptor.itemIndex = j;
				frame.left = (j % cols) * itemWidth;
				frame.top = (j / cols) * itemHeight + topStart;
				frame.right = frame.left + itemWidth;
				frame.bottom = frame.top + itemHeight;
				
				frame.inset(itemFrameInsetX, itemFrameInsetY);
				
				descriptor.frame = frame;
				descriptor.data = s.getDataAtIndex(j);
				proxies.put(descriptor.data, descriptor);
			}
			int mod = 0;
			if (s.getDataCount() % cols != 0)
				mod = 1;

			topStart += ((s.getDataCount() / cols) + mod) * itemHeight;
		}

	}
	

	/**
	 * NOTE: In this instance, we subtract/add the cellBufferSize (computed when
	 * item height is set, defaulted to 1 cell) to add a buffer of
	 * cellBufferSize to each end of the viewport
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<Object, FreeFlowItem> getItemProxies(int viewPortLeft, int viewPortTop) {
		LinkedHashMap<Object, FreeFlowItem> desc = new LinkedHashMap<Object, FreeFlowItem>();
		for (FreeFlowItem fd : proxies.values()) {
			if (fd.frame.top + itemHeight > viewPortTop - cellBufferSize
					&& fd.frame.top < viewPortTop + height + cellBufferSize) {
				
				desc.put(fd.data, fd);
			}
		}

		return desc;
	}
	
	@Override
	public FreeFlowItem getItemAt(float x, float y){
		return ViewUtils.getItemAt(proxies, (int)x, (int)y);
	}
	
	@Override
	public boolean horizontalScrollEnabled() {
		return false;
	}

	@Override
	public boolean verticalScrollEnabled() {
		return true;
	}

	@Override
	public int getContentWidth() {
		return width;
	}

	@Override
	public int getContentHeight() {
		if (itemsAdapter == null || itemsAdapter.getNumberOfSections() <= 0){
			return 0;
		}

		int sectionIndex = itemsAdapter.getNumberOfSections() - 1;
		Section s = itemsAdapter.getSection(sectionIndex);

		if (s.getDataCount() == 0)
			return 0;

		Object lastFrameData = s.getDataAtIndex(s.getDataCount() - 1);
		FreeFlowItem fd = proxies.get(lastFrameData);
		if(fd==null){
			return 0;
		}
		return (fd.frame.top + fd.frame.height());
	}

	@Override
	public FreeFlowItem getFreeFlowItemForItem(Object data) {
		return proxies.get(data);
	}

	public void setBufferCount(int bufferCount) {
		this.bufferCount = bufferCount;
	}
	
	public static class LayoutParams extends FreeFlowLayoutParams{
		public int itemWidth = 0;
		public int itemHeight = 0;
		public int headerWidth = 0;
		public int headerHeight = 0;
		
		public LayoutParams(int itemWidth, int itemHeight){
			this.itemWidth = itemWidth;
			this.itemHeight = itemHeight;
		}
		
		public LayoutParams(int itemWidth, int itemHeight, int headerWidth, int headerHeight){
			this.itemWidth = itemWidth;
			this.itemHeight = itemHeight;
			this.headerWidth = headerWidth;
			this.headerHeight = headerHeight;
		}
		
	}
	
	public int getItemWidth(){
		return itemWidth;
	}

}
