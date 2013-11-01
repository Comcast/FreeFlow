package org.freeflow.layouts;

import java.util.ArrayList;

import org.freeflow.core.Frame;
import org.freeflow.core.FrameDescriptor;

import android.util.Log;
import android.util.SparseArray;
import android.widget.BaseAdapter;

public class HGridLayout extends LayoutController {

	private static final String TAG = "HGridLayout";
	private int itemHeight = -1;
	private int itemWidth = -1;
	private int width = -1;
	private int height = -1;
	private BaseAdapter itemsAdapter;
	private ArrayList<FrameDescriptor> frameDescriptors = new ArrayList<FrameDescriptor>();

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
	public void setItems(BaseAdapter adapter) {
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

		frameDescriptors.clear();

		int rows = height / itemHeight;

		for (int i = 0; i < itemsAdapter.getCount(); i++) {
			FrameDescriptor descriptor = new FrameDescriptor();
			Frame frame = new Frame();
			descriptor.itemIndex = i;
			frame.left = (i / rows) * itemWidth;
			frame.top = (i % rows) * itemHeight;
			frame.width = itemWidth;
			frame.height = itemHeight;
			descriptor.frame = frame;
			frameDescriptors.add(descriptor);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SparseArray<FrameDescriptor> getFrameDescriptors(int viewPortLeft, int viewPortTop) {
		SparseArray<FrameDescriptor> desc = new SparseArray<FrameDescriptor>();

		for (int i = 0; i < frameDescriptors.size(); i++) {

			FrameDescriptor fd = frameDescriptors.get(i);

			if (fd.frame.left + itemWidth > viewPortLeft && fd.frame.left < viewPortLeft + width) {
				FrameDescriptor newDesc = new FrameDescriptor();
				newDesc.itemIndex = frameDescriptors.get(i).itemIndex;
				newDesc.frame = Frame.clone(frameDescriptors.get(i).frame);

				desc.append(newDesc.itemIndex, newDesc);
			}
		}

		for (int i = 0; i < desc.size(); i++) {
			desc.get(desc.keyAt(i)).frame.left -= viewPortLeft;
		}

		return desc;
	}

	@Override
	public Frame getViewportFrameForItemIndex(int index) {
		int rows = height / itemHeight;
		Frame frame = new Frame();
		frame.left = (index / rows) * itemWidth;
		frame.top = 0;
		frame.width = width;
		frame.height = height;

		if (itemWidth != -1 && width != -1 && frame.left > (frameDescriptors.size() / rows) * itemWidth - width)
			frame.top = (frameDescriptors.size() / rows) * itemWidth - width;

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

		int rows = height / itemHeight;

		return (itemWidth * (itemsAdapter.getCount() / rows)) - width;
	}

	@Override
	public int getMaximumViewPortY() {
		if (itemsAdapter == null)
			return 0;

		return height;
	}

	@Override
	public Frame getFrameForItemIndexAndViewport(int index, int viewPortLeft, int viewPortTop) {
		Frame frame = Frame.clone(frameDescriptors.get(index).frame);

		frame.left = frame.left - viewPortLeft;
		frame.top = frame.top - viewPortTop;

		return frame;
	}

}
