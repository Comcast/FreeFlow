package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.SectionedAdapter;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;
import org.freeflow.utils.ViewUtils;

import android.graphics.Rect;

public class VGridLayout extends AbstractLayout {

	private boolean layoutChanged = false;
	private static final String TAG = "VGridLayout";
	protected int itemHeight = -1;
	protected int itemWidth = -1;
	protected int headerWidth = -1;
	protected int headerHeight = -1;
	protected int width = -1;
	protected int height = -1;
	protected SectionedAdapter itemsAdapter;
	protected HashMap<Object, ItemProxy> frameDescriptors = new HashMap<Object, ItemProxy>();

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
		layoutChanged = true;
	}

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
	public void setAdapter(SectionedAdapter adapter) {
		this.itemsAdapter = adapter;
		layoutChanged = true;
	}

	public void generateItemProxies() {
		
		layoutChanged = false;

		frameDescriptors.clear();

		int cols = width / itemWidth;

		int topStart = 0;
		if(itemsAdapter == null) return;

		for (int i = 0; i < itemsAdapter.getNumberOfSections(); i++) {

			Section s = itemsAdapter.getSection(i);

			if (itemsAdapter.shouldDisplaySectionHeaders()) {

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
				frame.left = (j % cols) * itemWidth;
				frame.top = (j / cols) * itemHeight + topStart;
				frame.right = frame.left + itemWidth;
				frame.bottom = frame.top + itemHeight;
				
				frame.inset(itemFrameInsetX, itemFrameInsetY);
				
				descriptor.frame = frame;
				descriptor.data = s.getDataAtIndex(j);
				frameDescriptors.put(descriptor.data, descriptor);
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
	 * cellBufferSize to each end of the viewport. <br>
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public HashMap<? extends Object, ItemProxy> getItemProxies(int viewPortLeft, int viewPortTop) {
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
	public ItemProxy getItemAt(float x, float y){
		return ViewUtils.getItemAt(frameDescriptors, (int)x, (int)y);
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

		if (frameDescriptors.get(data) == null)
			return null;

		ItemProxy fd = ItemProxy.clone(frameDescriptors.get(data));
		return fd;
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
