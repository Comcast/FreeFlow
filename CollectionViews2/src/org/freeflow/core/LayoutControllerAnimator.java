package org.freeflow.core;

import android.animation.ValueAnimator;
import android.view.View;

public abstract class LayoutControllerAnimator {

	public LayoutControllerAnimator() {

	}

	public abstract ValueAnimator getFrameTransitionAnimation(final Frame of, final FrameDescriptor nf, final View v);
}
