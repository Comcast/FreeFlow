package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.BaseSectionedAdapter;
import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;
import org.freeflow.utils.ViewUtils;

public class VLayout extends AbstractLayout {

	private boolean dataChanged = false;
	private static final String TAG = "VLayout";
	private int itemHeight = -1;
	private int width = -1;
	private int height = -1;
	private BaseSectionedAdapter itemsAdapter;
	private HashMap<Object, ItemProxy> frameDescriptors = new HashMap<Object, ItemProxy>();
	private int headerHeight = -1;
	private int headerWidth = -1;

	private int cellBufferSize = 0;
	private int bufferCount = 1;

	public void setItemHeight(int i) {
		this.itemHeight = i;
		cellBufferSize = bufferCount * itemHeight;
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
		this.itemsAdapter = adapter;

		dataChanged = true;
	}

	/**
	 * TODO: Future optimization: can we avoid object allocation here?
	 */
	public void generateItemProxies() {
		if (itemHeight < 0) {
			throw new IllegalStateException("itemHeight not set");
		}

		if (height < 0 || width < 0) {
			throw new IllegalStateException("dimensions not set");
		}

		dataChanged = false;

		frameDescriptors.clear();
		int topStart = 0;

		for (int i = 0; i < itemsAdapter.getNumberOfSections(); i++) {

			Section s = itemsAdapter.getSection(i);

			if (itemsAdapter.shouldDisplaySectionHeaders()	) {

				if (headerWidth < 0) {
					throw new IllegalStateException("headerWidth not set");
				}

				if (headerHeight < 0) {
					throw new IllegalStateException("headerHeight not set");
				}
				
				ItemProxy header = new ItemProxy();
				Frame hframe = new Frame();
				header.itemSection = i;
				header.itemIndex = -1;
				header.isHeader = true;
				hframe.left = 0;
				hframe.top = topStart;
				hframe.width = headerWidth;
				hframe.height = headerHeight;
				header.frame = hframe;
				header.data = s.getSectionTitle();
				frameDescriptors.put(header.data, header);

				topStart += headerHeight;
			}

			for (int j = 0; j < s.getDataCount(); j++) {
				ItemProxy descriptor = new ItemProxy();
				Frame frame = new Frame();
				descriptor.itemSection = i;
				descriptor.itemIndex = j;
				frame.left = 0;
				frame.top = j * itemHeight + topStart;
				frame.width = width;
				frame.height = itemHeight;
				descriptor.frame = frame;
				descriptor.data = s.getData().get(j);
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
	public HashMap<? extends Object, ItemProxy> getItemProxies(int viewPortLeft, int viewPortTop) {
		HashMap<Object, ItemProxy> desc = new HashMap<Object, ItemProxy>();

		if (frameDescriptors.size() == 0 || dataChanged) {
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
	public Frame getOffScreenStartFrame() {
		Frame frame = new Frame();
		frame.height = itemHeight;
		frame.width = width;
		frame.left = 0;
		frame.top = height;

		return frame;
	}

	@Override
	public boolean horizontalDragEnabled() {
		return false;
	}

	@Override
	public boolean verticalDragEnabled() {
		return true;
	}

	@Override
	public int getContentWidth() {
		return 0;
	}

	@Override
	public int getContentHeight() {
		if (itemsAdapter == null)
			return 0;

		int sectionIndex = itemsAdapter.getNumberOfSections() - 1;
		Section s = itemsAdapter.getSection(sectionIndex);

		if (s.getDataCount() == 0)
			return 0;

		Object lastFrameData = s.getData().get(s.getDataCount() - 1);
		ItemProxy fd = frameDescriptors.get(lastFrameData);

		return (fd.frame.top + fd.frame.height) - height;
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
	public ItemProxy getItemAt(float x, float y){
		return ViewUtils.getItemAt(frameDescriptors, (int)x, (int)y);
	}

	@Override
	public void setHeaderItemDimensions(int hWidth, int hHeight) {
		headerWidth = hWidth;
		headerHeight = hHeight;
	}

	public void setBufferCount(int bufferCount) {
		this.bufferCount = bufferCount;
	}

}
