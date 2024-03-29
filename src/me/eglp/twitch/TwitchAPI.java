package me.eglp.twitch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.eglp.twitch.entity.TwitchGame;
import me.eglp.twitch.entity.TwitchStream;
import me.eglp.twitch.entity.TwitchUser;
import me.eglp.twitch.ratelimit.Ratelimiter;
import me.eglp.twitch.util.OAuthToken;
import me.eglp.twitch.util.TwitchEndpoint;
import me.mrletsplay.mrcore.http.HttpGet;
import me.mrletsplay.mrcore.http.HttpPost;
import me.mrletsplay.mrcore.http.HttpRequest;
import me.mrletsplay.mrcore.http.HttpResult;
import me.mrletsplay.mrcore.json.JSONArray;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.misc.FriendlyException;

public class TwitchAPI {

	public static final String
		ENDPOINT = "https://api.twitch.tv/helix/",
		OAUTH_ENDPOINT = "https://id.twitch.tv/oauth2/";
	
	private static final Pattern
		VALID_USER_NAME = Pattern.compile("[a-zA-Z0-9_-]++");
	
	private String clientID, clientSecret;
	private OAuthToken token;
	
	public TwitchAPI(String clientID, String clientSecret) {
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.token = new OAuthToken(makePostRequest(TwitchEndpoint.OAUTH_TOKEN, false, "client_id", clientID, "client_secret", clientSecret, "grant_type", "client_credentials"));
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
		if(!VALID_USER_NAME.matcher(name).matches()) return null;
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_USERS, "login", name).getJSONArray("data");
		if(arr.isEmpty()) return null;
		return new TwitchUser(this, arr.getJSONObject(0));
	}
	
	public List<TwitchUser> getUsersByNames(String... names) {
		List<String> params = new ArrayList<>();
		Arrays.stream(names)
			.filter(name -> VALID_USER_NAME.matcher(name).matches())
			.forEach(name -> {
				params.add("login");
				params.add(name);
			});
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_USERS, params.toArray(new String[params.size()])).getJSONArray("data");
		if(arr.isEmpty()) return Collections.emptyList();
		return arr.stream()
				.map(o -> new TwitchUser(this, (JSONObject) o))
				.collect(Collectors.toList());
	}
	
	public TwitchUser getUserByID(String id) {
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_USERS, "id", id).getJSONArray("data");
		if(arr.isEmpty()) return null;
		return new TwitchUser(this, arr.getJSONObject(0));
	}
	
	public List<TwitchUser> getUsersByIDs(String... ids) {
		List<String> params = new ArrayList<>();
		Arrays.stream(ids)
			.forEach(name -> {
				params.add("id");
				params.add(name);
			});
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_USERS, params.toArray(new String[params.size()])).getJSONArray("data");
		if(arr.isEmpty()) return Collections.emptyList();
		return arr.stream()
				.map(o -> new TwitchUser(this, (JSONObject) o))
				.collect(Collectors.toList());
	}
	
	public TwitchStream getStreamByUserID(String id) {
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_STREAMS, "user_id", id).getJSONArray("data");
		if(arr.isEmpty()) return null;
		return new TwitchStream(this, arr.getJSONObject(0));
	}
	
	public List<TwitchStream> getStreamsByUserIDs(String... ids) {
		List<String> params = new ArrayList<>();
		Arrays.stream(ids)
			.forEach(name -> {
				params.add("user_id");
				params.add(name);
			});
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_STREAMS, params.toArray(new String[params.size()])).getJSONArray("data");
		if(arr.isEmpty()) return Collections.emptyList();
		return arr.stream()
				.map(o -> new TwitchStream(this, (JSONObject) o))
				.collect(Collectors.toList());
	}
	
	public TwitchGame getGameByID(String id) {
		JSONArray arr = makeGetRequest(TwitchEndpoint.GET_GAMES, "id", id).getJSONArray("data");
		if(arr.isEmpty()) return null;
		return new TwitchGame(this, arr.getJSONObject(0));
	}
	
	private void ensureTokenValid(boolean forceRefresh) {
		if(forceRefresh || token.getExpiresAt() - System.currentTimeMillis() < 10000)
			this.token = new OAuthToken(makePostRequest(TwitchEndpoint.OAUTH_TOKEN, false, "client_id", clientID, "client_secret", clientSecret, "grant_type", "client_credentials"));
	}
	
	public synchronized JSONObject makeGetRequest(TwitchEndpoint endpoint, String... queryParams) {
		return makeGetRequest(endpoint, true, queryParams);
	}
	
	public synchronized JSONObject makeGetRequest(TwitchEndpoint endpoint, boolean tokenCheck, String... queryParams) {
		if(tokenCheck) ensureTokenValid(false);
		Ratelimiter.waitForRatelimitIfNeeded();
		HttpGet r = HttpRequest.createGet(endpoint.getURL());
		
		r.setHeaderParameter("Client-ID", clientID);
		if(token != null) r.setHeaderParameter("Authorization", "Bearer " + token.getAccessToken());
		for(int i = 0; i < queryParams.length; i+=2) {
			r.addQueryParameter(queryParams[i], queryParams[i+1]);
		}
		
		try {
			HttpResult res = r.execute();
			
			if(res.getHeaderFields().containsKey("ratelimit-reset")) {
				String ratelimitReset = res.getHeaderFields().get("ratelimit-reset").get(0);
				String ratelimitRemaining = res.getHeaderFields().get("ratelimit-remaining").get(0);
				Ratelimiter.setRatelimitReset(ratelimitRemaining.equals("0"), Long.parseLong(ratelimitReset));
			}
			
			return res.asJSONObject();
		}catch(Exception e) {
			if(!tokenCheck) throw new FriendlyException("Request failed", e);
			ensureTokenValid(true);
			return makeGetRequest(endpoint, false, queryParams);
		}
	}
	
	public synchronized JSONObject makePostRequest(TwitchEndpoint endpoint, String... queryParams) {
		return makePostRequest(endpoint, true, queryParams);
	}
	
	public synchronized JSONObject makePostRequest(TwitchEndpoint endpoint, boolean tokenCheck, String... queryParams) {
		if(tokenCheck) ensureTokenValid(false);
		Ratelimiter.waitForRatelimitIfNeeded();
		HttpPost r = HttpRequest.createPost(endpoint.getURL());

		r.setHeaderParameter("Client-ID", clientID);
		if(token != null) r.setHeaderParameter("Authorization", "Bearer " + token.getAccessToken());
		for(int i = 0; i < queryParams.length; i+=2) {
			r.setQueryParameter(queryParams[i], queryParams[i+1]);
		}
		
		try {
			HttpResult res = r.execute();
			
			if(res.getHeaderFields().containsKey("ratelimit-reset")) {
				String ratelimitReset = res.getHeaderFields().get("ratelimit-reset").get(0);
				String ratelimitRemaining = res.getHeaderFields().get("ratelimit-remaining").get(0);
				Ratelimiter.setRatelimitReset(ratelimitRemaining.equals("0"), Long.parseLong(ratelimitReset));
			}
			
			return res.asJSONObject();
		}catch(Exception e) {
			if(!tokenCheck) throw e;
			ensureTokenValid(true);
			return makePostRequest(endpoint, false, queryParams);
		}
	}
	
}
