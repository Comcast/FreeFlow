package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.BaseSectionedAdapter;
import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;

public class VGridLayout extends AbstractLayout {

	private boolean dataChanged = false;
	private static final String TAG = "VGridLayout";
	private int itemHeight = -1;
	private int itemWidth = -1;
	private int headerWidth = -1;
	private int headerHeight = -1;
	private int width = -1;
	private int height = -1;
	private BaseSectionedAdapter itemsAdapter;
	private HashMap<Object, ItemProxy> frameDescriptors = new HashMap<Object, ItemProxy>();

	public void setItemHeight(int itemHeight) {
		if (itemHeight == this.itemHeight)
			return;

		this.itemHeight = itemHeight;
		dataChanged = true;
	}

	public void setItemWidth(int itemWidth) {
		if (itemWidth == this.itemWidth)
			return;

		this.itemWidth = itemWidth;
		dataChanged = true;
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

		if (itemWidth < 0) {
			throw new IllegalStateException("itemWidth not set");
		}

		if (headerWidth < 0) {
			throw new IllegalStateException("headerWidth not set");
		}

		if (headerHeight < 0) {
			throw new IllegalStateException("headerHeight not set");
		}

		if (height < 0 || width < 0)
			throw new IllegalStateException("dimensions not set");

		dataChanged = false;

		frameDescriptors.clear();

		int cols = width / itemWidth;

		int topStart = 0;

		for (int i = 0; i < itemsAdapter.getNumberOfSections(); i++) {

			Section s = itemsAdapter.getSection(i);

			if (s.shouldDisplayHeader()) {
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
				frame.left = (j % cols) * itemWidth;
				frame.top = (j / cols) * itemHeight + topStart;
				frame.width = itemWidth;
				frame.height = itemHeight;
				descriptor.frame = frame;
				descriptor.data = s.getData().get(j);
				frameDescriptors.put(descriptor.data, descriptor);
			}
			int mod = 0;
			if (s.getDataCount() % cols != 0)
				mod = 1;

			topStart += ((s.getDataCount() / cols) + mod) * itemHeight;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashMap<? extends Object, ItemProxy> getItemProxies(int viewPortLeft, int viewPortTop) {
		HashMap<Object, ItemProxy> desc = new HashMap<Object, ItemProxy>();

		if(frameDescriptors.size() == 0 || dataChanged) {
			generateItemProxies();
		}
		
		for (ItemProxy fd : frameDescriptors.values()) {
			if (fd.frame.top + itemHeight > viewPortTop && fd.frame.top < viewPortTop + height) {
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
		frame.width = itemWidth;
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
	public int getMinimumViewPortX() {
		return 0;
	}

	@Override
	public int getMinimumViewPortY() {
		return 0;
	}

	@Override
	public int getMaximumViewPortX() {
		return 0;
	}

	@Override
	public int getMaximumViewPortY() {
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
		if(frameDescriptors.size() == 0 || dataChanged) {
			generateItemProxies();
		}
		
		if (frameDescriptors.get(data) == null)
			return null;

		ItemProxy fd = ItemProxy.clone(frameDescriptors.get(data));
		return fd;
	}

	@Override
	public void setHeaderItemDimensions(int hWidth, int hHeight) {
		if (hWidth == headerWidth && headerHeight == hHeight)
			return;

		dataChanged = true;
		headerWidth = hWidth;
		headerHeight = hHeight;
	}

}
