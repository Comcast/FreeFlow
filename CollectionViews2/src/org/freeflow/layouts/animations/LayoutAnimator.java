package org.freeflow.layouts.animations;

import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;

import android.view.View;

public abstract class LayoutAnimator {

	public LayoutAnimator() {

	}
	
	public abstract void clear();

	public abstract void transitionToFrame(final Frame of, final ItemProxy nf, final View v);
	
	public abstract void start();
}
