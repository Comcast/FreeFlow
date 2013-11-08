package org.freeflow.core;

import android.view.View;

public abstract class LayoutControllerAnimator {

	public LayoutControllerAnimator() {

	}

	public abstract void transitionToFrame(final Frame of, final FrameDescriptor nf, final View v, final int duration);
}
