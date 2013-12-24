package org.freeflow.layouts.animations;

import org.freeflow.core.Container;
import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.LayoutChangeSet;

import android.view.View;

public abstract class LayoutAnimator {

	public LayoutAnimator() {

	}

	public abstract void clear();
	
	public abstract void animateChanges(LayoutChangeSet changes, Container callback);
	public abstract void transitionToFrame(final Frame of, final ItemProxy nf, final View v);
	
	public abstract void start();

}
