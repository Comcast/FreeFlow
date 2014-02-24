package com.comcast.freeflow.animations;

import com.comcast.freeflow.core.Container;
import com.comcast.freeflow.core.LayoutChangeSet;

public abstract class LayoutAnimator {

	protected LayoutChangeSet changeSet;

	public LayoutAnimator() {

	}

	public LayoutChangeSet getChangeSet() {
		return changeSet;
	}

	public abstract void cancel();

	public abstract void animateChanges(LayoutChangeSet changes, Container callback);

	/*
	 * public abstract void transitionToFrame(final Frame of, final ItemProxy
	 * nf, final View v);
	 */

	public abstract void start();

}
