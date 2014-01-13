package org.freeflow.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.freeflow.core.ItemProxy;

import android.graphics.Rect;

public class ViewUtils {
	public static ItemProxy getItemAt(HashMap<Object, ItemProxy> frameDescriptors, int x, int y){
		Iterator<Entry<Object, ItemProxy>> it = frameDescriptors.entrySet().iterator();
		
		while (it.hasNext()) {
	    	Entry<Object, ItemProxy> pair = it.next();
	        Rect f = pair.getValue().frame;
	    	
	    	if(f.contains((int)x, (int)y)) return pair.getValue();
	      
	    }
		return null;
	}
}
