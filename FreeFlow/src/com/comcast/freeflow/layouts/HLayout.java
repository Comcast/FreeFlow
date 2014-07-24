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

public class HLayout extends FreeFlowLayoutBase implements FreeFlowLayout {

	private static final String TAG = "HLayout";
	protected int itemWidth = -1;
	protected Map<Object, FreeFlowItem> proxies = new LinkedHashMap<Object, FreeFlowItem>();
	protected int headerHeight = -1;
	protected int headerWidth = -1;

	protected int cellBufferSize = 0;
	protected int bufferCount = 1;
	
	protected FreeFlowLayoutParams layoutParams;
	
	@Override
	public void setLayoutParams(FreeFlowLayoutParams params){
		
		if(params.equals(this.layoutParams)){
			return;
		}
		
		LayoutParams lp = (LayoutParams)params;
		this.itemWidth = lp.itemWidth;
		this.headerWidth = lp.headerWidth;
		this.headerHeight = lp.headerHeight;
		cellBufferSize = bufferCount * cellBufferSize;
		
	}

	public void prepareLayout() {
		if (itemWidth < 0) {
			throw new IllegalStateException("itemWidth not set");
		}

		proxies.clear();
		int leftStart = 0;

		for (int i = 0; i < itemsAdapter.getNumberOfSections(); i++) {
			Section s = itemsAdapter.getSection(i);

			if (itemsAdapter.shouldDisplaySectionHeaders()) {

				if (headerWidth < 0) {
					throw new IllegalStateException("headerWidth not set");
				}

				if (headerHeight < 0) {
					throw new IllegalStateException("headerHeight not set");
				}

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
				frame.left = j * itemWidth + leftStart;
				frame.top = 0;
				frame.right = frame.left + itemWidth;
				frame.bottom = height;
				descriptor.frame = frame;
				descriptor.data = s.getDataAtIndex(j);
				proxies.put(descriptor.data, descriptor);
			}

			leftStart += s.getDataCount() * itemWidth;
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
	public Map<Object, FreeFlowItem> getItemProxies(int viewPortLeft, int viewPortTop) {
		LinkedHashMap<Object, FreeFlowItem> desc = new LinkedHashMap<Object, FreeFlowItem>();

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
		if (itemsAdapter == null || itemsAdapter.getNumberOfSections() <= 0){
			return 0;
		}

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
		public int headerWidth = 0;
		public int headerHeight = 0;
		
		public LayoutParams(int itemWidth){
			this.itemWidth = itemWidth;
		}
		
		public LayoutParams(int itemWidth, int headerWidth, int headerHeight){
			this.itemWidth = itemWidth;
			this.headerWidth = headerWidth;
			this.headerHeight = headerHeight;
		}
	}

}
