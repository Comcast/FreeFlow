package org.freeflow.collectionviews2;

import java.util.HashMap;

import org.freeflow.core.Container;
import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;

import android.test.AndroidTestCase;
import android.util.Log;

public class ContainerTest extends AndroidTestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testContainerDelta(){
		Container container = new Container(getContext());
		container.onMeasureCalled(100, 100);
		
		HashMap<String, ItemProxy> oldMap = new HashMap<String, ItemProxy>();
		HashMap<String, ItemProxy> newMap = new HashMap<String, ItemProxy>();
		
		
		String one = "one";
		ItemProxy proxy = new ItemProxy();
		proxy.data = one;
		proxy.frame = new Frame(0,0, 20, 20);
		oldMap.put(one, proxy);
		
		ItemProxy proxy2 = new ItemProxy();
		proxy.data = one;
		proxy.frame =  new Frame(20,20,40,40);
		newMap.put(one, proxy2);
		
		System.out.print("YO!!");
		Log.d("atest", ">> "+container.getMeasuredWidth());
		
		assertTrue(container.getMeasuredWidth()==100);
	}
}
