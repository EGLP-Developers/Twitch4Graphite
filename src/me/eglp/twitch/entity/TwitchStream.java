package me.eglp.twitch.entity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import me.eglp.twitch.TwitchAPI;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.misc.Complex;

public class TwitchStream {

	private TwitchAPI twitch;
	private String id, userID, gameID;
	private List<String> communityIDs;
	private String title;
	private long viewerCount;
	private Instant startedAt;
	private String language, thumbnailURL;
	
	public TwitchStream(TwitchAPI twitch, JSONObject raw) {
		this.twitch = twitch;
		this.id = raw.getString("id");
		this.userID = raw.getString("user_id");
		this.gameID = raw.getString("game_id");
		this.communityIDs = raw.has("community_ids") ? Complex.castList(raw.getJSONArray("community_ids"), String.class).get() : Collections.emptyList();
		this.title = raw.getString("title");
		this.viewerCount = raw.getLong("viewer_count");
		this.startedAt = Instant.parse(raw.getString("started_at"));
		this.language = raw.getString("language");
		this.thumbnailURL = raw.getString("thumbnail_url");
	}
	
	public TwitchAPI getTwitch() {
		return twitch;
	}
	
	public String getId() {
		return id;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public String getGameID() {
		return gameID;
	}
	
	public TwitchGame getGame() {
		if(gameID == null || gameID.isEmpty()) return null;
		return twitch.getGameByID(gameID);
	}
	
	public List<String> getCommunityIDs() {
		return communityIDs;
	}
	
	public String getTitle() {
		return title;
	}
	
	public long getViewerCount() {
		return viewerCount;
	}
	
	public Instant getStartedAt() {
		return startedAt;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getThumbnailURL(int width, int height) {
		return thumbnailURL.replace("{width}", String.valueOf(width)).replace("{height}", String.valueOf(height));
	}

}
