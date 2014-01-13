package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.BaseSectionedAdapter;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;
import org.freeflow.utils.ViewUtils;

import android.graphics.Rect;
import android.util.Log;

public class HLayout extends AbstractLayout {

	private boolean dataChanged = false;
	private static final String TAG = "HLayout";
	private int itemWidth = -1;
	protected int width = -1;
	protected int height = -1;
	private BaseSectionedAdapter itemsAdapter;
	private HashMap<Object, ItemProxy> frameDescriptors = new HashMap<Object, ItemProxy>();
	private int headerHeight = -1;
	private int headerWidth = -1;

	private int cellBufferSize = 0;
	private int bufferCount = 1;

	public void setItemWidth(int i) {
		this.itemWidth = i;
		cellBufferSize = bufferCount * itemWidth;
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
		dataChanged = true;
	}

	@Override
	public void setItems(BaseSectionedAdapter adapter) {
		if(adapter == this.itemsAdapter)
			return;
		
		Log.d(TAG, "setItems called");
		
		this.itemsAdapter = adapter;
		dataChanged = true;
	}

	/**
	 * TODO: Future optimization: can we avoid object allocation here?
	 */
	public void generateItemProxies() {
		if (itemWidth < 0) {
			throw new IllegalStateException("itemWidth not set");
		}

		if (height < 0 || width < 0) {
			throw new IllegalStateException("dimensions not set");
		}

		dataChanged = false;

		frameDescriptors.clear();
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

				ItemProxy header = new ItemProxy();
				Rect hframe = new Rect();
				header.itemSection = i;
				header.itemIndex = -1;
				header.isHeader = true;
				hframe.left = leftStart;
				hframe.top = 0;
				hframe.right = leftStart + headerWidth;
				hframe.bottom = headerHeight;
				header.frame = hframe;
				header.data = s.getSectionTitle();
				frameDescriptors.put(header.data, header);

				leftStart += headerWidth;
			}

			for (int j = 0; j < s.getDataCount(); j++) {
				ItemProxy descriptor = new ItemProxy();
				Rect frame = new Rect();
				descriptor.itemSection = i;
				descriptor.itemIndex = j;
				frame.left = j * itemWidth + leftStart;
				frame.top = 0;
				frame.right = frame.left + itemWidth;
				frame.bottom = height;
				descriptor.frame = frame;
				descriptor.data = s.getData().get(j);
				frameDescriptors.put(descriptor.data, descriptor);
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
	public HashMap<? extends Object, ItemProxy> getItemProxies(int viewPortLeft, int viewPortTop) {
		HashMap<Object, ItemProxy> desc = new HashMap<Object, ItemProxy>();

		if (frameDescriptors.size() == 0 || dataChanged) {
			generateItemProxies();
		}

		for (ItemProxy fd : frameDescriptors.values()) {

			if (fd.frame.left + itemWidth > viewPortLeft - cellBufferSize
					&& fd.frame.left < viewPortLeft + width + cellBufferSize) {
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
	public boolean horizontalDragEnabled() {
		return true;
	}

	@Override
	public boolean verticalDragEnabled() {
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

		Object lastFrameData = s.getData().get(s.getDataCount() - 1);
		ItemProxy fd = frameDescriptors.get(lastFrameData);

		return (fd.frame.left + fd.frame.width()) - width;
	}

	@Override
	public int getContentHeight() {
		return 0;
	}

	@Override
	public ItemProxy getItemProxyForItem(Object data) {
		if (frameDescriptors.size() == 0 || dataChanged) {
			generateItemProxies();
		}

		ItemProxy fd = ItemProxy.clone(frameDescriptors.get(data));
		return fd;
	}

	@Override
	public void setHeaderItemDimensions(int hWidth, int hHeight) {
		if (hWidth == headerWidth && hHeight == headerHeight)
			return;

		headerHeight = hHeight;
		headerWidth = hWidth;
		dataChanged = true;

	}

	public void setBufferCount(int bufferCount) {
		this.bufferCount = bufferCount;
	}

}
