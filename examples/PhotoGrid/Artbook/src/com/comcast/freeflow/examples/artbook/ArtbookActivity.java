package com.comcast.freeflow.examples.artbook;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ArtbookActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artbook);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artbook, menu);
		return true;
	}

}
