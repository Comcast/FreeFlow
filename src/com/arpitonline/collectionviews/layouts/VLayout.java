package com.arpitonline.collectionviews.layouts;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;

import com.arpitonline.collectionviews.CollectionContainer;
import com.arpitonline.collectionviews.ICollectionViewLayout;

public class VLayout implements ICollectionViewLayout {
	
	private static final String TAG = "VLayout";
	
	@Override
	public void measure(CollectionContainer parent, ArrayList<View> views, int widthMeasureSpec,
			int heightMeasureSpec) {
		
		int fullW  = MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), MeasureSpec.EXACTLY);
		
		
	
		
		int anyHt;
		
		for(int i=0; i<views.size(); i++){
			View v = views.get(i);
			
			LayoutParams params = v.getLayoutParams();
			
			if(params.height == LayoutParams.WRAP_CONTENT){
				anyHt = MeasureSpec.makeMeasureSpec(1000, MeasureSpec.AT_MOST);
			}
			else{
				anyHt = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
			}
			
			
			views.get(i).measure(fullW, anyHt);
		}
	}
	
	@Override
	public void layout(ArrayList<View> views, boolean changed, int l, int t, int r, int b) {
		int top = t;
		
		for(int i=0; i<views.size(); i++){
			View v = views.get(i);
			v.layout(l, top, r, top+v.getMeasuredHeight());
			top = top+v.getHeight()+5;
		}
	}
	

}
