package com.comcast.freeflow.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.comcast.freeflow.core.ItemProxy;

public class ViewUtils {
	public static ItemProxy getItemAt(HashMap<? extends Object, ItemProxy> frameDescriptors, int x, int y){

		Iterator<? extends Object> it=  frameDescriptors.entrySet().iterator();
			
		while (it.hasNext()) {
			Entry<Object, ItemProxy> pair = (Entry<Object, ItemProxy>) it.next();
			if(pair.getValue().frame.contains((int)x, (int)y)) return pair.getValue();
	      
	    }
		return null;
	}
}
