package com.comcast.freeflow.helpers;

import java.util.ArrayList;

import com.comcast.freeflow.core.ItemProxy;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class DefaultSectionAdapter implements SectionedAdapter {

	private ArrayList<Section> sections = new ArrayList<Section>();
	private Context context;
	public int headerHeight = 20;
	public int itemHeight = 20;

	public DefaultSectionAdapter(Context context, int headerCount, int itemCount) {
		this.context = context;
		setData(headerCount, itemCount);
	}

	public void setData(int headerCount, int itemCount) {
		sections.clear();
		for (int i = 0; i < headerCount; i++) {
			Section s = new Section();
			s.setSectionTitle("Section " + i);
			for (int j = 0; j < itemCount; j++) {
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
			tv = (TextView) convertView;
		} else {
			tv = new TextView(context);
		}
		tv.setLayoutParams(new LayoutParams(300, itemHeight));
		tv.setFocusable(false);
		tv.setBackgroundColor(Color.LTGRAY);
		tv.setText("s" + section + " p" + position);

		return tv;
	}

	@Override
	public View getHeaderViewForSection(int section, View convertView, ViewGroup parent) {
		TextView tv = null;
		if (convertView != null) {
			tv = (TextView) convertView;
		} else {
			tv = new TextView(context);
		}

		tv.setFocusable(false);
		tv.setBackgroundColor(Color.GRAY);
		tv.setLayoutParams(new LayoutParams(300, headerHeight));
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

	public ArrayList<Section> getSections() {
		return sections;
	}

	@Override
	public Class[] getViewTypes() {
		Class[] types = { TextView.class, TextView.class };

		return types;
	}

	@Override
	public Class getViewType(ItemProxy proxy) {

		return TextView.class;
	}

	@Override
	public boolean shouldDisplaySectionHeaders() {
		return true;
	}

}
