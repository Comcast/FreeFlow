package org.freeflow.core;

public class ItemProxy {
	public int itemIndex;
	public int itemSection;
	public Object data;
	public boolean isHeader = false;
	public int state;

	public Frame frame;

	public static ItemProxy clone(ItemProxy desc) {
		if(desc == null)
			return null;
		
		ItemProxy fd = new ItemProxy();
		fd.itemIndex = desc.itemIndex;
		fd.itemSection = desc.itemSection;
		fd.data = desc.data;
		fd.frame = Frame.clone(desc.frame);
		fd.isHeader = desc.isHeader;
		fd.state = desc.state;

		return fd;
	}
}
