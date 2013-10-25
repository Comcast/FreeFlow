package org.freeflow.core;

import java.util.ArrayList;

import org.freeflow.layouts.LayoutController;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class Container extends ViewGroup implements OnKeyListener {

	private static final String TAG = "Container";
	private SparseArray<View> usedViews;
	private ArrayList<View> viewpool;
	private SparseArray<FrameDescriptor> frames = null;
	private boolean inMeasure = false;
	private BaseAdapter itemAdapter;
	private LayoutController layoutController;
	private int viewPortX = 0;

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
		setOnKeyListener(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

		if (layoutController != null) {
			inMeasure = true;
			layoutController.setDimensions(getMeasuredWidth(), getMeasuredHeight());
			SparseArray<FrameDescriptor> oldFrames = frames;
			frames = layoutController.getFrameDescriptors(viewPortX, 0);

			for (int i = 0; i < frames.size(); i++) {
				FrameDescriptor frameDesc = frames.get(frames.keyAt(i));

				if (oldFrames != null && oldFrames.get(frameDesc.itemIndex) != null) {
					oldFrames.remove(frameDesc.itemIndex);
				}

				if (usedViews.get(frameDesc.itemIndex) == null) {
					View view = itemAdapter.getView(frameDesc.itemIndex, viewpool.size() > 0 ? viewpool.remove(0)
							: null, this);

					view.setOnKeyListener(this);

					int widthSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.width, MeasureSpec.EXACTLY);
					int heightSpec = MeasureSpec.makeMeasureSpec(frameDesc.frame.height, MeasureSpec.EXACTLY);

					view.measure(widthSpec, heightSpec);

					usedViews.append(frameDesc.itemIndex, view);
					addView(view);
				}
			}

			for (int i = 0; oldFrames != null && i < oldFrames.size(); i++) {
				View view = usedViews.get(oldFrames.keyAt(i));
				viewpool.add(view);
				usedViews.remove(oldFrames.keyAt(i));
				removeView(view);

				Log.d(TAG, "removing unused view");
			}

		}

		inMeasure = false;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (layoutController == null || frames == null)
			return;

		for (int i = 0; i < usedViews.size(); i++) {
			View v = usedViews.get(usedViews.keyAt(i));

			if (v != null) {
				Frame frame = frames.get(usedViews.keyAt(i)).frame;
				v.layout(frame.left, frame.top, frame.left + frame.width, frame.top + frame.height);
			}
		}

	}

	public void setLayout(LayoutController lc) {
		layoutController = lc;
		requestLayout();
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

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			if (keyCode == KeyEvent.KEYCODE_D) {
				Log.d(TAG, "D pressed");
				viewPortX += 5;
				viewPortX = viewPortX > 1000 ? 1000 : viewPortX;
				requestLayout();
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_A) {
				Log.d(TAG, "A pressed");
				viewPortX -= 5;
				viewPortX = viewPortX < 0 ? 0 : viewPortX;
				requestLayout();
				return true;
			}
		}

		return false;
	}

}
