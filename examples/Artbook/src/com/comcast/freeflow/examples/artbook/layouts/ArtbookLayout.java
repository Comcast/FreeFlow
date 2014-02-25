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
package com.comcast.freeflow.examples.artbook.layouts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.graphics.Rect;
import android.util.Log;

import com.comcast.freeflow.core.ItemProxy;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.FreeFlowLayout.FreeFlowLayoutParams;
import com.comcast.freeflow.utils.ViewUtils;

public class ArtbookLayout implements FreeFlowLayout {

	private static final String TAG = "ArtbookLayout";

	private int largeItemSide;
	private int regularItemSide;

	private int viewPortWidth;
	private int viewPortHeight;

	@Override
	public void setDimensions(int measuredWidth, int measuredHeight) {
		largeItemSide = measuredWidth / 2;
		regularItemSide = measuredWidth / 4;

		viewPortWidth = measuredWidth;
		viewPortHeight = measuredHeight;
	}

	private HashMap<Object, ItemProxy> map;
	private Section s;

	@Override
	public void setAdapter(SectionedAdapter adapter) {

		// assuming one section
		map = new HashMap<Object, ItemProxy>();
		s = adapter.getSection(0);

		int rowIndex;

		for (int i = 0; i < s.getDataCount(); i++) {
			rowIndex = i / 5;

			ItemProxy p = new ItemProxy();
			p.isHeader = false;
			p.itemIndex = i;
			p.itemSection = 0;
			p.data = s.getDataAtIndex(i);

			Rect r = new Rect();

			switch (i % 5) {
			case (0):
				r.left = 0;
				r.top = rowIndex * largeItemSide;
				r.right = largeItemSide;
				r.bottom = r.top + largeItemSide;
				
				if(rowIndex % 2 != 0){
					r.offset(largeItemSide, 0);
				}
				
				
				break;

			case (1):
				r.left = largeItemSide;
				r.right = largeItemSide + regularItemSide;
				r.top = rowIndex * largeItemSide;
				r.bottom = r.top + regularItemSide;
				
				if(rowIndex % 2 != 0){
					r.offset(-largeItemSide, 0);
				}
				
				break;

			case (2):
				r.left = 3 * regularItemSide;
				r.right = viewPortWidth;
				r.top = rowIndex * largeItemSide;
				r.bottom = r.top + regularItemSide;
				
				if(rowIndex % 2 != 0){
					r.offset(-largeItemSide, 0);
				}
				
				break;

			case (3):
				r.left = largeItemSide;
				r.right = largeItemSide + regularItemSide;
				r.top = rowIndex * largeItemSide + regularItemSide;
				r.bottom = r.top + regularItemSide;
				if(rowIndex % 2 != 0){
					r.offset(-largeItemSide, 0);
				}
				break;

			case (4):
				r.left = 3 * regularItemSide;
				r.right = viewPortWidth;
				r.top = rowIndex * largeItemSide + regularItemSide;
				r.bottom = r.top + regularItemSide;
				if(rowIndex % 2 != 0){
					r.offset(-largeItemSide, 0);
				}
				break;

			default:
				break;
			}
			p.frame = r;
			map.put(s.getDataAtIndex(i), p);
		}
	}

	@Override
	public HashMap<? extends Object, ItemProxy> getItemProxies(
			int viewPortLeft, int viewPortTop) {

		Rect viewport = new Rect(viewPortLeft, 
								viewPortTop, 
								viewPortLeft + viewPortWidth, 
								viewPortTop + viewPortHeight);
		HashMap<Object, ItemProxy> ret = new HashMap<Object, ItemProxy>();

		Iterator<Entry<Object, ItemProxy>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, ItemProxy> pairs = it.next();
			ItemProxy p = (ItemProxy) pairs.getValue();
			if ( Rect.intersects(p.frame, viewport) ) {
				ret.put(pairs.getKey(), p);
			}
		}
		return ret;
		
	}

	@Override
	public ItemProxy getItemProxyForItem(Object item) {
		Log.d(TAG, " returing item: " + map.get(item));
		return map.get(item);
	}

	@Override
	public int getContentWidth() {
		return 0;
	}

	@Override
	public int getContentHeight() {
		return s.getDataCount() / 5 * largeItemSide;
	}

	@Override
	public ItemProxy getItemAt(float x, float y) {
		return (ItemProxy) ViewUtils.getItemAt(map, (int) x, (int) y);
	}

	@Override
	public void setLayoutParams(FreeFlowLayoutParams params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean verticalScrollEnabled() {
		return true;
	}
	
	@Override
	public boolean horizontalScrollEnabled(){
		return false;
	}
}
