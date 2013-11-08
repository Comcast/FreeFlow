package org.freeflow.layouts.animations;

import org.freeflow.core.Frame;
import org.freeflow.core.FrameDescriptor;

public interface iFrameChangeListener {
	public void animateToFrame(Frame oldFrame, FrameDescriptor newFrame, int duration);
}
