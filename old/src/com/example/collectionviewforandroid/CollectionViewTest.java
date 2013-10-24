package com.example.collectionviewforandroid;

import org.freeflow.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class CollectionViewTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection_view_test);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.collection_view_test, menu);
		return true;
	}

}
