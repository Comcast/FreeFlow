package org.freeflow.collectionviews2;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.freeflow.core.Container;
import org.freeflow.core.Frame;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.LayoutChangeSet;
import org.freeflow.helpers.SimpleDataAdapter;
import org.freeflow.layouts.VLayout;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.ViewTreeObserver.OnPreDrawListener;

public class ContainerTest extends ActivityInstrumentationTestCase2<MainActivity> {
	
	Activity main ;
	
	public ContainerTest() {
		super(MainActivity.class);
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		main = getActivity();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testViewChangesWhenVie() throws InterruptedException{
		final CountDownLatch lock = new CountDownLatch(1);
		main.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				final Container container = new Container(main);
							
				
				VLayout vLayout = new VLayout();
				vLayout.setItemHeight(100);
				vLayout.setHeaderItemDimensions(200, 10);
				vLayout.setItemHeight(300);
				container.setLayout(vLayout);
				
				SimpleDataAdapter adapter = new SimpleDataAdapter(main, 1, 2);
				container.setAdapter(adapter);
				
				
				main.setContentView(container);	
				
				container.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						
						assertEquals(3, container.getChildCount());
						lock.countDown();
						return false;
					}
				});
			}
		});
		lock.await(5000, TimeUnit.MILLISECONDS);
		
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
		
		Container container = new Container(getActivity());
		LayoutChangeSet changes = container.getViewChanges(oldMap, newMap);
		
		assertTrue(changes.getMoved().size() == 1);
		assertTrue(changes.getAdded().size() == 0);
		assertTrue(changes.getRemoved().size() == 0);
	}
	
	
//	public void testAsync(){
//		Container c = new Container(getActivity());
//		c.setLayoutParams(new LayoutParams(400,400));
//		
//	}
	
	
}
