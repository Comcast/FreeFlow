package org.freeflow.core;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

public class ItemProxy {
	public int itemIndex;
	public int itemSection;
	public Object data;
	public boolean isHeader = false;
	public Rect frame;
	public View view;
	public Bundle extras;

	public static ItemProxy clone(ItemProxy desc) {
		if (desc == null)
			return null;

		ItemProxy fd = new ItemProxy();
		fd.itemIndex = desc.itemIndex;
		fd.itemSection = desc.itemSection;
		fd.data = desc.data;
		fd.frame = new Rect(desc.frame);
		fd.isHeader = desc.isHeader;
		fd.view = desc.view;
		fd.extras = desc.extras;
		return fd;
	}
}
