package org.freeflow.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;

import android.graphics.Rect;

public class ViewUtils {
	public static ItemProxy getItemAt(HashMap<Object, ItemProxy> frameDescriptors, int x, int y){
		Iterator<Entry<Object, ItemProxy>> it = frameDescriptors.entrySet().iterator();
		Rect rect = new Rect();
		
		while (it.hasNext()) {
	    	Entry<Object, ItemProxy> pair = it.next();
	        Frame f = pair.getValue().frame;
	    	rect.left = f.left;
	    	rect.right = f.left+f.width;
	    	rect.top = f.top;
	    	rect.bottom = f.top+f.height;
	    	
	    	if(rect.contains((int)x, (int)y)) return pair.getValue();
	      
	    }
		return null;
	}
}
