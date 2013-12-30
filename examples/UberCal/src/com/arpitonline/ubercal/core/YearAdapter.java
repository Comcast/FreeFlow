package com.arpitonline.ubercal.core;

import org.freeflow.core.BaseSectionedAdapter;
import org.freeflow.core.ItemProxy;
import org.freeflow.core.Section;

import android.view.View;
import android.view.ViewGroup;

public class YearAdapter implements BaseSectionedAdapter {
	
	private YearSection yearSection;
	public YearAdapter(){
		yearSection = new YearSection();
	
	}
	
	@Override
	public long getItemId(int section, int position) {
		return section*100+position;
	}

	@Override
	public View getViewForSection(int section, int position, View convertView,
			ViewGroup parent) {
		
		Month m = (Month) yearSection.getData().get(position);
		return m.getView(parent); 
	}

	@Override
	public View getHeaderViewForSection(int section, View convertView,
			ViewGroup parent) {
		return null;
	}

	@Override
	public int getNumberOfSections() {
		return 1;
	}

	@Override
	public Section getSection(int index) {
		return yearSection;
	}
	

	public class YearSection extends Section{
		public YearSection(){
			super();
			for (int i=0; i < 12; i++){
				Month m = new Month(i);
				this.data.add(m);
			}
		}
	}


	@Override
	public Class[] getViewTypes() {
		return new Class[]{View.class};
	}

	@Override
	public Class getViewType(ItemProxy proxy) {
		return View.class;
	}

}

