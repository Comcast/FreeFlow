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
package com.comcast.freeflow.examples.freeflowphotogrid;

import java.util.ArrayList;

import com.comcast.freeflow.animations.DefaultLayoutAnimator;
import com.comcast.freeflow.core.AbsLayoutContainer;
import com.comcast.freeflow.core.AbsLayoutContainer.OnItemClickListener;
import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;
import com.comcast.freeflow.examples.freeflowphotogrid.R;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.HGridLayout;
import com.comcast.freeflow.layouts.HLayout;
import com.comcast.freeflow.layouts.VGridLayout;
import com.comcast.freeflow.layouts.VLayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private FreeFlowContainer container = null;
	private HLayout hLayout = null;
	private VLayout vLayout = null;
	private VGridLayout vGridLayout = null;
	private HGridLayout hGridLayout = null;
	
	private Button changeButton, jumpButton, jumpButtonAnim;
	
	private FreeFlowLayout[] layouts ;
	private int currentLayoutIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

		final ImageAdapter adapter = new ImageAdapter();

		container = new FreeFlowContainer(this);

		DefaultLayoutAnimator anim = (DefaultLayoutAnimator) container.getLayoutAnimator();
		anim.animateAllSetsSequentially = false;
		anim.animateIndividualCellsSequentially = false;

		container.requestFocus();
		hLayout = new HLayout();
		hLayout.setLayoutParams(new HLayout.LayoutParams(100, 150, 600));

		vLayout = new VLayout();
		vLayout.setLayoutParams(new VLayout.LayoutParams(100, 600, 150));

		vGridLayout = new VGridLayout();
		vGridLayout.setLayoutParams(new VGridLayout.LayoutParams(200,200, 600, 100));

		hGridLayout = new HGridLayout();
		hGridLayout.setLayoutParams(new HGridLayout.LayoutParams(200, 200, 100, 600));
	
		layouts = new FreeFlowLayout[]{ vLayout,hLayout, vGridLayout, hGridLayout};
		
		container.setAdapter(adapter);
		container.setLayout(layouts[currentLayoutIndex]);

		frameLayout.addView(container);

		changeButton = ((Button) frameLayout.findViewById(R.id.transitionButton));
		changeButton.setText("Layout");
		
		changeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentLayoutIndex++;
				if(currentLayoutIndex == layouts.length){
					currentLayoutIndex = 0;
				}
				container.setLayout(layouts[currentLayoutIndex]);
			}
		});

		jumpButton = (Button) findViewById(R.id.jumpButton);
		jumpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int section = (int) (adapter.getNumberOfSections() * Math.random());
				int index = (int) (adapter.getSection(section).getDataCount() * Math.random());
				String s = "section = " + section + ", index = " + index;
				Toast.makeText(MainActivity.this,s , Toast.LENGTH_SHORT ).show();
				container.scrollToItem(section, index, false);

			}
		});
		
		jumpButtonAnim = (Button) findViewById(R.id.jumpButtonAnim);
		jumpButtonAnim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int section = (int) (adapter.getNumberOfSections() * Math.random());
				int index = (int) (adapter.getSection(section).getDataCount() * Math.random());
				String s = "section = " + section + ", index = " + index;
				Toast.makeText(MainActivity.this,s , Toast.LENGTH_SHORT ).show();
				container.scrollToItem(section, index, true);

			}
		});
		
		
		container.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(AbsLayoutContainer parent, FreeFlowItem proxy) {
				Log.d("Test", "container item: "+proxy.itemSection +" /  "+proxy.itemIndex );
			}
		});
		
		changeButton.bringToFront();
		jumpButton.bringToFront();
		jumpButtonAnim.bringToFront();
	}

	class ImageAdapter implements SectionedAdapter {

		private ArrayList<Section> sections = new ArrayList<Section>();

		public ImageAdapter() {
			for (int i = 0; i < 10; i++) {
				Section s = new Section();

				s.setSectionTitle("Section " + i);
				for (int j = 0; j < 10; j++) {
					s.addItem(new Object());
				}
				sections.add(s);
			}
		}

		@Override
		public long getItemId(int section, int position) {
			return section * 1000 + position;
		}

		@Override
		public View getItemView(int section, int position, View convertView, ViewGroup parent) {
			TextView tv = null;
			if (convertView != null) {
				// Log.d(TAG, "Convert view not null");
				tv = (TextView) convertView;
			} else {
				tv = new TextView(MainActivity.this);
			}

			tv.setFocusable(false);
			tv.setBackgroundResource(R.drawable.orange);
			// tv.setAlpha(.25f);
			// button.setOnTouchListener(MainActivity.this);
			tv.setText("s" + section + " p" + position);

			return tv;
		}

		@Override
		public View getHeaderViewForSection(int section, View convertView, ViewGroup parent) {
			TextView tv = null;
			if (convertView != null) {
				// Log.d(TAG, "Convert view not null");
				tv = (TextView) convertView;
			} else {
				tv = new TextView(MainActivity.this);
			}

			tv.setFocusable(false);
			tv.setBackgroundColor(Color.GRAY);
			// button.setOnTouchListener(MainActivity.this);
			tv.setText("section header" + section);

			return tv;
		}

		@Override
		public int getNumberOfSections() {
			return sections.size();
		}

		@Override
		public Section getSection(int index) {
			if (index < sections.size() && index >= 0)
				return sections.get(index);

			return null;
		}

		@Override
		public Class[] getViewTypes() {
			Class[] types = { TextView.class, TextView.class };

			return types;
		}

		@Override
		public Class getViewType(FreeFlowItem proxy) {
			return TextView.class;
		}

		@Override
		public boolean shouldDisplaySectionHeaders() {
			return true;
		}

	}

}
