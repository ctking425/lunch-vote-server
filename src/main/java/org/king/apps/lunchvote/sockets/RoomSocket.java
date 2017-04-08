package org.king.apps.lunchvote.sockets;

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

@ServerEndpoint("/socket/room")
public class RoomSocket {
	
	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
	
	public RoomSocket() {
		
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				System.out.println("Starting runner");
				try {
					int i = 0;
					while(true) {
						System.out.println("Looping");
						i++;
						if(sessions.size() > 0) {
							System.out.println("Inside: "+sessions.size());
							for(Session s : sessions) {
								System.out.println("Sending to: "+s.getId());
								s.getBasicRemote().sendText("{\"data\":\"Message "+i+"\"}");
							}
						}
						
						Thread.sleep(2000);
					}
				} catch(Exception e) {
					System.out.println("Stopping runner");
				}
			}
		};
		
		new Thread(runner).start();
	}
	
	@OnOpen
	public void onOpen(Session s) throws IOException {
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
			session.getBasicRemote().sendText("{\"from\":\""+s.getId()+"\", \"data\":\""+message+"\"");
		}
	}

	@OnError
	public void onError(Session s, Throwable e){
		System.out.println("Error occurred: "+s.getId());
		e.printStackTrace();
	}

}
