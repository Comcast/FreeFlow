package org.freeflow.layouts.animations;

import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;

public interface iFrameChangeListener {
	public void animateToFrame(Frame oldFrame, ItemProxy newFrame, int duration);
}
