package org.king.apps.lunchvote.sockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.king.apps.lunchvote.utils.Timer;

@ServerEndpoint("/socket/timer/{roomId}")
public class TimerSocket {
	
	public static Map<String, Set<Session>> sessions = Collections.synchronizedMap(new HashMap<String, Set<Session>>());
	private static Map<String, Timer> timers = Collections.synchronizedMap(new HashMap<String, Timer>());
	
	
	@OnOpen
	public void onOpen(@PathParam("roomId") String roomId, Session s) throws IOException {
		System.out.println("Adding session: "+s.getId());
		
		Set<Session> sessionSet = sessions.get(roomId);
		
		if(sessionSet == null) {
			sessionSet = new HashSet<Session>();
			sessions.put(roomId, sessionSet);
		}
		
		sessionSet.add(s);
		
		if(!timers.containsKey(roomId)) {
			Timer t = new Timer(roomId);
			new Thread(t).start();
			timers.put(roomId, t);
		}
		
		System.out.println("Size for room: "+roomId+" : "+sessionSet.size());
	}
	
	@OnClose
	public void onClose(@PathParam("roomId") String roomId, Session s) {
		
	}
	
	@OnError
	public void onError(Session s, Throwable e) {
		System.out.println("Error occurred: "+s.getId());
		e.printStackTrace();
	}

}
