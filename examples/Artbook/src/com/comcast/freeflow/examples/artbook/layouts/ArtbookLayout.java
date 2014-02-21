package com.comcast.freeflow.examples.artbook.layouts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;
import org.freeflow.core.SectionedAdapter;
import org.freeflow.layouts.AbstractLayout;
import org.freeflow.utils.ViewUtils;

import android.graphics.Rect;
import android.util.Log;

public class ArtbookLayout extends AbstractLayout {

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
	public void setItems(SectionedAdapter adapter) {

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
				ret.put(pairs.getKey(), ItemProxy.clone(p));
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
	public boolean horizontalDragEnabled(){
		return false;
	}
	

	@Override
	public int getContentHeight() {
		return s.getDataCount() / 5 * largeItemSide;
	}

	@Override
	public void setHeaderItemDimensions(int hWidth, int hHeight) {
	}

	@Override
	public ItemProxy getItemAt(float x, float y) {
		return (ItemProxy) ViewUtils.getItemAt(map, (int) x, (int) y);
	}
}
