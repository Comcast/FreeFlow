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
import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.FreeFlowContainer.OnScrollListener;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.HLayout;
import com.comcast.freeflow.layouts.VGridLayout;
import com.comcast.freeflow.layouts.VLayout;
import com.comcast.freeflow.layouts.VGridLayout.LayoutParams;
import com.comcast.freeflow.examples.artbook.data.DribbbleDataAdapter;
import com.comcast.freeflow.examples.artbook.layouts.ArtbookLayout;
import com.comcast.freeflow.examples.artbook.models.DribbbleFeed;
import com.comcast.freeflow.examples.artbook.models.DribbbleFetch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class ArtbookActivity extends Activity implements OnClickListener{

	public static final String TAG = "ArtbookActivity";

	private FreeFlowContainer container;
	private VGridLayout grid;
	private ArtbookLayout custom;

	private DribbbleFetch fetch;
	private int itemsPerPage = 25;
	private int pageIndex = 1;
	
	DribbbleDataAdapter adapter;
	
	FreeFlowLayout[] layouts;
	int currLayoutIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artbook);
		

		container = (FreeFlowContainer) findViewById(R.id.container);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		findViewById(R.id.load_more).setOnClickListener(this);
		//Our new layout
		custom = new ArtbookLayout();
		
		//Grid Layout
		grid = new VGridLayout();
		VGridLayout.LayoutParams params = new VGridLayout.LayoutParams(size.x/2, size.x/2);
		grid.setLayoutParams(params);
		
		//Vertical Layout
		VLayout vlayout = new VLayout();
		VLayout.LayoutParams params2 = new VLayout.LayoutParams(size.x);
		vlayout.setLayoutParams(params2);
		
		//HLayout
		HLayout hlayout = new HLayout();
		hlayout.setLayoutParams(new HLayout.LayoutParams(size.x));
		
		
		layouts = new FreeFlowLayout[]{custom, grid, vlayout, hlayout};
		
		adapter = new DribbbleDataAdapter(this);
		
		
		container.setLayout(layouts[currLayoutIndex]);
		container.setAdapter(adapter);
		
		
		fetch = new DribbbleFetch();
		
		fetch.load(this,itemsPerPage , pageIndex);

	}

	public void onDataLoaded(DribbbleFeed feed) {
		Log.d(TAG, "photo: " + feed.getShots().get(0).getImage_teaser_url());
		adapter.update(feed);
		container.dataInvalidated();
		container.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AbsLayoutContainer parent, FreeFlowItem proxy) {
				
			}
		});
		
		container.addScrollListener( new OnScrollListener() {
			 
			@Override
			public void onScroll(FreeFlowContainer container) {
				Log.d(TAG, "scroll percent "+ container.getScrollPercentY() );
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
			currLayoutIndex++;
			if(currLayoutIndex == layouts.length){
				currLayoutIndex =  0;
			}
			container.setLayout(layouts[currLayoutIndex]);
			
			break;
		case (R.id.action_about): Intent about = new Intent(this, AboutActivity.class);
			startActivity(about);
			break;
		}
		
		return true;
		
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "Loading data");
		pageIndex++;
		fetch.load(this, itemsPerPage, pageIndex);
	}
}
