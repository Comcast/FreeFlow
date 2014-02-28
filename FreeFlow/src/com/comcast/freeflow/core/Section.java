/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.comcast.freeflow.core;

import java.util.ArrayList;
import java.util.List;

public class Section {

	protected List<Object> data;
	protected Object headerData;
	protected int selectedIndex = 0;

	public Section() {
		data = new ArrayList<Object>();
	}

	public List<Object> getData() {
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

	public void setData(List<Object> data) {
		this.data = data;
	}

	public void setSectionTitle(String sectionTitle) {
		this.headerData = new StringHolder(sectionTitle) ;
	}
	
	public String getSectionTitle() {
		return headerData.toString();
	}
	
	public void setHeaderData(Object headerData){
		this.headerData = headerData;
	}
	
	public Object getHeaderData(){
		return headerData;
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
	
	public class StringHolder{
		public String str;
		public StringHolder(String str){
			this.str = str;
		}
		
		@Override
		public String toString(){
			return str;
		}
	}

}
