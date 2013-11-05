package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.BaseSectionedAdapter;
import org.freeflow.core.Frame;
import org.freeflow.core.FrameDescriptor;
import org.freeflow.core.LayoutController;
import org.freeflow.core.Section;

public class HGridLayout extends LayoutController {

	private static final String TAG = "HGridLayout";
	private int itemHeight = -1;
	private int itemWidth = -1;
	private int width = -1;
	private int height = -1;
	private BaseSectionedAdapter itemsAdapter;
	private HashMap<Object, FrameDescriptor> frameDescriptors = new HashMap<Object, FrameDescriptor>();
	private int headerWidth = -1;
	private int headerHeight = -1;

	public void setItemHeight(int i) {
		this.itemHeight = i;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
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
		if (itemsAdapter != null) {
			generateFrameDescriptors();
		}
	}

	@Override
	public void setItems(BaseSectionedAdapter adapter) {
		this.itemsAdapter = adapter;
		if (width != -1 && height != -1 && itemWidth != -1 && itemHeight != -1) {
			generateFrameDescriptors();
		}
	}

	/**
	 * TODO: Future optimization: can we avoid object allocation here?
	 */
	@Override
	public void generateFrameDescriptors() {
		if (itemHeight < 0) {
			throw new IllegalStateException("itemHeight not set");
		}

		if (itemWidth < 0) {
			throw new IllegalStateException("itemWidth not set");
		}

		if (height < 0 || width < 0)
			throw new IllegalStateException("dimensions not set");

		if (headerWidth < 0) {
			throw new IllegalStateException("headerWidth not set");
		}

		if (headerHeight < 0) {
			throw new IllegalStateException("headerHeight not set");
		}

		frameDescriptors.clear();

		int rows = height / itemHeight;
		int leftStart = 0;

		for (int i = 0; i < itemsAdapter.getSectionCount(); i++) {

			FrameDescriptor header = new FrameDescriptor();
			Frame hframe = new Frame();
			header.itemSection = i;
			header.itemIndex = -1;
			header.isHeader = true;
			hframe.left = leftStart;
			hframe.top = 0;
			hframe.width = headerWidth;
			hframe.height = headerHeight;
			header.frame = hframe;
			header.data = itemsAdapter.getSection(i).getSectionTitle();
			frameDescriptors.put(header.data, header);

			leftStart += headerWidth;

			for (int j = 0; j < itemsAdapter.getSectionCount(); j++) {
				FrameDescriptor descriptor = new FrameDescriptor();
				Frame frame = new Frame();
				descriptor.itemSection = i;
				descriptor.itemIndex = j;
				frame.left = (j / rows) * itemWidth + leftStart;
				frame.top = (j % rows) * itemHeight;
				frame.width = itemWidth;
				frame.height = itemHeight;
				descriptor.frame = frame;
				descriptor.data = itemsAdapter.getItem(i, j);
				frameDescriptors.put(descriptor.data, descriptor);
			}
			int mod = 0;
			if (itemsAdapter.getCountForSection(i) % rows != 0)
				mod = 1;
			leftStart += ((itemsAdapter.getCountForSection(i) / rows) + mod) * itemWidth;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashMap<Object, FrameDescriptor> getFrameDescriptors(int viewPortLeft, int viewPortTop) {
		HashMap<Object, FrameDescriptor> desc = new HashMap<Object, FrameDescriptor>();

		Object[] keyset = frameDescriptors.keySet().toArray();
		for (int i = 0; i < frameDescriptors.size(); i++) {

			FrameDescriptor fd = frameDescriptors.get(keyset[i]);

			if (fd.frame.left + itemWidth > viewPortLeft && fd.frame.left < viewPortLeft + width) {
				FrameDescriptor newDesc = FrameDescriptor.clone(fd);
				newDesc.frame.left -= viewPortLeft;
				desc.put(newDesc.data, newDesc);
			}
		}

		return desc;
	}

	@Override
	public Frame getViewportFrameForItem(Object item) {
		FrameDescriptor fd = frameDescriptors.get(item);

		Frame frame = new Frame();
		frame.left = fd.frame.left;
		frame.top = 0;
		frame.width = width;
		frame.height = height;

		if (itemWidth != -1 && width != -1 && frame.left > getMaximumViewPortX())
			frame.left = getMaximumViewPortX();

		return frame;
	}

	@Override
	public Frame getOffScreenStartFrame() {
		Frame frame = new Frame();
		frame.height = itemHeight;
		frame.width = itemWidth;
		frame.left = width;
		frame.top = 0;

		return frame;
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
	public int getMinimumViewPortX() {
		return 0;
	}

	@Override
	public int getMinimumViewPortY() {
		return 0;
	}

	@Override
	public int getMaximumViewPortX() {
		if (itemsAdapter == null)
			return 0;

		int sectionIndex = itemsAdapter.getSectionCount() - 1;
		Section s = itemsAdapter.getSection(sectionIndex);

		Object lastFrameData = itemsAdapter.getItem(sectionIndex, s.getDataCount() - 1);
		FrameDescriptor fd = frameDescriptors.get(lastFrameData);

		return (fd.frame.left + fd.frame.width) - width;
	}

	@Override
	public int getMaximumViewPortY() {
		if (itemsAdapter == null)
			return 0;

		return height;
	}

	@Override
	public FrameDescriptor getFrameDescriptorForItemAndViewport(Object data, int viewPortLeft, int viewPortTop) {
		FrameDescriptor fd = FrameDescriptor.clone(frameDescriptors.get(data));

		fd.frame.left -= viewPortLeft;
		fd.frame.top -= viewPortTop;

		return fd;
	}

	@Override
	public void setHeaderItemDimensions(int hWidth, int hHeight) {
		headerWidth = hWidth;
		headerHeight = hHeight;
	}

}
