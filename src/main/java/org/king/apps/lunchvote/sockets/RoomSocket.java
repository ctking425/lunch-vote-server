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
import org.king.apps.lunchvote.exception.RoomNotFoundException;
import org.king.apps.lunchvote.models.Message;
import org.king.apps.lunchvote.models.Room;
import org.king.apps.lunchvote.models.Votable;
import org.king.apps.lunchvote.utils.Serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ServerEndpoint("/socket/room/{roomId}")
public class RoomSocket {
	
	public static final String TYPE = "type";
	public static final String DATA = "data";
	
	public static final String ROOM_INIT = "ROOM_INIT";
	public static final String ROOM_STATE = "ROOM_STATE";
	public static final String NOM_ADD = "NOMINATION";
	public static final String VOTE_ADD = "VOTE";
	public static final String VETO_ADD = "VETO";
	
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
		
		Room room;
		try {
			room = roomCtrl.getRoom(roomId);
			String out = Serializer.toJson(new Message<Room>(ROOM_INIT, room));
			System.out.println("Room Out: "+out);
			s.getBasicRemote().sendText(out);
		} catch (RoomNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@OnClose
	public void onClose(@PathParam("roomId") String roomId, Session s) {
		System.out.println("Removing session: "+s.getId());
		if(sessions.containsKey(roomId)) sessions.get(roomId).remove(s);
		roomCtrl.removeRoom(roomId);
	}
	
	@OnMessage
	public void onMessage(@PathParam("roomId") String roomId, String message, Session s) throws IOException {
		System.out.println("Message recieved from: "+s.getId()+"\n"+message.toString());
		
		if(!s.getQueryString().startsWith("key=")) {
			return;
		}
		
		String userKey = s.getQueryString().substring(4);
		System.out.println(userKey);
		
		JsonObject obj = new JsonParser().parse(message).getAsJsonObject();
		String msgType = obj.get(TYPE).getAsString();
		
		try {
			switch(msgType) {
			case NOM_ADD:
				String nomName = obj.get(DATA).getAsJsonObject().get("name").getAsString();
				String nomDesc = obj.get(DATA).getAsJsonObject().get("description").getAsString();
				Votable nomination = roomCtrl.addNomination(roomId, userKey, nomName, nomDesc);
				if(nomination != null) {
					sendToAll(roomId, new Message<Votable>(NOM_ADD, nomination));
				} else {
					System.out.println("Nomination Failed");
				}
				break;
			case VOTE_ADD:
				String voteId = obj.get(DATA).getAsString();
				boolean successVote = roomCtrl.vote(roomId, userKey, voteId);
				if(successVote) {
					System.out.println("Successful Vote");
					sendToAll(roomId, new Message<String>(VOTE_ADD, voteId));
				} else {
					System.out.println("Vote Failed");
				}
				break;
			case VETO_ADD:
				String vetoId = obj.get(DATA).getAsString();
				boolean successVeto = roomCtrl.veto(roomId, userKey, vetoId);
				if(successVeto) {
					System.out.println("Successful Veto");
					sendToAll(roomId, new Message<String>(VETO_ADD, vetoId));
				} else {
					System.out.println("Veto Failed");
				}
				break;
			default:
				break;
			}
		} catch(RoomNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	@OnError
	public void onError(Session s, Throwable e){
		System.out.println("Error occurred: "+s.getId());
	}
	
	public static void sendToAll(String roomId, Message<?> message) throws IOException {
		if(sessions.containsKey(roomId)) {
			for(Session s : sessions.get(roomId)) {
				if(s.isOpen()) s.getBasicRemote().sendText(Serializer.toJson(message));
			}
		}
	}
	
	public static boolean isRoomEmpty(String roomId) {
		if(sessions.containsKey(roomId)) {
			return sessions.get(roomId).isEmpty();
		}
		return true;
	}
}
