package org.king.apps.lunchvote.sockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.king.apps.lunchvote.controllers.RoomController;
import org.king.apps.lunchvote.models.Room;
import org.king.apps.lunchvote.models.Votable;
import org.king.apps.lunchvote.utils.Serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ServerEndpoint("/socket/room/{roomId}")
public class RoomSocket {
	
	private static final String ROOM_INIT = "ROOM_INIT";
	private static final String NOM_ADD = "NOMINATION";
	private static final String VOTE_ADD = "VOTE";
	private static final String VETO_ADD = "VETO";
	
	public static Map<String, Set<Session>> sessions = Collections.synchronizedMap(new HashMap<String, Set<Session>>());
	
	private RoomController roomCtrl;
	
	public RoomSocket() {
		super();
		roomCtrl = new RoomController();
	}
	
	@OnOpen
	public void onOpen(@PathParam("roomId") String roomId, Session s) throws IOException {
		System.out.println("Adding session: "+s.getId());
		
		Set<Session> sessionSet = sessions.get(roomId);
		
		if(sessionSet == null) {
			sessionSet = Collections.synchronizedSet(new HashSet<Session>());
			sessions.put(roomId, sessionSet);
		}
		
		sessionSet.add(s);
		
		//Need to add the user to the room, need to send the current room data to the client
		Room room = roomCtrl.addUserToRoom(roomId, s.getId());
		String out = Serializer.toJson(new Message<Room>(ROOM_INIT, room));
		System.out.println("Room Out: "+out);
		s.getBasicRemote().sendText(out);
	}
	
	@OnClose
	public void onClose(@PathParam("roomId") String roomId, Session s) {
		System.out.println("Removing session: "+s.getId());
		sessions.get(roomId).remove(s);
		roomCtrl.removeUserFromRoom(roomId, s.getId());
	}
	
	@OnMessage
	public void onMessage(@PathParam("roomId") String roomId, String message, Session s) throws IOException {
		System.out.println("Message recieved from: "+s.getId()+"\n"+message.toString());
		JsonObject obj = new JsonParser().parse(message).getAsJsonObject();
		
		String msgType = obj.get("type").getAsString();
		
		switch(msgType) {
		case NOM_ADD:
			String nomName = obj.get("data").getAsJsonObject().get("name").getAsString();
			String nomDesc = obj.get("data").getAsJsonObject().get("description").getAsString();
			Votable nomination = roomCtrl.addNomination(roomId, nomName, nomDesc);
			sendToAll(roomId, new Message<Votable>(NOM_ADD, nomination));
			break;
		default:
			break;
		}
	}

	@OnError
	public void onError(Session s, Throwable e){
		System.out.println("Error occurred: "+s.getId());
		e.printStackTrace();
	}
	
	private void sendToAll(String roomId, Message<?> message) throws IOException {
		for(Session s : sessions.get(roomId)) {
			if(s.isOpen()) s.getBasicRemote().sendText(Serializer.toJson(message));
		}
	}
	
	class Message<T> {
		private String type;
		private T data;
		
		public Message() {
			super();
		}
		
		public Message(String type, T data) {
			super();
			this.type = type;
			this.data = data;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public T getData() {
			return data;
		}
		
		public void setData(T data) {
			this.data = data;
		}
	}

}
