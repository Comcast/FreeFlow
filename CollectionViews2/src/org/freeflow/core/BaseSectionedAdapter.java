package org.freeflow.core;

import android.view.View;
import android.view.ViewGroup;

public interface BaseSectionedAdapter {

	public abstract long getItemId(int section, int position);

	public abstract View getViewForSection(int section, int position, View convertView, ViewGroup parent);

	public abstract View getHeaderViewForSection(int section, View convertView, ViewGroup parent);

	public abstract int getNumberOfSections();

	public abstract Section getSection(int index);

	public abstract Class[] getViewTypes();

	public abstract Class getViewType(ItemProxy proxy);

}
