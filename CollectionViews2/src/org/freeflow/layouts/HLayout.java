package org.freeflow.layouts;

import java.util.ArrayList;

import org.freeflow.core.Frame;
import org.freeflow.core.FrameDescriptor;

import android.util.Log;
import android.util.SparseArray;
import android.widget.BaseAdapter;

public class HLayout extends LayoutController {

	private static final String TAG = "HLayout";
	private int itemWidth = -1;
	private int width = -1;
	private int height = -1;
	private BaseAdapter itemsAdapter;
	private ArrayList<FrameDescriptor> frameDescriptors = new ArrayList<FrameDescriptor>();

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
		if (itemsAdapter != null) {
			generateFrameDescriptors();
		}
	}

	@Override
	public void setItems(BaseAdapter adapter) {
		this.itemsAdapter = adapter;

		if (width != -1 && height != -1) {
			generateFrameDescriptors();
		}

	}

	/**
	 * TODO: Future optimization: can we avoid object allocation here?
	 */
	@Override
	public void generateFrameDescriptors() {
		if (itemWidth < 0) {
			throw new IllegalStateException("itemWidth not set");
		}

		if (height < 0 || width < 0) {
			throw new IllegalStateException("dimensions not set");
		}

		frameDescriptors.clear();
		for (int i = 0; i < itemsAdapter.getCount(); i++) {
			FrameDescriptor descriptor = new FrameDescriptor();
			Frame frame = new Frame();
			descriptor.itemIndex = i;
			frame.left = i * itemWidth;
			frame.top = 0;
			frame.width = itemWidth;
			frame.height = height;
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

			if (frameDescriptors.get(i).frame.left + itemWidth > viewPortLeft
					&& frameDescriptors.get(i).frame.left < viewPortLeft + width) {
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
		Frame frame = new Frame();
		frame.left = index * itemWidth;
		frame.top = 0;
		frame.width = width;
		frame.height = height;

		if (itemWidth != -1 && width != -1 && frameDescriptors.size() > 0
				&& frame.left > frameDescriptors.size() * itemWidth - width)
			frame.left = frameDescriptors.size() * itemWidth - width;

		return frame;
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
}
