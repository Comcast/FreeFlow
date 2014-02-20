package com.comcast.freeflow.examples.artbook.layouts;

import java.util.HashMap;

import org.freeflow.core.ItemProxy;
import org.freeflow.core.SectionedAdapter;
import org.freeflow.layouts.AbstractLayout;

public class ArtbookLayout extends AbstractLayout {


	private int largeItemSide;
	private int regularItemSide;
	
	@Override
	public void setDimensions(int measuredWidth, int measuredHeight) {
		largeItemSide = measuredWidth/2;
		regularItemSide = measuredWidth/4;
	}

	@Override
	public void setItems(SectionedAdapter adapter) {
	}

	@Override
	public HashMap<? extends Object, ItemProxy> getItemProxies(
			int viewPortLeft, int viewPortTop) {
		return null;
	}

	@Override
	public ItemProxy getItemProxyForItem(Object item) {
		return null;
	}

	@Override
	public int getContentWidth() {
		return 0;
	}

	@Override
	public int getContentHeight() {
		return 0;
	}

	@Override
	public void setHeaderItemDimensions(int hWidth, int hHeight) {
	}

	@Override
	public ItemProxy getItemAt(float x, float y) {
		return null;
	}
}
