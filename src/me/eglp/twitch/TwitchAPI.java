package me.eglp.twitch;

import me.eglp.twitch.entity.TwitchGame;
import me.eglp.twitch.entity.TwitchStream;
import me.eglp.twitch.entity.TwitchUser;
import me.eglp.twitch.util.OAuthToken;
import me.eglp.twitch.util.TwitchEndpoint;
import me.mrletsplay.mrcore.http.HttpGet;
import me.mrletsplay.mrcore.http.HttpPost;
import me.mrletsplay.mrcore.http.HttpRequest;
import me.mrletsplay.mrcore.misc.JSON.JSONArray;
import me.mrletsplay.mrcore.misc.JSON.JSONObject;

public class TwitchAPI {

	public static final String
		ENDPOINT = "https://api.twitch.tv/helix/",
		OAUTH_ENDPOINT = "https://id.twitch.tv/oauth2/";
	
	private String clientID, clientSecret;
	private OAuthToken token;
	
	public TwitchAPI(String clientID, String clientSecret) {
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.token = new OAuthToken(makePostRequest(TwitchEndpoint.OAUTH_TOKEN, "client_id", clientID, "client_secret", clientSecret, "grant_type", "client_credentials"));
	}
	
	public String getClientID() {
		return clientID;
	}
	
	public String getClientSecret() {
		return clientSecret;
	}
	
	public OAuthToken getToken() {
		return token;
	}
	
	public TwitchUser getUserByName(String name) {
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_USERS, "login", name).getJSONArray("data");
		if(arr.isEmpty()) return null;
		return new TwitchUser(this, arr.getJSONObject(0));
	}
	
	public TwitchUser getUserByID(String id) {
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_USERS, "id", id).getJSONArray("data");
		if(arr.isEmpty()) return null;
		return new TwitchUser(this, arr.getJSONObject(0));
	}
	
	public TwitchStream getStreamByUserID(String id) {
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_STREAMS, "user_id", id).getJSONArray("data");
		if(arr.isEmpty()) return null;
		return new TwitchStream(this, arr.getJSONObject(0));
	}
	
	public TwitchGame getGameByID(String id) {
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_GAMES, "id", id).getJSONArray("data");
		if(arr.isEmpty()) return null;
		return new TwitchGame(this, arr.getJSONObject(0));
	}
	
	public JSONObject makeGetRequest(TwitchEndpoint endpoint, String... queryParams) {
		HttpGet r = HttpRequest.createGet(endpoint.getURL());
		if(token != null) r.setHeaderParameter("Authorization", "Bearer " + token.getAccessToken());
		for(int i = 0; i < queryParams.length; i+=2) {
			r.setQueryParameter(queryParams[i], queryParams[i+1]);
		}
		return r.execute().asJSONObject();
	}
	
	public JSONObject makePostRequest(TwitchEndpoint endpoint, String... queryParams) {
		HttpPost r = HttpRequest.createPost(endpoint.getURL());
		if(token != null) r.setHeaderParameter("Authorization", "Bearer " + token.getAccessToken());
		for(int i = 0; i < queryParams.length; i+=2) {
			r.setQueryParameter(queryParams[i], queryParams[i+1]);
		}
		return r.execute().asJSONObject();
	}
	
}
