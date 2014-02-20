package org.freeflow.core;

import android.view.View;
import android.view.ViewGroup;

public interface SectionedAdapter {

	public long getItemId(int section, int position);

	public View getItemView(int section, int position, View convertView, ViewGroup parent);

	public View getHeaderViewForSection(int section, View convertView, ViewGroup parent);

	public int getNumberOfSections();

	public Section getSection(int index);

	public Class[] getViewTypes();

	public Class getViewType(ItemProxy proxy);
	
	public boolean shouldDisplaySectionHeaders();

}
