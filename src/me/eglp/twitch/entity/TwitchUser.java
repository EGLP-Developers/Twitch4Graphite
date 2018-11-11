package me.eglp.twitch.entity;

import me.eglp.twitch.TwitchAPI;
import me.mrletsplay.mrcore.json.JSONObject;

public class TwitchUser {

	private TwitchAPI twitch;
	private String id, login, displayName, type, broadcasterType, description, profileImageURL, offlineImageURL;
	private long viewCount;
	
	public TwitchUser(TwitchAPI twitch, JSONObject raw) {
		this.twitch = twitch;
		this.id = raw.getString("id");
		this.login = raw.getString("login");
		this.displayName = raw.getString("display_name");
		this.type = raw.getString("type");
		this.broadcasterType = raw.getString("broadcaster_type");
		this.description = raw.getString("description");
		this.profileImageURL = raw.getString("profile_image_url");
		this.offlineImageURL = raw.getString("offline_image_url");
		this.viewCount = raw.getLong("view_count");
	}
	
	public TwitchAPI getTwitch() {
		return twitch;
	}
	
	public String getID() {
		return id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getType() {
		return type;
	}
	
	public String getBroadcasterType() {
		return broadcasterType;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getProfileImageURL() {
		return profileImageURL;
	}
	
	public String getOfflineImageURL() {
		return offlineImageURL;
	}
	
	public long getViewCount() {
		return viewCount;
	}
	
	public TwitchStream getStream() {
		return twitch.getStreamByUserID(id);
	}
	
}
