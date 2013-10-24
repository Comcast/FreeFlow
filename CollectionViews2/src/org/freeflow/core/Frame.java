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
}
