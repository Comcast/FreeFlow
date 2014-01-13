package org.freeflow.core;

/**
 * A Frame is just a Rectangle that defines the position of
 * an item. Its usually used in conjunction with <code>ItemProxy</code>
 * objects
 *
 */

public class Frame {
	public int left;
	public int top;
	public int width;
	public int height;
	
	public Frame(){
		
	}
	
	public Frame(int left, int top, int width, int height){
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	

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
	
	@Override
	public String toString(){
		return 	"Left:"+left+
				" Top:"+top+
				" Width:"+width+
				" Height:"+height;
	}
}
