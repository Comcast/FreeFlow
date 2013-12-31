package org.freeflow.core;

import android.view.View;

public class ItemProxy {
	public int itemIndex;
	public int itemSection;
	public Object data;
	public boolean isHeader = false;
	public int state;

	public Frame frame;
	public View view;

	public static ItemProxy clone(ItemProxy desc) {
		if (desc == null)
			return null;

		ItemProxy fd = new ItemProxy();
		fd.itemIndex = desc.itemIndex;
		fd.itemSection = desc.itemSection;
		fd.data = desc.data;
		fd.frame = Frame.clone(desc.frame);
		fd.isHeader = desc.isHeader;
		fd.state = desc.state;
		fd.view = desc.view;
		return fd;
	}

	public boolean compareFrames(Frame frame2) {
		if (frame2 == null)
			return false;

		if (frame.left != frame2.left)
			return false;

		if (frame.top != frame2.top)
			return false;

		if (frame.width != frame2.width)
			return false;

		if (frame.height != frame2.height)
			return false;

		return true;
	}
}
