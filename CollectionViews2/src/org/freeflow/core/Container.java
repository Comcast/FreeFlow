package org.freeflow.core;

import java.util.ArrayList;

import org.freeflow.layouts.LayoutController;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class Container extends ViewGroup {

	private static final String TAG = "Container";
	private SparseArray<View> usedViews;
	private ArrayList<View> viewpool;
	private SparseArray<FrameDescriptor> frames = null;
	private boolean inMeasure = false;
	private BaseAdapter itemAdapter;
	private LayoutController layoutController;
	public int viewPortX = 0;
	public int viewPortY = 0;

	public Container(Context context) {
		super(context);
		init();
	}

	public Container(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Container(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		usedViews = new SparseArray<View>();
		viewpool = new ArrayList<View>();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		if (layoutController != null) {
			inMeasure = true;
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());
			SparseArray<FrameDescriptor> oldFrames = frames;
			frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);

			for (int i = 0; i < frames.size(); i++) {
				FrameDescriptor frameDesc = frames.get(frames.keyAt(i));

				if (oldFrames != null && oldFrames.get(frameDesc.itemIndex) != null) {
					oldFrames.remove(frameDesc.itemIndex);
				}

				doMeasure(frameDesc);
				cleanupViews(oldFrames);
			}
		}
		inMeasure = false;

	}

	private void doMeasure(FrameDescriptor frameDesc) {

		if (usedViews.get(frameDesc.itemIndex) == null) {
			View view = itemAdapter.getView(frameDesc.itemIndex, viewpool.size() > 0 ? viewpool.remove(0) : null, this);

			int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width, MeasureSpec.EXACTLY);
			int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height, MeasureSpec.EXACTLY);

			view.measure(widthSpec, heightSpec);

			usedViews.append(frameDesc.itemIndex, view);
			addView(view);
		} else {
			int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width, MeasureSpec.EXACTLY);
			int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height, MeasureSpec.EXACTLY);
			usedViews.get(frameDesc.itemIndex).measure(widthSpec, heightSpec);
		}
	}

	private void cleanupViews(SparseArray<FrameDescriptor> oldFrames) {
		for (int i = 0; oldFrames != null && i < oldFrames.size(); i++) {
			View view = usedViews.get(oldFrames.keyAt(i));
			viewpool.add(view);
			usedViews.remove(oldFrames.keyAt(i));
			removeView(view);

			Log.d(TAG, "removing unused view");
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (layoutController == null || frames == null)
			return;
		for (int i = 0; i < usedViews.size(); i++) {
			View v = usedViews.get(usedViews.keyAt(i));

			if (v != null) {
				Frame frame = frames.get(usedViews.keyAt(i)).frame;
				doLayout(v, frame);
			}
		}

	}

	private void doLayout(View view, Frame frame) {
		view.layout(frame.left, frame.top, frame.left + frame.width, frame.top + frame.height);
	}

	public void setLayout(LayoutController lc) {

		layoutController = lc;

		FrameDescriptor oldFrame = null;

		SparseArray<FrameDescriptor> oldFrames = frames;

		if (frames != null) {
			oldFrame = frames.get(frames.keyAt(0));
			int index = oldFrame.itemIndex;
			Frame vpFrame = layoutController.getViewportFrameForItemIndex(index);

			viewPortX = vpFrame.left;
			viewPortY = vpFrame.top;
		}

		if (this.itemAdapter != null) {
			layoutController.setItems(itemAdapter);
		}

		if (frames != null && oldFrames != null) {
			animateBetweenLayouts(oldFrames);
		} else {
			requestLayout();
		}

	}

	private void animateBetweenLayouts(SparseArray<FrameDescriptor> oldFrames) {
		layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());
		frames = layoutController.getFrameDescriptors(viewPortX, viewPortY);

		cleanupViews(oldFrames);
		
		for (int i = 0; i < frames.size(); i++) {

			final FrameDescriptor of = oldFrames.get(oldFrames.keyAt(i));
			final FrameDescriptor nf = frames.get(of.itemIndex);

			if (usedViews.get(nf.itemIndex) == null) {
				doMeasure(nf);
			}

			ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
			anim.setDuration(500);
			anim.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					View v = usedViews.get(of.itemIndex);
					int itemWidth = of.frame.width
							+ (int) ((nf.frame.width - of.frame.width) * animation.getAnimatedFraction());
					int itemHeight = of.frame.height
							+ (int) ((nf.frame.height - of.frame.height) * animation.getAnimatedFraction());
					int widthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);
					int heightSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY);

					v.measure(widthSpec, heightSpec);

					Frame frame = new Frame();
					Frame nff = nf.frame;
					Frame off = of.frame;
					frame.left = (int) (off.left + (nff.left - off.left) * animation.getAnimatedFraction());
					frame.top = (int) (off.top + (nff.top - off.top) * animation.getAnimatedFraction());
					frame.width = (int) (off.width + (nff.width - off.width) * animation.getAnimatedFraction());
					frame.height = (int) (off.height + (nff.height - off.height) * animation.getAnimatedFraction());
					v.layout(frame.left, frame.top, frame.left + frame.width, frame.top + frame.height);
				}
			});
			
			

			anim.start();
		}

	}

	@Override
	public void requestLayout() {

		if (inMeasure)
			return;

		super.requestLayout();
	}

	public void setAdapter(BaseAdapter adapter) {
		this.itemAdapter = adapter;
		if (layoutController != null) {
			layoutController.setItems(adapter);
		}
	}

}
