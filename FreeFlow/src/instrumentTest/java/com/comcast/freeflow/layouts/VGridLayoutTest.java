/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.comcast.freeflow.layouts;

import java.util.HashMap;

import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.helpers.DefaultSectionAdapter;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class VGridLayoutTest extends InstrumentationTestCase {
	private Context context;

	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getContext();
	}

	public void testGridLayoutMath(){
		VGridLayout vGrid = new VGridLayout();
		vGrid.setLayoutParams(new VGridLayout.LayoutParams(250, 250, 200, 500));
		DefaultSectionAdapter adapter = new DefaultSectionAdapter(context, 2, 5);
		vGrid.setAdapter(adapter);

		vGrid.setDimensions(600, 1000);
		vGrid.prepareLayout();

		HashMap<? extends Object , FreeFlowItem> map ;
		map = vGrid.getItemProxies(0, 0);

		assertEquals("VGridLayout did not generate correct number of frames", 5, map.size());
		vGrid.setDimensions(600, 1001);
		map = vGrid.getItemProxies(0, 0);
		assertEquals("VGridLayout did not generate correct number of frames (2) ", 6, map.size());

		FreeFlowItem proxy = map.get( adapter.getSection(0).getHeaderData() );

		assertNotNull("Header frame was null", proxy);
	}
}
