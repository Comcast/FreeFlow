package com.comcast.freeflow.core;

import java.util.ArrayList;

public class Section {

	protected ArrayList<Object> data;
	protected String sectionTitle;
	protected int selectedIndex = 0;

	public Section() {
		data = new ArrayList<Object>();
	}

	private ArrayList<Object> getData() {
		return data;
	}
	
	public void clearData() {
		data.clear();
	}
	
	public Object getDataAtIndex(int index){
		return data.get(index);
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

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

}
