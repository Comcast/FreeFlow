package org.freeflow;

import java.util.ArrayList;

import org.freeflow.layouts.VLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class CollectionContainer extends ViewGroup {
	
	private static final String TAG = "CollectionContainer";
	private ICollectionViewLayout currentLayout;
	
	
	public CollectionContainer(Context context) {
		super(context);
		init(context, null, -1);
	}

	public CollectionContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, -1);
	}

	public CollectionContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle){
		this.currentLayout = new VLayout();
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d(TAG, "measuring: "+getChildCount());
		this.currentLayout.measure(this, getChildren(), widthMeasureSpec, heightMeasureSpec);
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		currentLayout.layout(getChildren(), changed, l, t, r, b);
	}
	
	private ArrayList<View> children;
	public ArrayList<View> getChildren(){
		children = new ArrayList<View>();
		for(int i=0; i<getChildCount(); i++){
			children.add(getChildAt(i));
		}
		return children;
	}

}
