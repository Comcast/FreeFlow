package org.freeflow.core;

public class FrameDescriptor {
	public int itemIndex;
	public int itemSection;
	public Object data;
	public boolean isHeader = false;

	public Frame frame;
	
	public static FrameDescriptor clone(FrameDescriptor desc){
		FrameDescriptor fd = new FrameDescriptor();
		fd.itemIndex = desc.itemIndex;
		fd.itemSection = desc.itemSection;
		fd.data = desc.data;
		fd.frame = Frame.clone(desc.frame);
		fd.isHeader = desc.isHeader;
		
		return fd;
	}
}
