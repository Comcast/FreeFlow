package org.freeflow.layouts;

import java.util.ArrayList;

import org.freeflow.core.Frame;
import org.freeflow.core.FrameDescriptor;

import android.util.Log;
import android.util.SparseArray;
import android.widget.BaseAdapter;

public class VLayout extends LayoutController {

	private static final String TAG = "VLayout";
	private int itemHeight = -1;
	private int width = -1;
	private int height = -1;
	private BaseAdapter itemsAdapter;
	private ArrayList<FrameDescriptor> frameDescriptors = new ArrayList<FrameDescriptor>();

	public void setItemHeight(int i) {
		this.itemHeight = i;
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
		if (itemHeight < 0) {
			throw new IllegalStateException("itemHeight not set");
		}

		if (height < 0 || width < 0) {
			throw new IllegalStateException("dimensions not set");
		}

		frameDescriptors.clear();
		for (int i = 0; i < itemsAdapter.getCount(); i++) {
			FrameDescriptor descriptor = new FrameDescriptor();
			Frame frame = new Frame();
			descriptor.itemIndex = i;
			frame.left = 0;
			frame.top = i * itemHeight;
			frame.width = width;
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

			if (frameDescriptors.get(i).frame.top + itemHeight > viewPortTop
					&& frameDescriptors.get(i).frame.top < viewPortTop + height) {
				FrameDescriptor newDesc = new FrameDescriptor();
				newDesc.itemIndex = frameDescriptors.get(i).itemIndex;
				newDesc.frame = Frame.clone(frameDescriptors.get(i).frame);

				desc.append(newDesc.itemIndex, newDesc);
			}
		}

		for (int i = 0; i < desc.size(); i++) {
			desc.get(desc.keyAt(i)).frame.top -= viewPortTop;
		}

		return desc;
	}

	@Override
	public Frame getViewportFrameForItemIndex(int index) {
		Frame frame = new Frame();
		frame.left = 0;
		frame.top = index * itemHeight;
		frame.width = width;
		frame.height = height;

		if (itemHeight != -1 && height != -1 && frame.top > frameDescriptors.size() * itemHeight - height)
			frame.top = frameDescriptors.size() * itemHeight - height;

		return frame;
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
}
