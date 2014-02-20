package org.freeflow.examples.freeflowphotogrid;

import org.freeflow.core.Container;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;
import org.freeflow.core.SectionedAdapter;
import org.freeflow.layouts.HLayout;
import org.freeflow.layouts.VLayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SimpleCollection extends Activity implements SectionedAdapter {

	private final String TAG = "SimpleCollection";

	private Section section = null;
	Container container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple);

		// populateLL();
		populateContainer();

		findViewById(R.id.layout_change).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "changing layout");

				HLayout l = new HLayout();
				l.setHeaderItemDimensions(10, 10);
				l.setItemWidth(40);
				container.setLayout(l);

			}
		});

	}

	int[] colors = new int[] { 0xFFff0000, 0xff00ff00, 0xff0000ff };

	private void populateLL() {

		ControlLinearLayout l = (ControlLinearLayout) findViewById(R.id.control_layout);
		l.setVisibility(View.VISIBLE);
		for (int i = 0; i < colors.length; i++) {
			View v = new View(this);
			v.setBackgroundColor(colors[i]);

			v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 30));
			l.addView(v);
		}

	}

	private void populateContainer() {

		section = new Section();
		

		for (int i = 0; i < colors.length; i++) {
			section.addItem(colors[i]);
		}

		container = (Container) findViewById(R.id.container);

		VLayout vLayout = new VLayout();
		vLayout.setItemHeight(30); // Note: ItemWidth would be set by the width
									// of the container
		vLayout.setHeaderItemDimensions(10, 10);

		container.setAdapter(this);
		container.setLayout(vLayout);
	}

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}

	@Override
	public View getItemView(int section, int position, View convertView, ViewGroup parent) {

		View v;

		if (convertView == null)
			v = new View(SimpleCollection.this);
		else
			v = (View) convertView;

		v.setBackgroundColor((Integer) SimpleCollection.this.section.getDataAtIndex(position));

		return v;
	}

	@Override
	public View getHeaderViewForSection(int section, View convertView, ViewGroup parent) {

		return null;
	}

	@Override
	public int getNumberOfSections() {
		return 1;
	}

	@Override
	public Section getSection(int index) {
		return section;
	}

	@Override
	public Class[] getViewTypes() {
		Class[] types = { View.class };

		return types;
	}

	@Override
	public Class getViewType(ItemProxy proxy) {
		return View.class;
	}

	@Override
	public boolean shouldDisplaySectionHeaders() {
		return false;
	}

}
