package org.king.apps.lunchvote.sockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.king.apps.lunchvote.singletons.SessionManager;

@ServerEndpoint("/socket/timer/{roomId}")
public class TimerSocket {
	
	public static final String SOCKET_NAME = "TimerSocket";
	
	public static Map<String, Set<Session>> sessions = Collections.synchronizedMap(new HashMap<String, Set<Session>>());
	
	@OnOpen
	public void onOpen(@PathParam("roomId") String roomId, Session s) throws IOException {
		SessionManager.getInstance().addSession(SOCKET_NAME, roomId, s);
	}
	
	@OnClose
	public void onClose(@PathParam("roomId") String roomId, Session s) {
		SessionManager.getInstance().removeSession(SOCKET_NAME, roomId, s);
	}
	
	@OnError
	public void onError(Session s, Throwable e) {
		System.out.println("Error occurred: "+s.getId());
		e.printStackTrace();
	}

}
