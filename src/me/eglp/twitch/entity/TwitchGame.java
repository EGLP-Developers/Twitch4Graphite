package me.eglp.twitch.entity;

import me.eglp.twitch.TwitchAPI;
import me.mrletsplay.mrcore.json.JSONObject;

public class TwitchGame {

	private TwitchAPI twitch;
	private String id, name, boxArtURL;
	
	public TwitchGame(TwitchAPI twitch, JSONObject raw) {
		this.twitch = twitch;
		this.id = raw.getString("id");
		this.name = raw.getString("name");
		this.boxArtURL = raw.getString("box_art_url");
	}
	
	public TwitchAPI getTwitch() {
		return twitch;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getBoxArtURL(int width, int height) {
		return boxArtURL.replace("{width}", String.valueOf(width)).replace("{height}", String.valueOf(height));
	}
	
}
