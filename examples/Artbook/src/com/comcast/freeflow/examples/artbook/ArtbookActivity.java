package com.comcast.freeflow.examples.artbook;

import org.freeflow.core.Container;
import org.freeflow.layouts.VGridLayout;

import com.comcast.freeflow.examples.artbook.data.DribbbleDataAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;

public class ArtbookActivity extends Activity {

	public static final String TAG = "ArtbookActivity";

	private Container container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artbook);
		DribbbleFetch fetch = new DribbbleFetch();
		fetch.load(this);

		container = (Container) findViewById(R.id.container);
	}

	public void onDataLoaded(DribbbleFeed feed) {
		Log.d(TAG, "photo: " + feed.getShots().get(0).getImage_teaser_url());
		DribbbleDataAdapter adapter = new DribbbleDataAdapter(this, feed);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		VGridLayout grid = new VGridLayout();
		grid.setHeaderItemDimensions(0, 0);
		grid.setItemWidth(size.x / 2);
		grid.setItemHeight(size.x / 2);
		
		container.setLayout(grid);
		container.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artbook, menu);
		return true;
	}

}
