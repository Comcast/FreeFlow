package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.BaseSectionedAdapter;
import org.freeflow.core.Frame;
import org.freeflow.core.FrameDescriptor;
import org.freeflow.core.LayoutController;
import org.freeflow.core.Section;

public class HLayout extends LayoutController {

	private boolean dataChanged = false;
	private static final String TAG = "HLayout";
	private int itemWidth = -1;
	protected int width = -1;
	protected int height = -1;
	private BaseSectionedAdapter itemsAdapter;
	private HashMap<Object, FrameDescriptor> frameDescriptors = new HashMap<Object, FrameDescriptor>();
	private int headerHeight = -1;
	private int headerWidth = -1;

	public void setItemWidth(int i) {
		this.itemWidth = i;
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
	@Override
	protected void generateFrameDescriptors() {
		if (itemWidth < 0) {
			throw new IllegalStateException("itemWidth not set");
		}

		if (height < 0 || width < 0) {
			throw new IllegalStateException("dimensions not set");
		}

		if (headerWidth < 0) {
			throw new IllegalStateException("headerWidth not set");
		}

		if (headerHeight < 0) {
			throw new IllegalStateException("headerHeight not set");
		}

		dataChanged = false;

		frameDescriptors.clear();
		int leftStart = 0;

		for (int i = 0; i < itemsAdapter.getNumberOfSections(); i++) {
			Section s = itemsAdapter.getSection(i);

			if (s.shouldDisplayHeader()) {
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
				header.data = s.getSectionTitle();
				frameDescriptors.put(header.data, header);

				leftStart += headerWidth;
			}

			for (int j = 0; j < s.getDataCount(); j++) {
				FrameDescriptor descriptor = new FrameDescriptor();
				Frame frame = new Frame();
				descriptor.itemSection = i;
				descriptor.itemIndex = j;
				frame.left = j * itemWidth + leftStart;
				frame.top = 0;
				frame.width = itemWidth;
				frame.height = height;
				descriptor.frame = frame;
				descriptor.data = s.getData().get(j);
				frameDescriptors.put(descriptor.data, descriptor);
			}

			leftStart += s.getDataCount() * itemWidth;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashMap<? extends Object, FrameDescriptor> getFrameDescriptors(int viewPortLeft, int viewPortTop) {
		HashMap<Object, FrameDescriptor> desc = new HashMap<Object, FrameDescriptor>();

		if(frameDescriptors.size() == 0 || dataChanged) {
			generateFrameDescriptors();
		}
		
		for (FrameDescriptor fd : frameDescriptors.values()) {

			if (fd.frame.left + itemWidth > viewPortLeft && fd.frame.left < viewPortLeft + width) {
				FrameDescriptor newDesc = FrameDescriptor.clone(fd);
				desc.put(newDesc.data, newDesc);
			}
		}

		return desc;
	}

	@Override
	public Frame getOffScreenStartFrame() {
		Frame frame = new Frame();
		frame.height = height;
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

		int sectionIndex = itemsAdapter.getNumberOfSections() - 1;
		Section s = itemsAdapter.getSection(sectionIndex);

		if (s.getDataCount() == 0)
			return 0;

		Object lastFrameData = s.getData().get(s.getDataCount() - 1);
		FrameDescriptor fd = frameDescriptors.get(lastFrameData);

		return (fd.frame.left + fd.frame.width) - width;
	}

	@Override
	public int getMaximumViewPortY() {
		return 0;
	}

	@Override
	public FrameDescriptor getFrameDescriptorForItem(Object data) {
		if(frameDescriptors.size() == 0 || dataChanged) {
			generateFrameDescriptors();
		}
		
		FrameDescriptor fd = FrameDescriptor.clone(frameDescriptors.get(data));
		return fd;
	}

	@Override
	public void setHeaderItemDimensions(int hWidth, int hHeight) {
		if(hWidth == headerWidth && hHeight == headerHeight)
			return;
		
		headerHeight = hHeight;
		headerWidth = hWidth;
		dataChanged = true;
		
		
	}

}
