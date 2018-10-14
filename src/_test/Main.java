package _test;

import java.time.ZoneOffset;

import me.eglp.twitch.TwitchAPI;
import me.eglp.twitch.entity.TwitchStream;
import me.eglp.twitch.entity.TwitchUser;

public class Main {

	public static void main(String[] args) {
		TwitchAPI api = new TwitchAPI("u0y2i2tohgpia7iu4t4p17sacy7nd5", "bixsnkd6oglwe185so6hdwm709c9gp");
		System.out.println(api.getToken().getAccessToken());
//		api.getUser("4x5d4fuuqtvgzxxvtsdcopnduqtgi2");
		TwitchUser user = api.getUserByName("twitchpresents");
		TwitchStream str = user.getStream();
		System.out.println(str.getTitle());
		System.out.println(str.getStartedAt().atOffset(ZoneOffset.UTC).toString());
		System.out.println(str.getThumbnailURL(1920, 1080));
		System.out.println(str.getViewerCount());
		System.out.println(user.getViewCount());
		System.out.println(str.getGame().getBoxArtURL(1080, 1920));
	}
	
}
