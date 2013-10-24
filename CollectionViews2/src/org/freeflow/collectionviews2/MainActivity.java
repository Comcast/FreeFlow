package org.freeflow.collectionviews2;

import org.freeflow.core.Container;
import org.freeflow.layouts.HLayout;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String[] images = new String[]{"", "", ""};
		ImageAdapter adapter = new ImageAdapter(images);
		
		Container container = new Container(this);
		HLayout layout = new HLayout();
		layout.setItemWidth(100);
		container.setLayout(layout);
		container.setAdapter(adapter);
		
		
	}
	
	class ImageAdapter extends BaseAdapter{

		private String[] images;
		public ImageAdapter(String[] images){
			this.images = images;
		}
		
		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}
		
	}

}
