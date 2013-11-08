package org.freeflow.core;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;

public abstract class BaseSectionedAdapter {

	protected ArrayList<Section> sections;

	public BaseSectionedAdapter() {
		sections = new ArrayList<Section>();
	}

	public void addItemForSection(Object object, int section) {
		if (section < 0 || section > sections.size()) {
			throw new IllegalArgumentException("section is less than 0 or has not been created yet.");
		}

		sections.get(section).addItem(object);

	}

	public int createNewSection(String title, boolean shouldDisplayHeader) {
		Section s = new Section();
		s.setSectionTitle(title);
		s.setShouldDisplayHeader(shouldDisplayHeader);
		sections.add(s);

		return sections.size();
	}

	public int getTotalItemCount() {
		int count = 0;
		for (Section section : sections) {
			count += section.getDataCount();
		}

		return count;
	}

	public int getSectionCount() {
		return sections.size();
	}

	public int getCountForSection(int section) {
		if (section < 0 || section > sections.size()) {
			throw new IllegalArgumentException("section is less than 0 or has not been created yet.");
		}

		return sections.get(section).getDataCount();
	}

	public Object getItem(int section, int position) {
		if (section < 0 || section >= sections.size()) {
			throw new IllegalArgumentException("section is less than 0 or has not been created yet.");
		}

		Section s = sections.get(section);
		if (position < 0 || position >= s.getDataCount()) {
			throw new IllegalArgumentException("position is less than 0 or greater than section data count.");
		}

		return s.getData().get(position);
	}

	public Section getSection(int section) {
		if (section < 0 || section >= sections.size()) {
			throw new IllegalArgumentException("section is less than 0 or has not been created yet.");
		}

		return sections.get(section);
	}

	public abstract long getItemId(int section, int position);

	public abstract View getViewForSection(int section, int position, View convertView, ViewGroup parent);

	public abstract View getHeaderViewForSection(int section, View convertView, ViewGroup parent);

}
