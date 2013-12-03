package org.freeflow.examples.freeflowphotogrid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class ControlLinearLayout extends LinearLayout {
	
	private static final String TAG = "ControlLinearLayout";
	
	public ControlLinearLayout(Context context) {
		super(context);
	}

	public ControlLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "created w xml");
	}

	public ControlLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		Log.d(TAG, "created w stys");
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "onLayout");
		super.onLayout(changed, l, t, r, b);
	}
	

}
