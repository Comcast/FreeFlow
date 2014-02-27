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
package com.comcast.freeflow.examples.artbook.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class DribbbleFeed {

	@Expose
	private String page;
	@Expose
	private Integer per_page;
	@Expose
	private Integer pages;
	@Expose
	private Integer total;
	@Expose
	private List<Shot> shots = new ArrayList<Shot>();

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public DribbbleFeed withPage(String page) {
		this.page = page;
		return this;
	}

	public Integer getPer_page() {
		return per_page;
	}

	public void setPer_page(Integer per_page) {
		this.per_page = per_page;
	}

	public DribbbleFeed withPer_page(Integer per_page) {
		this.per_page = per_page;
		return this;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public DribbbleFeed withPages(Integer pages) {
		this.pages = pages;
		return this;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public DribbbleFeed withTotal(Integer total) {
		this.total = total;
		return this;
	}

	public List<Shot> getShots() {
		return shots;
	}

	public void setShots(List<Shot> shots) {
		this.shots = shots;
	}

	public DribbbleFeed withShots(List<Shot> shots) {
		this.shots = shots;
		return this;
	}

}
