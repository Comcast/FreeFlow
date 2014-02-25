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

import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.FreeFlowLayout.FreeFlowLayoutParams;
import com.comcast.freeflow.layouts.FreeFlowLayoutBase;
import com.comcast.freeflow.utils.ViewUtils;

public class ArtbookLayout extends FreeFlowLayoutBase implements FreeFlowLayout {

	private static final String TAG = "ArtbookLayout";

	private int largeItemSide;
	private int regularItemSide;


	@Override
	public void setDimensions(int measuredWidth, int measuredHeight) {
		super.setDimensions(measuredWidth, measuredHeight);
		largeItemSide = measuredWidth / 2;
		regularItemSide = measuredWidth / 4;

	}

	private HashMap<Object, FreeFlowItem> map;
	private Section s;
	
	@Override
	public void prepareLayout(){
		map = new HashMap<Object, FreeFlowItem>();
		s = itemsAdapter.getSection(0);
		int rowIndex;

		for (int i = 0; i < s.getDataCount(); i++) {
			rowIndex = i / 5;

			FreeFlowItem p = new FreeFlowItem();
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
				r.right = width;
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
				r.right = width;
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
	public HashMap<? extends Object, FreeFlowItem> getItemProxies(
			int viewPortLeft, int viewPortTop) {

		Rect viewport = new Rect(viewPortLeft, 
								viewPortTop, 
								viewPortLeft + width, 
								viewPortTop + height);
		HashMap<Object, FreeFlowItem> ret = new HashMap<Object, FreeFlowItem>();

		Iterator<Entry<Object, FreeFlowItem>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, FreeFlowItem> pairs = it.next();
			FreeFlowItem p = (FreeFlowItem) pairs.getValue();
			if ( Rect.intersects(p.frame, viewport) ) {
				ret.put(pairs.getKey(), p);
			}
		}
		return ret;
		
	}

	@Override
	public FreeFlowItem getFreeFlowItemForItem(Object item) {
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
	public FreeFlowItem getItemAt(float x, float y) {
		return (FreeFlowItem) ViewUtils.getItemAt(map, (int) x, (int) y);
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
