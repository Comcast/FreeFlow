package org.freeflow.core;

import java.util.ArrayList;

public class Section {

	private ArrayList<Object> data;
	private String sectionTitle;
	private boolean displayHeader = false;
	private int selectedIndex = 0;

	public Section() {
		data = new ArrayList<Object>();
	}

	public ArrayList<Object> getData() {
		return data;
	}

	public int getDataCount() {
		return data.size();
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setData(ArrayList<Object> data) {
		this.data = data;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public void addItem(Object item) {
		data.add(item);
	}

	public void setShouldDisplayHeader(boolean displayHeader) {
		this.displayHeader = displayHeader;
	}

	public boolean shouldDisplayHeader() {
		return displayHeader;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

}
