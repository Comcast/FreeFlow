package com.comcast.freeflow.layouts;

import java.util.HashMap;

import com.comcast.freeflow.helpers.DefaultSectionAdapter;
import com.comcast.freeflow.teststub.MainActivity;

import com.comcast.freeflow.core.ItemProxy;

import android.test.ActivityInstrumentationTestCase2;

public class VGridLayoutTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public VGridLayoutTest() {
		super(MainActivity.class);
	}
	
	public void testGridLayoutMath(){
		VGridLayout vGrid = new VGridLayout();
		vGrid.setLayoutParams(new VGridLayout.LayoutParams(250, 250, 200, 500));
		DefaultSectionAdapter adapter = new DefaultSectionAdapter(getActivity(), 2, 5);
		vGrid.setAdapter(adapter);
		
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
