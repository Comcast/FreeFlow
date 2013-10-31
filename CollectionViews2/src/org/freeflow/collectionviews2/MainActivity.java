package org.freeflow.collectionviews2;

import org.freeflow.core.Container;
import org.freeflow.layouts.HGridLayout;
import org.freeflow.layouts.HLayout;
import org.freeflow.layouts.VGridLayout;
import org.freeflow.layouts.VLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

		ImageAdapter adapter = new ImageAdapter(null);

		container = new Container(this);

		// container.setOnTouchListener(this);
		container.setFocusable(true);
		container.requestFocus();
		hLayout = new HLayout();
		hLayout.setItemWidth(100);

		vLayout = new VLayout();
		vLayout.setItemHeight(100);

		vGridLayout = new VGridLayout();
		vGridLayout.setItemHeight(200);
		vGridLayout.setItemWidth(200);

		hGridLayout = new HGridLayout();
		hGridLayout.setItemHeight(200);
		hGridLayout.setItemWidth(200);

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

	class ImageAdapter extends BaseAdapter {

		private static final String TAG = "ImageAdapter";
		private String[] images;

		public ImageAdapter(String[] images) {
			this.images = images;
		}

		@Override
		public int getCount() {
			return 100;
		}

		@Override
		public Object getItem(int position) {
			return String.valueOf(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView button = null;
			if (convertView != null) {
				// Log.d(TAG, "Convert view not null");
				button = (TextView) convertView;
			} else {
				button = new TextView(MainActivity.this);
			}

			button.setFocusable(false);
			button.setBackgroundResource(R.drawable.orange);
			// button.setOnTouchListener(MainActivity.this);
			button.setText("" + position);

			return button;
		}

	}

}
