package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;
import org.freeflow.core.SectionedAdapter;
import org.freeflow.layouts.AbstractLayout.FreeFlowLayoutParams;
import org.freeflow.layouts.HLayout.LayoutParams;
import org.freeflow.utils.ViewUtils;

import android.graphics.Rect;

public class VLayout extends AbstractLayout {

	private boolean layoutChanged = false;
	private static final String TAG = "VLayout";
	private int itemHeight = -1;
	private int width = -1;
	private int height = -1;
	private SectionedAdapter itemsAdapter;
	private HashMap<Object, ItemProxy> frameDescriptors = new HashMap<Object, ItemProxy>();
	private int headerHeight = -1;
	private int headerWidth = -1;

	private int cellBufferSize = 0;
	private int bufferCount = 1;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDimensions(int measuredWidth, int measuredHeight) {
		if (measuredHeight == height && measuredWidth == width) {
			return;
		}
		this.width = measuredWidth;
		this.height = measuredHeight;
		layoutChanged = true;
	}

	@Override
	public void setLayoutParams(FreeFlowLayoutParams params) {
		if (params.equals(this.layoutParams)) {
			return;
		}
		LayoutParams lp = (LayoutParams) params;
		this.itemHeight = lp.itemHeight;
		this.headerWidth = lp.headerWidth;
		this.headerHeight = lp.headerHeight;
		cellBufferSize = bufferCount * cellBufferSize;
		layoutChanged = true;
	}

	@Override
	public void setAdapter(SectionedAdapter adapter) {
		this.itemsAdapter = adapter;

		layoutChanged = true;
	}

	public void generateItemProxies() {
		if (itemHeight < 0) {
			throw new IllegalStateException("itemHeight not set");
		}

		if (height < 0 || width < 0) {
			throw new IllegalStateException("dimensions not set");
		}

		layoutChanged = false;

		frameDescriptors.clear();
		int topStart = 0;

		for (int i = 0; i < itemsAdapter.getNumberOfSections(); i++) {

			Section s = itemsAdapter.getSection(i);

			if (itemsAdapter.shouldDisplaySectionHeaders()) {

				if (headerWidth < 0) {
					throw new IllegalStateException("headerWidth not set");
				}

				if (headerHeight < 0) {
					throw new IllegalStateException("headerHeight not set");
				}

				ItemProxy header = new ItemProxy();
				Rect hframe = new Rect();
				header.itemSection = i;
				header.itemIndex = -1;
				header.isHeader = true;
				hframe.left = 0;
				hframe.top = topStart;
				hframe.right = headerWidth;
				hframe.bottom = topStart + headerHeight;
				header.frame = hframe;
				header.data = s.getSectionTitle();
				frameDescriptors.put(header.data, header);

				topStart += headerHeight;
			}

			for (int j = 0; j < s.getDataCount(); j++) {
				ItemProxy descriptor = new ItemProxy();
				Rect frame = new Rect();
				descriptor.itemSection = i;
				descriptor.itemIndex = j;
				frame.left = 0;
				frame.top = j * itemHeight + topStart;
				frame.right = width;
				frame.bottom = frame.top + itemHeight;
				descriptor.frame = frame;
				descriptor.data = s.getDataAtIndex(j);
				frameDescriptors.put(descriptor.data, descriptor);
			}

			topStart += (s.getDataCount()) * itemHeight;
		}

	}

	/**
	 * NOTE: In this instance, we subtract/add the cellBufferSize (computed when
	 * item height is set, defaulted to 1 cell) to add a buffer of
	 * cellBufferSize to each end of the viewport. <br>
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public HashMap<? extends Object, ItemProxy> getItemProxies(
			int viewPortLeft, int viewPortTop) {
		HashMap<Object, ItemProxy> desc = new HashMap<Object, ItemProxy>();

		if (frameDescriptors.size() == 0 || layoutChanged) {
			generateItemProxies();
		}

		for (ItemProxy fd : frameDescriptors.values()) {
			if (fd.frame.top + itemHeight > viewPortTop - cellBufferSize
					&& fd.frame.top < viewPortTop + height + cellBufferSize) {
				ItemProxy newDesc = ItemProxy.clone(fd);
				desc.put(newDesc.data, newDesc);
			}
		}

		return desc;
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
		if (itemsAdapter == null)
			return 0;

		int sectionIndex = itemsAdapter.getNumberOfSections() - 1;
		Section s = itemsAdapter.getSection(sectionIndex);

		if (s.getDataCount() == 0)
			return 0;

		Object lastFrameData = s.getDataAtIndex(s.getDataCount() - 1);
		ItemProxy fd = frameDescriptors.get(lastFrameData);

		return (fd.frame.top + fd.frame.height());
	}

	@Override
	public ItemProxy getItemProxyForItem(Object data) {
		if (frameDescriptors.size() == 0 || layoutChanged) {
			generateItemProxies();
		}

		ItemProxy fd = ItemProxy.clone(frameDescriptors.get(data));

		return fd;
	}

	@Override
	public ItemProxy getItemAt(float x, float y) {
		return ViewUtils.getItemAt(frameDescriptors, (int) x, (int) y);
	}

	public void setHeaderItemDimensions(int hWidth, int hHeight) {
		headerWidth = hWidth;
		headerHeight = hHeight;
	}

	public void setBufferCount(int bufferCount) {
		this.bufferCount = bufferCount;
	}

	public static class LayoutParams extends FreeFlowLayoutParams {
		public int itemHeight = 0;
		public int headerWidth = 0;
		public int headerHeight = 0;

		public LayoutParams(int itemHeight) {
			this.itemHeight = itemHeight;
		}

		public LayoutParams(int itemHeight, int headerWidth, int headerHeight) {
			this.itemHeight = itemHeight;
			this.headerWidth = headerWidth;
			this.headerHeight = headerHeight;
		}
	}

}
