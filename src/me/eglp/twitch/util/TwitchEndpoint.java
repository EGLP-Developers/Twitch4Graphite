package me.eglp.twitch.util;

import me.eglp.twitch.TwitchAPI;

public enum TwitchEndpoint {
	
	GET_STREAMS(TwitchAPI.ENDPOINT + "streams"),
	GET_USERS(TwitchAPI.ENDPOINT + "users"),
	GET_GAMES(TwitchAPI.ENDPOINT + "games"),
	
	OAUTH_TOKEN(TwitchAPI.OAUTH_ENDPOINT + "token")
	;
	
	public final String url;
	
	private TwitchEndpoint(String url) {
		this.url = url;
	}
	
	public String getURL() {
		return url;
	}
	
}
