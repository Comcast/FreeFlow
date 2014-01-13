package org.freeflow.core;

import android.view.View;
import android.view.ViewGroup;

public interface BaseSectionedAdapter {

	public long getItemId(int section, int position);

	public View getViewForSection(int section, int position, View convertView, ViewGroup parent);

	public View getHeaderViewForSection(int section, View convertView, ViewGroup parent);

	public int getNumberOfSections();

	public Section getSection(int index);

	public Class[] getViewTypes();

	public Class getViewType(ItemProxy proxy);
	
	public boolean shouldDisplaySectionHeaders();

}
