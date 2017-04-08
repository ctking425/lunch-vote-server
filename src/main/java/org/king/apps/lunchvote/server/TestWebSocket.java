package org.king.apps.lunchvote.server;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/testendpoint")
public class TestWebSocket {
	
	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void onOpen(Session s) {
		System.out.println("Adding session: "+s.getId());
		sessions.add(s);
	}
	
	@OnClose
	public void onClose(Session s){
		System.out.println("Removing session: "+s.getId());
		sessions.remove(s);
	}
	
	@OnMessage
	public void onMessage(String message, Session s) throws IOException{
		System.out.println("Message recieved from: "+s.getId());
		for(Session session : sessions) {
			System.out.println("Sending to: "+session.getId());
			session.getBasicRemote().sendText(message);
		}
	}

	@OnError
	public void onError(Throwable e){
		e.printStackTrace();
	}

}
