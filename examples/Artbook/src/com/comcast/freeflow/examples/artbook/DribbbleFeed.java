package com.comcast.freeflow.examples.artbook;

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
