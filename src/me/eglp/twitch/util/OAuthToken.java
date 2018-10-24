package me.eglp.twitch.util;

import me.mrletsplay.mrcore.json.JSONObject;

public class OAuthToken {
	
	private String accessToken, refreshToken, scope, tokenType;
	private long expiresIn;

	public OAuthToken(String accessToken, String refreshToken, long expiresIn, String scope, String tokenType) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
		this.scope = scope;
		this.tokenType = tokenType;
	}

	public OAuthToken(JSONObject raw) {
		this.accessToken = raw.getString("access_token");
		this.refreshToken = raw.has("refresh_token") ? raw.getString("refresh_token") : null;
		this.expiresIn = raw.getLong("expires_in");
		this.scope = raw.has("scope") ? raw.getString("scope") : null;
		this.tokenType = raw.getString("token_type");
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public long getExpiresIn() {
		return expiresIn;
	}
	
	public String getScope() {
		return scope;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
}
