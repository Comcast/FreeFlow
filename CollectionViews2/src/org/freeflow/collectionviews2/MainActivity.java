package org.freeflow.collectionviews2;

import org.freeflow.core.Container;
import org.freeflow.layouts.HLayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

		String[] images = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
				"14", "15", "16", "17", "18", "19" };
		ImageAdapter adapter = new ImageAdapter(images);

		Container container = new Container(this);
		HLayout layout = new HLayout();
		layout.setItemWidth(100);
		container.setLayout(layout);
		container.setAdapter(adapter);

		frameLayout.addView(container);

	}

	class ImageAdapter extends BaseAdapter {

		private static final String TAG = "ImageAdapter";
		private String[] images;

		public ImageAdapter(String[] images) {
			this.images = images;
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object getItem(int position) {
			return images[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Button button = null;
			if (convertView != null) {
				Log.d(TAG, "Convert view not null");
				button = (Button) convertView;
			} else {
				button = new Button(MainActivity.this);
			}

			// button.setFocusable(false);
			button.setText("" + images[position]);

			return button;
		}

	}

}
