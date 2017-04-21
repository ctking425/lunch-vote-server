package org.king.apps.lunchvote.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import javax.websocket.Session;

import org.king.apps.lunchvote.exception.RoomNotFoundException;
import org.king.apps.lunchvote.models.Message;
import org.king.apps.lunchvote.models.Room;
import org.king.apps.lunchvote.models.RoomState;
import org.king.apps.lunchvote.models.User;
import org.king.apps.lunchvote.models.Votable;
import org.king.apps.lunchvote.singletons.CryptService;
import org.king.apps.lunchvote.singletons.RoomStore;
import org.king.apps.lunchvote.singletons.SessionManager;
import org.king.apps.lunchvote.singletons.TimerManager;
import org.king.apps.lunchvote.singletons.UserStore;
import org.king.apps.lunchvote.sockets.RoomSocket;
import org.king.apps.lunchvote.sockets.TimerSocket;
import org.king.apps.lunchvote.utils.Serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RoomController {
	
	public static final String TYPE = "type";
	public static final String DATA = "data";
	
	public static final String ROOM_INIT = "ROOM_INIT";
	public static final String ROOM_STATE = "ROOM_STATE";
	public static final String NOM_ADD = "NOMINATION";
	public static final String VOTE_ADD = "VOTE";
	public static final String VETO_ADD = "VETO";
	
	public RoomController() {}
	
	public Collection<Room> getAllRooms() {
		return RoomStore.getInstance().getAllRooms();
	}
	
	public Set<String> getRoomKeys() {
		return RoomStore.getInstance().getRoomKeys();
	}
	
	public String createRoom(Room room) {
		String id = UUID.randomUUID().toString();
		
		room.setId(id);
		
		System.out.println(room);
		
		RoomStore.getInstance().addRoom(id, room);
		
		readyRoom(id);
		
		return id;
	}
	
	public Room getRoom(String roomId) throws RoomNotFoundException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		
		if(room == null) {
			throw new RoomNotFoundException();
		}
		
		return room;
	}
	
	public User addUserToRoom(String roomId, String address) throws RoomNotFoundException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		
		if(room == null) {
			throw new RoomNotFoundException("Could not find room with id: "+roomId);
		}
		
		String userId = CryptService.getInstance().hash(address);
		
		User user = UserStore.getInstance().findUser(roomId, userId);
		
		if(user == null) {
			//This user hasn't joined the room before, create a new user
			user = new User(userId, "Anonymous", room.getMaxVotes(), room.getMaxVetos(), room.getMaxNominations());
			UserStore.getInstance().addUser(roomId, user);
		}
		
		//The client is going to receive the encrypted version of their id to prevent spoofing.
		User clone = user.clone();
		clone.setId(CryptService.getInstance().encrypt(user.getId()));
		
		return clone;
	}
	
	public void sendRoomInitialization(String roomId, Session session) throws IOException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		session.getBasicRemote().sendText(Serializer.toJson(new Message<Room>("ROOM_INIT", room)));
	}
	
	public void processMessage(String roomId, String userKey, String message) throws IOException, RoomNotFoundException {
		JsonObject obj = new JsonParser().parse(message).getAsJsonObject();
		String msgType = obj.get(TYPE).getAsString();

		switch(msgType) {
		case NOM_ADD:
			String nomName = obj.get(DATA).getAsJsonObject().get("name").getAsString();
			String nomDesc = obj.get(DATA).getAsJsonObject().get("description").getAsString();
			Votable nomination = this.addNomination(roomId, userKey, nomName, nomDesc);
			if(nomination != null) {
				SessionManager.getInstance().sendMessage(RoomSocket.SOCKET_NAME, roomId, new Message<Votable>(NOM_ADD, nomination));
			} else {
				System.out.println("Nomination Failed");
			}
			break;
		case VOTE_ADD:
			String voteId = obj.get(DATA).getAsString();
			boolean successVote = this.vote(roomId, userKey, voteId);
			if(successVote) {
				System.out.println("Successful Vote");
				SessionManager.getInstance().sendMessage(RoomSocket.SOCKET_NAME, roomId, new Message<String>(VOTE_ADD, voteId));
				System.out.println("Vote Failed");
			}
			break;
		case VETO_ADD:
			String vetoId = obj.get(DATA).getAsString();
			boolean successVeto = this.veto(roomId, userKey, vetoId);
			if(successVeto) {
				System.out.println("Successful Veto");
				SessionManager.getInstance().sendMessage(RoomSocket.SOCKET_NAME, roomId, new Message<String>(VETO_ADD, vetoId));
			} else {
				System.out.println("Veto Failed");
			}
			break;
		default:
			break;
		}
	}
	
	public void readyRoom(String roomId) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		TimerManager.getInstance().startTimer(roomId, room.getReadyTime(), RoomState.Nominations);
	}
	
	public void startNominations(String roomId) throws IOException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		if(room.getRoomState() == RoomState.Ready) {
			room.setRoomState(RoomState.Nominations);
			TimerManager.getInstance().removeTimer(roomId);
			TimerManager.getInstance().startTimer(roomId, room.getNominationTime(), RoomState.Voting);
			SessionManager.getInstance().sendMessage(RoomSocket.SOCKET_NAME, roomId, new Message<RoomState>(ROOM_STATE, RoomState.Nominations));
		}
	}
	
	public void startVotes(String roomId) throws IOException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		if(room.getRoomState() == RoomState.Nominations) {
			room.setRoomState(RoomState.Voting);
			TimerManager.getInstance().removeTimer(roomId);
			TimerManager.getInstance().startTimer(roomId, room.getVotingTime(), RoomState.Complete);
			SessionManager.getInstance().sendMessage(RoomSocket.SOCKET_NAME, roomId, new Message<RoomState>(ROOM_STATE, RoomState.Voting));
		}
	}
	
	public void endVotes(String roomId) throws IOException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		if(room.getRoomState() == RoomState.Voting) {
			room.setRoomState(RoomState.Complete);
			TimerManager.getInstance().removeTimer(roomId);
			SessionManager.getInstance().sendMessage(RoomSocket.SOCKET_NAME, roomId, new Message<RoomState>(ROOM_STATE, RoomState.Complete));
			removeRoom(roomId);
		}
	}
	
	public void removeRoom(String roomId) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		if(room.getRoomState() == RoomState.Complete && SessionManager.getInstance().isEmpty(RoomSocket.SOCKET_NAME, roomId)) {
			System.out.println("Removing room: "+roomId);
			SessionManager.getInstance().removeSessions(RoomSocket.SOCKET_NAME, roomId);
			SessionManager.getInstance().removeSessions(TimerSocket.SOCKET_NAME, roomId);
			UserStore.getInstance().removeRoom(roomId);
			RoomStore.getInstance().removeRoom(roomId);
		}
	}
	
	public Votable addNomination(String roomId, String userId, String nomName, String nomDesc) throws RoomNotFoundException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		User u = UserStore.getInstance().findUser(roomId, userId);
		
		if(u == null) {
			System.out.println("Nom could not find user");
			return null;
		}
		
		if(u.getNominations() == 0) {
			return null;
		}
		
		Votable nomination = new Votable(UUID.randomUUID().toString(), nomName, nomDesc);
		
		u.useNomination();
		
		room.addVotable(nomination);
		
		return nomination;
	}
	
	public boolean vote(String roomId, String userId, String votableId) throws RoomNotFoundException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		User u = UserStore.getInstance().findUser(roomId, userId);
		
		if(u == null) {
			return false;
		}
		
		if(u.getVotes() == 0) {
			return false;
		}
		
		Votable v = room.findVotable(votableId);
		
		if(v == null) {
			return false;
		}
		
		u.useVote();
		v.addVote();
		
		return true;
	}

	public boolean veto(String roomId, String userId, String votableId) throws RoomNotFoundException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		User u = UserStore.getInstance().findUser(roomId, userId);
		
		if(u == null) {
			return false;
		}
		
		if(u.getVetos() == 0) {
			return false;
		}
		
		Votable v = room.findVotable(votableId);
		
		if(v == null) {
			return false;
		}
		
		u.useVeto();
		v.addVeto();
		
		return true;
	}

}
