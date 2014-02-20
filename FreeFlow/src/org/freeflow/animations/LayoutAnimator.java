package org.freeflow.animations;

import org.freeflow.core.Container;
import org.freeflow.core.LayoutChangeSet;

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
