package org.king.apps.lunchvote.sockets;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.king.apps.lunchvote.controllers.RoomController;
import org.king.apps.lunchvote.exception.RoomNotFoundException;
import org.king.apps.lunchvote.singletons.SessionManager;

@ServerEndpoint("/socket/room/{roomId}")
public class RoomSocket {
	
	public static final String SOCKET_NAME = "RoomSocket";
	
	private RoomController roomCtrl;
	
	public RoomSocket() {
		super();
		roomCtrl = new RoomController();
	}
	
	@OnOpen
	public void onOpen(@PathParam("roomId") String roomId, Session s) throws IOException {
		System.out.println("Adding session: "+s.getId());
		
		SessionManager.getInstance().addSession(SOCKET_NAME, roomId, s);
		roomCtrl.sendRoomInitialization(roomId, s);
	}
	
	@OnClose
	public void onClose(@PathParam("roomId") String roomId, Session s) {
		System.out.println("Removing session: "+s.getId());
		SessionManager.getInstance().removeSession(SOCKET_NAME, roomId, s);
	}
	
	@OnMessage
	public void onMessage(@PathParam("roomId") String roomId, String message, Session s) throws IOException {
		System.out.println("Message recieved from: "+s.getId()+"\n"+message.toString());
		
		if(!s.getQueryString().startsWith("key=")) {
			return;
		}
		
		String userKey = s.getQueryString().substring(4);
		
		try {
			roomCtrl.processMessage(roomId, userKey, message);
		} catch (RoomNotFoundException e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Session s, Throwable e){
		System.out.println("Error occurred: "+s.getId());
	}
}
