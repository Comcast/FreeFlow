package com.arpitonline.collectionviews;

import java.util.ArrayList;

import android.view.View;

public interface ICollectionViewLayout {
	public void measure(CollectionContainer parent, ArrayList<View> views, int widthMeasureSpec, int heightMeasureSpec);
	public void layout(ArrayList<View> views, boolean changed, int l, int t, int r, int b);
	
}
