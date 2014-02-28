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

import java.util.HashMap;
import java.util.Map;

import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;
import com.comcast.freeflow.layouts.FreeFlowLayout.FreeFlowLayoutParams;
import com.comcast.freeflow.layouts.VGridLayout.LayoutParams;
import com.comcast.freeflow.utils.ViewUtils;

import android.graphics.Rect;

public class HGridLayout extends FreeFlowLayoutBase implements FreeFlowLayout {

	private static final String TAG = "HGridLayout";
	private int itemHeight = -1;
	private int itemWidth = -1;
	private Map<Object, FreeFlowItem> proxies = new HashMap<Object, FreeFlowItem>();
	private int headerWidth = -1;
	private int headerHeight = -1;
	private int cellBufferSize = 0;
	private int bufferCount = 1;
	
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

		int rows = height / itemHeight;
		int leftStart = 0;

		for (int i = 0; i < itemsAdapter.getNumberOfSections(); i++) {

			Section s = itemsAdapter.getSection(i);

			if (itemsAdapter.shouldDisplaySectionHeaders()) {
				
				FreeFlowItem header = new FreeFlowItem();
				Rect hframe = new Rect();
				header.itemSection = i;
				header.itemIndex = -1;
				header.isHeader = true;
				hframe.left = leftStart;
				hframe.top = 0;
				hframe.right = leftStart + headerWidth;
				hframe.bottom = headerHeight;
				header.frame = hframe;
				header.data = s.getHeaderData();
				proxies.put(header.data, header);

				leftStart += headerWidth;
			}

			for (int j = 0; j < s.getDataCount(); j++) {
				FreeFlowItem descriptor = new FreeFlowItem();
				Rect frame = new Rect();
				descriptor.itemSection = i;
				descriptor.itemIndex = j;
				frame.left = (j / rows) * itemWidth + leftStart;
				frame.top = (j % rows) * itemHeight;
				frame.right = frame.left + itemWidth;
				frame.bottom = frame.top + itemHeight;
				descriptor.frame = frame;
				descriptor.data = s.getDataAtIndex(j);
				proxies.put(descriptor.data, descriptor);
			}
			int mod = 0;
			if (s.getDataCount() % rows != 0)
				mod = 1;
			leftStart += ((s.getDataCount() / rows) + mod) * itemWidth;
		}
	}

	/**
	 * NOTE: In this instance, we subtract/add the cellBufferSize (computed when
	 * item width is set, defaulted to 1 cell) to add a buffer of cellBufferSize
	 * to each end of the viewport. <br>
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public HashMap<Object, FreeFlowItem> getItemProxies(int viewPortLeft, int viewPortTop) {
		HashMap<Object, FreeFlowItem> desc = new HashMap<Object, FreeFlowItem>();

		for (FreeFlowItem fd : proxies.values()) {

			if (fd.frame.left + itemWidth > viewPortLeft - cellBufferSize
					&& fd.frame.left < viewPortLeft + width + cellBufferSize) {
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
		return true;
	}

	@Override
	public boolean verticalScrollEnabled() {
		return false;
	}

	@Override
	public int getContentWidth() {
		if (itemsAdapter == null)
			return 0;

		int sectionIndex = itemsAdapter.getNumberOfSections() - 1;
		Section s = itemsAdapter.getSection(sectionIndex);

		if (s.getDataCount() == 0)
			return 0;

		Object lastFrameData = s.getDataAtIndex(s.getDataCount() - 1);
		FreeFlowItem fd = proxies.get(lastFrameData);

		return (fd.frame.left + fd.frame.width());
	}

	@Override
	public int getContentHeight() {
		if (itemsAdapter == null)
			return 0;

		return height;
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

}
