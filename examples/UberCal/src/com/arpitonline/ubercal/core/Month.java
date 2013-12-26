package com.arpitonline.ubercal.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.arpitonline.ubercal.R;

public class Month {
	private int index;
	
	public Month(int index){
		this.index = index;
	}
	
	public View getView(ViewGroup parent){
		Context c = parent.getContext();
		View v = new View(c);
		v.setBackgroundDrawable(c.getResources().getDrawable(R.drawable.box));
	
		
		return v;
	}

	
	
	
	
}

