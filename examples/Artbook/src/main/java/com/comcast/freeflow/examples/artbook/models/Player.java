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

public class Player {

	@Expose
	private Integer id;
	@Expose
	private String name;
	@Expose
	private String location;
	@Expose
	private Integer followers_count;
	@Expose
	private Integer draftees_count;
	@Expose
	private Integer likes_count;
	@Expose
	private Integer likes_received_count;
	@Expose
	private Integer comments_count;
	@Expose
	private Integer comments_received_count;
	@Expose
	private Integer rebounds_count;
	@Expose
	private Integer rebounds_received_count;
	@Expose
	private String url;
	@Expose
	private String avatar_url;
	@Expose
	private String username;
	@Expose
	private String twitter_screen_name;
	@Expose
	private String website_url;
	@Expose
	private Integer drafted_by_player_id;
	@Expose
	private Integer shots_count;
	@Expose
	private Integer following_count;
	@Expose
	private String created_at;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Player withId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Player withName(String name) {
		this.name = name;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Player withLocation(String location) {
		this.location = location;
		return this;
	}

	public Integer getFollowers_count() {
		return followers_count;
	}

	public void setFollowers_count(Integer followers_count) {
		this.followers_count = followers_count;
	}

	public Player withFollowers_count(Integer followers_count) {
		this.followers_count = followers_count;
		return this;
	}

	public Integer getDraftees_count() {
		return draftees_count;
	}

	public void setDraftees_count(Integer draftees_count) {
		this.draftees_count = draftees_count;
	}

	public Player withDraftees_count(Integer draftees_count) {
		this.draftees_count = draftees_count;
		return this;
	}

	public Integer getLikes_count() {
		return likes_count;
	}

	public void setLikes_count(Integer likes_count) {
		this.likes_count = likes_count;
	}

	public Player withLikes_count(Integer likes_count) {
		this.likes_count = likes_count;
		return this;
	}

	public Integer getLikes_received_count() {
		return likes_received_count;
	}

	public void setLikes_received_count(Integer likes_received_count) {
		this.likes_received_count = likes_received_count;
	}

	public Player withLikes_received_count(Integer likes_received_count) {
		this.likes_received_count = likes_received_count;
		return this;
	}

	public Integer getComments_count() {
		return comments_count;
	}

	public void setComments_count(Integer comments_count) {
		this.comments_count = comments_count;
	}

	public Player withComments_count(Integer comments_count) {
		this.comments_count = comments_count;
		return this;
	}

	public Integer getComments_received_count() {
		return comments_received_count;
	}

	public void setComments_received_count(Integer comments_received_count) {
		this.comments_received_count = comments_received_count;
	}

	public Player withComments_received_count(Integer comments_received_count) {
		this.comments_received_count = comments_received_count;
		return this;
	}

	public Integer getRebounds_count() {
		return rebounds_count;
	}

	public void setRebounds_count(Integer rebounds_count) {
		this.rebounds_count = rebounds_count;
	}

	public Player withRebounds_count(Integer rebounds_count) {
		this.rebounds_count = rebounds_count;
		return this;
	}

	public Integer getRebounds_received_count() {
		return rebounds_received_count;
	}

	public void setRebounds_received_count(Integer rebounds_received_count) {
		this.rebounds_received_count = rebounds_received_count;
	}

	public Player withRebounds_received_count(Integer rebounds_received_count) {
		this.rebounds_received_count = rebounds_received_count;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Player withUrl(String url) {
		this.url = url;
		return this;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public Player withAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Player withUsername(String username) {
		this.username = username;
		return this;
	}

	public String getTwitter_screen_name() {
		return twitter_screen_name;
	}

	public void setTwitter_screen_name(String twitter_screen_name) {
		this.twitter_screen_name = twitter_screen_name;
	}

	public Player withTwitter_screen_name(String twitter_screen_name) {
		this.twitter_screen_name = twitter_screen_name;
		return this;
	}

	public String getWebsite_url() {
		return website_url;
	}

	public void setWebsite_url(String website_url) {
		this.website_url = website_url;
	}

	public Player withWebsite_url(String website_url) {
		this.website_url = website_url;
		return this;
	}

	public Integer getDrafted_by_player_id() {
		return drafted_by_player_id;
	}

	public void setDrafted_by_player_id(Integer drafted_by_player_id) {
		this.drafted_by_player_id = drafted_by_player_id;
	}

	public Player withDrafted_by_player_id(Integer drafted_by_player_id) {
		this.drafted_by_player_id = drafted_by_player_id;
		return this;
	}

	public Integer getShots_count() {
		return shots_count;
	}

	public void setShots_count(Integer shots_count) {
		this.shots_count = shots_count;
	}

	public Player withShots_count(Integer shots_count) {
		this.shots_count = shots_count;
		return this;
	}

	public Integer getFollowing_count() {
		return following_count;
	}

	public void setFollowing_count(Integer following_count) {
		this.following_count = following_count;
	}

	public Player withFollowing_count(Integer following_count) {
		this.following_count = following_count;
		return this;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public Player withCreated_at(String created_at) {
		this.created_at = created_at;
		return this;
	}

}
