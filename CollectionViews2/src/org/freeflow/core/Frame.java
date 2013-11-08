package org.freeflow.core;

public class Frame {
	public int left;
	public int top;
	public int width;
	public int height;

	public static Frame clone(Frame frame) {
		Frame newFrame = new Frame();
		newFrame.left = frame.left;
		newFrame.top = frame.top;
		newFrame.width = frame.width;
		newFrame.height = frame.height;

		return newFrame;
	}

	public boolean equals(Frame frame) {
		if(frame == null)
			return false;
		
		if (frame.left == left && frame.top == top && frame.width == width && frame.height == height)
			return true;

		return false;
	}
}
