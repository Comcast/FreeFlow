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
package com.comcast.freeflow.examples.artbook;

import com.comcast.freeflow.core.AbsLayoutContainer;
import com.comcast.freeflow.core.AbsLayoutContainer.OnItemClickListener;
import com.comcast.freeflow.core.Container;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Container.OnScrollListener;
import com.comcast.freeflow.layouts.VGridLayout;
import com.comcast.freeflow.layouts.VGridLayout.LayoutParams;

import com.comcast.freeflow.examples.artbook.data.DribbbleDataAdapter;
import com.comcast.freeflow.examples.artbook.layouts.ArtbookLayout;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

public class ArtbookActivity extends Activity {

	public static final String TAG = "ArtbookActivity";

	private Container container;
	private VGridLayout grid;
	private ArtbookLayout custom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artbook);
		DribbbleFetch fetch = new DribbbleFetch();
		fetch.load(this);

		container = (Container) findViewById(R.id.container);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		grid = new VGridLayout();
		VGridLayout.LayoutParams params = new VGridLayout.LayoutParams(size.x/2, size.x/2);
		grid.setLayoutParams(params);

		custom = new ArtbookLayout();

	}

	public void onDataLoaded(DribbbleFeed feed) {
		Log.d(TAG, "photo: " + feed.getShots().get(0).getImage_teaser_url());
		DribbbleDataAdapter adapter = new DribbbleDataAdapter(this, feed);
		container.setLayout(custom);
		container.setAdapter(adapter);
		container.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AbsLayoutContainer parent, FreeFlowItem proxy) {
				
			}
		});
		
		container.addScrollListener( new OnScrollListener() {
			
			@Override
			public void onScroll(Container container) {
				Log.d(TAG, "scroll percent "+ container.getScrollPercentX() );
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.artbook, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		case (R.id.action_change_layout):
			if(container.getLayout() == grid){
				container.setLayout(custom);
			}
			else{
				container.setLayout(grid);
			}
		}
		
		return true;
		
	}
}
