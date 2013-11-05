package org.freeflow.collectionviews2;

import org.freeflow.core.BaseSectionedAdapter;
import org.freeflow.core.Container;
import org.freeflow.layouts.HGridLayout;
import org.freeflow.layouts.HLayout;
import org.freeflow.layouts.VGridLayout;
import org.freeflow.layouts.VLayout;

import android.R.color;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	Container container = null;
	HLayout hLayout = null;
	VLayout vLayout = null;

	VGridLayout vGridLayout = null;
	HGridLayout hGridLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

		ImageAdapter adapter = new ImageAdapter();

		for (int i = 0; i < 10; i++) {
			adapter.createNewSection("Section " + i);
			for (int j = 0; j < 10; j++) {
				adapter.addItemForSection(new Object(), i);
			}
		}

		container = new Container(this);

		// container.setOnTouchListener(this);
		container.setFocusable(true);
		container.requestFocus();
		hLayout = new HLayout();
		hLayout.setItemWidth(100);
		hLayout.setHeaderItemDimensions(150, 600);

		vLayout = new VLayout();
		vLayout.setItemHeight(100);
		vLayout.setHeaderItemDimensions(600, 150);

		vGridLayout = new VGridLayout();
		vGridLayout.setItemHeight(200);
		vGridLayout.setItemWidth(200);
		vGridLayout.setHeaderItemDimensions(600, 100);

		hGridLayout = new HGridLayout();
		hGridLayout.setItemHeight(200);
		hGridLayout.setItemWidth(200);
		hGridLayout.setHeaderItemDimensions(100, 600);

		container.setAdapter(adapter);
		container.setLayout(hLayout);

		frameLayout.addView(container);

		((Button) frameLayout.findViewById(R.id.transitionButton)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (container.getLayoutController() == hLayout)
					container.setLayout(vLayout);
				else if (container.getLayoutController() == vLayout)
					container.setLayout(vGridLayout);
				else if (container.getLayoutController() == vGridLayout)
					container.setLayout(hGridLayout);
				else
					container.setLayout(hLayout);
			}
		});

		frameLayout.findViewById(R.id.transitionButton).bringToFront();
	}

	class ImageAdapter extends BaseSectionedAdapter {

		@Override
		public long getItemId(int section, int position) {
			return section * 1000 + position;
		}

		@Override
		public View getViewForSection(int section, int position, View convertView, ViewGroup parent) {
			TextView tv = null;
			if (convertView != null) {
				// Log.d(TAG, "Convert view not null");
				tv = (TextView) convertView;
			} else {
				tv = new TextView(MainActivity.this);
			}

			tv.setFocusable(false);
			tv.setBackgroundResource(R.drawable.orange);
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

	}

}
