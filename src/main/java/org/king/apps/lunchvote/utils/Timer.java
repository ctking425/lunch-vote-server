package org.king.apps.lunchvote.utils;

import javax.websocket.Session;

import org.king.apps.lunchvote.sockets.TimerSocket;

public class Timer implements Runnable {
	
	private String roomId;
	private static final int TIME = 300;
	
	public Timer(String roomId) {
		this.roomId = roomId;
	}

	@Override
	public void run() {
		int current = TIME;
		while(current >= 0) {
			try {
				
				String t = String.format("{\"data\":\"%d:%02d\"}", (current/60), (current%60));
				
				System.out.println(roomId+" countdown: "+t);
				
				for(Session s : TimerSocket.sessions.get(roomId)) {
					if(s.isOpen()) s.getBasicRemote().sendText(t);
				}
				current--;
				Thread.sleep(1000);
			} catch (Exception e) {
				continue;
			}
		}
	}

}
