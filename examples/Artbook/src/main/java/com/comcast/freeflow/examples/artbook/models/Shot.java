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

import com.google.gson.annotations.Expose;

public class Shot extends Object {

	@Expose
	private Integer id;
	@Expose
	private String title;
	@Expose
	private Integer height;
	@Expose
	private Integer width;
	@Expose
	private Integer likes_count;
	@Expose
	private Integer comments_count;
	@Expose
	private Integer rebounds_count;
	@Expose
	private String url;
	@Expose
	private String short_url;
	@Expose
	private Integer views_count;
	@Expose
	private Integer rebound_source_id;
	@Expose
	private String image_url;
	@Expose
	private String image_teaser_url;
	@Expose
	private Player player;
	@Expose
	private String created_at;
	@Expose
	private String image_400_url;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Shot withId(Integer id) {
		this.id = id;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Shot withTitle(String title) {
		this.title = title;
		return this;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Shot withHeight(Integer height) {
		this.height = height;
		return this;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Shot withWidth(Integer width) {
		this.width = width;
		return this;
	}

	public Integer getLikes_count() {
		return likes_count;
	}

	public void setLikes_count(Integer likes_count) {
		this.likes_count = likes_count;
	}

	public Shot withLikes_count(Integer likes_count) {
		this.likes_count = likes_count;
		return this;
	}

	public Integer getComments_count() {
		return comments_count;
	}

	public void setComments_count(Integer comments_count) {
		this.comments_count = comments_count;
	}

	public Shot withComments_count(Integer comments_count) {
		this.comments_count = comments_count;
		return this;
	}

	public Integer getRebounds_count() {
		return rebounds_count;
	}

	public void setRebounds_count(Integer rebounds_count) {
		this.rebounds_count = rebounds_count;
	}

	public Shot withRebounds_count(Integer rebounds_count) {
		this.rebounds_count = rebounds_count;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Shot withUrl(String url) {
		this.url = url;
		return this;
	}

	public String getShort_url() {
		return short_url;
	}

	public void setShort_url(String short_url) {
		this.short_url = short_url;
	}

	public Shot withShort_url(String short_url) {
		this.short_url = short_url;
		return this;
	}

	public Integer getViews_count() {
		return views_count;
	}

	public void setViews_count(Integer views_count) {
		this.views_count = views_count;
	}

	public Shot withViews_count(Integer views_count) {
		this.views_count = views_count;
		return this;
	}

	public Integer getRebound_source_id() {
		return rebound_source_id;
	}

	public void setRebound_source_id(Integer rebound_source_id) {
		this.rebound_source_id = rebound_source_id;
	}

	public Shot withRebound_source_id(Integer rebound_source_id) {
		this.rebound_source_id = rebound_source_id;
		return this;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public Shot withImage_url(String image_url) {
		this.image_url = image_url;
		return this;
	}

	public String getImage_teaser_url() {
		return image_teaser_url;
	}

	public void setImage_teaser_url(String image_teaser_url) {
		this.image_teaser_url = image_teaser_url;
	}

	public Shot withImage_teaser_url(String image_teaser_url) {
		this.image_teaser_url = image_teaser_url;
		return this;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Shot withPlayer(Player player) {
		this.player = player;
		return this;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public Shot withCreated_at(String created_at) {
		this.created_at = created_at;
		return this;
	}

	public String getImage_400_url() {
		return image_400_url;
	}

	public void setImage_400_url(String image_400_url) {
		this.image_400_url = image_400_url;
	}

	public Shot withImage_400_url(String image_400_url) {
		this.image_400_url = image_400_url;
		return this;
	}
	
	@Override
	public boolean equals(Object other){
		if(! (other instanceof Shot)) return false;
		Shot oShot = (Shot)other;
		return oShot.image_url.equals(image_url) && (oShot.id == this.id);
	}

}
