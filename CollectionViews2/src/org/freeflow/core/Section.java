package org.freeflow.core;

import java.util.ArrayList;

public class Section {

	private ArrayList<Object> data;
	private String sectionTitle;
	
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
	
	public void addItem(Object item){
		data.add(item);
	}
	
	

}
