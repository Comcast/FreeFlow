package com.comcast.freeflow.examples.artbook.layouts;

import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;
import org.freeflow.core.SectionedAdapter;

import android.view.View;
import android.view.ViewGroup;

public class ArtbookLayout implements SectionedAdapter {

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		return null;
	}

	@Override
	public View getHeaderViewForSection(int section, View convertView,
			ViewGroup parent) {
		return null;
	}

	@Override
	public int getNumberOfSections() {
		return 0;
	}

	@Override
	public Section getSection(int index) {
		return null;
	}

	@Override
	public Class[] getViewTypes() {
		return null;
	}

	@Override
	public Class getViewType(ItemProxy proxy) {
		return null;
	}

	@Override
	public boolean shouldDisplaySectionHeaders() {
		return false;
	}

}
