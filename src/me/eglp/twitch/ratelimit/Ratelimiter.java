package me.eglp.twitch.ratelimit;

public class Ratelimiter {
	
	private static boolean wait;
	private static long globalReset;
	
	public static void setRatelimitReset(boolean wait, long globalReset) {
		Ratelimiter.wait = wait;
		Ratelimiter.globalReset = globalReset;
	}
	
	public static synchronized void waitForRatelimitIfNeeded() {
		if(wait) {
			try {
				Thread.sleep(globalReset * 1000 - System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			wait = false;
		}
	}

}
