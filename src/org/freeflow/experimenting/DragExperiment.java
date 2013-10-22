package org.freeflow.experimenting;

import java.util.ArrayList;

import org.freeflow.interfaces.AdapterChangeObserver;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class DragExperiment extends ViewGroup implements AdapterChangeObserver {

	private static final String TAG = "DragExperiment";
	private Viewport viewport;
	private VelocityTracker mVelocityTracker = null;
	private ArrayList<View> viewPool;

	private DragAdapter adapter;
	float deltaX = -1f;

	private int maxSize = 0;

	public DragExperiment(Context context) {
		super(context);
		init();
	}

	public DragExperiment(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	public DragExperiment(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setBackgroundColor(Color.LTGRAY);
		this.viewport = new Viewport();
		viewPool = new ArrayList<View>();

		adapter = new DragAdapter();

		dataSetChanged();

		for (int i = 0; i < 15; i++)
			viewPool.add(new Button(this.getContext()));

	}

	public void setAdapter(DragAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(600, 500);

		int freespec = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY);
		for (int i = 0; i < getChildCount(); i++)
			getChildAt(i).measure(freespec, freespec);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();

		mVelocityTracker.addMovement(event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			deltaX = event.getX();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			moveScreen(event.getX() - deltaX);

			deltaX = event.getX();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			return true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {

			mVelocityTracker.computeCurrentVelocity(1000);

			if (Math.abs(mVelocityTracker.getXVelocity()) > 100) {
				final float velocity = mVelocityTracker.getXVelocity();
				ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int translateX = (int) ((1 - animation.getAnimatedFraction()) * velocity / 350);

						moveScreen(translateX);

						if (animation.getAnimatedFraction() == 1f)
							requestLayout();
					}
				});

				animator.setDuration(500);
				animator.start();

			} else {
				requestLayout();
			}
			return true;
		}

		return false;

	}

	private void moveScreen(float movement) {

		float oldLeft = viewport.getLeft();

		viewport.setLeft(viewport.getLeft() + (movement));

		if ((maxSize - getMeasuredWidth()) + viewport.getLeft() < 0) {
			viewport.setLeft(-(maxSize - getMeasuredWidth()));
		} else if (viewport.getLeft() > 0) {
			viewport.setLeft(0);
		} else {

			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				child.setTranslationX(getChildAt(i).getTranslationX() + ((viewport.getLeft() - oldLeft)));
				if (child.getX() + child.getMeasuredWidth() > 0 && child.getX() < getMeasuredWidth())
					child.setVisibility(VISIBLE);

			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int leftStart = 0;

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setTranslationX(0);

			if (((int) viewport.getLeft()) + leftStart + child.getMeasuredWidth() > 0) {
				child.layout(leftStart + (int) viewport.getLeft(), 100, leftStart + child.getMeasuredWidth()
						+ (int) viewport.getLeft(), 100 + child.getMeasuredHeight());
				child.setVisibility(VISIBLE);
			} else {
				Log.d(TAG, "skip");

				child.setVisibility(GONE);
			}

			leftStart += child.getMeasuredWidth();
		}

	}

	public class Viewport {
		private float left = 0, top = 0;

		public void setLeft(float left) {
			this.left = left;
		}

		public void setTop(float top) {
			this.top = top;
		}

		public float getLeft() {
			return left;
		}

		public float getTop() {
			return top;
		}
	}

	public class DragAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 20;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new Button(DragExperiment.this.getContext());
			}

			((Button) convertView).setText("Button: " + position);

			return convertView;
		}

	}

	@Override
	public void dataSetChanged() {
		maxSize = adapter.getCount() * 100;
	}

}
