package org.king.apps.lunchvote.utils;

import javax.websocket.Session;

import org.king.apps.lunchvote.controllers.RoomController;
import org.king.apps.lunchvote.models.Data;
import org.king.apps.lunchvote.models.RoomState;
import org.king.apps.lunchvote.sockets.TimerSocket;

public class Timer implements Runnable {
	
	private String roomId;
	private int time = 300;
	private RoomState callback;
	
	public Timer(String roomId, int time, RoomState callback) {
		this.roomId = roomId;
		this.time = time;
		this.callback = callback;
	}

	@Override
	public void run() {
		int current = time;
		while(current >= 0) {
			Data d = new Data(String.format("%d:%02d", (current/60), (current%60)));
			
			try {
				for(Session s : TimerSocket.sessions.get(roomId)) {
					if(s != null && s.isOpen()) s.getBasicRemote().sendText(Serializer.toJson(d));
				}
			} catch(Exception e) {
				//Do Nothing
			}
			
			current--;
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				continue;
			}
		}
		
		RoomController roomCtrl = new RoomController();
		
		try {
			switch(callback) {
			case Nominations:
				roomCtrl.startNominations(roomId);
				break;
			case Voting:
				roomCtrl.startVotes(roomId);
				break;
			case Complete:
				roomCtrl.endVotes(roomId);
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
