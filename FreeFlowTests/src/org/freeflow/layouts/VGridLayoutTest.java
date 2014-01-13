package org.freeflow.layouts;

import java.util.HashMap;

import org.freeflow.core.ItemProxy;
import org.freeflow.helpers.DefaultSectionAdapter;
import org.freeflow.teststub.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class VGridLayoutTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public VGridLayoutTest() {
		super(MainActivity.class);
	}
	
	public void testGridLayoutMath(){
		VGridLayout vGrid = new VGridLayout();
		vGrid.setHeaderItemDimensions(200, 500);
		vGrid.setItemHeight( 250 );
		vGrid.setItemWidth(250);
		
		DefaultSectionAdapter adapter = new DefaultSectionAdapter(getActivity(), 2, 5);
		vGrid.setItems(adapter);
		
		vGrid.setDimensions(600, 1000);
		
		HashMap<? extends Object , ItemProxy> map ;
		map = vGrid.getItemProxies(0, 0);
		
		assertEquals("VGridLayout did not generate correct number of frames", 5, map.size());
		vGrid.setDimensions(600, 1001);
		map = vGrid.getItemProxies(0, 0);
		assertEquals("VGridLayout did not generate correct number of frames (2) ", 6, map.size());
		
		ItemProxy proxy = map.get( adapter.getSection(0).getSectionTitle() );
		
		assertNotNull("Header frame was null", proxy);
		
	}

}
