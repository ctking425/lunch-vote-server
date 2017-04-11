package org.king.apps.lunchvote.utils;

import javax.websocket.Session;

import org.king.apps.lunchvote.models.Data;
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
				
				Data d = new Data(String.format("%d:%02d", (current/60), (current%60)));
				
				System.out.println(roomId+" countdown: "+d);
				
				for(Session s : TimerSocket.sessions.get(roomId)) {
					if(s.isOpen()) s.getBasicRemote().sendText(Serializer.toJson(d));
				}
				current--;
				Thread.sleep(1000);
			} catch (Exception e) {
				continue;
			}
		}
	}

}
