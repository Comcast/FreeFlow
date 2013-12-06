package org.freeflow.collectionviews2;

import java.util.HashMap;

import org.freeflow.core.Container;
import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.LayoutChangeSet;

import android.test.AndroidTestCase;
import android.util.Log;

public class ContainerTest extends AndroidTestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Tests if a view is within the viewport and is moved,
	 */
	public void testViewChangesWhenViewMovesWithinViewport(){
		
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
		
		Container container = new Container(getContext());
		LayoutChangeSet changes = container.getViewChanges(oldMap, newMap);
		
		assertTrue(changes.getMoved().size() == 1);
		assertTrue(changes.getAdded().size() == 0);
		assertTrue(changes.getRemoved().size() == 0);
	}
	
	
}
