package org.king.apps.lunchvote.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.king.apps.lunchvote.exception.RoomNotFoundException;
import org.king.apps.lunchvote.models.Message;
import org.king.apps.lunchvote.models.Room;
import org.king.apps.lunchvote.models.RoomState;
import org.king.apps.lunchvote.models.User;
import org.king.apps.lunchvote.models.Votable;
import org.king.apps.lunchvote.singletons.CryptService;
import org.king.apps.lunchvote.singletons.RoomStore;
import org.king.apps.lunchvote.singletons.TimerManager;
import org.king.apps.lunchvote.singletons.UserStore;
import org.king.apps.lunchvote.sockets.RoomSocket;
import org.king.apps.lunchvote.sockets.TimerSocket;

public class RoomController {
	
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
			RoomSocket.sendToAll(roomId, new Message<RoomState>(RoomSocket.ROOM_STATE, RoomState.Nominations));
		}
	}
	
	public void startVotes(String roomId) throws IOException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		if(room.getRoomState() == RoomState.Nominations) {
			room.setRoomState(RoomState.Voting);
			TimerManager.getInstance().removeTimer(roomId);
			TimerManager.getInstance().startTimer(roomId, room.getVotingTime(), RoomState.Complete);
			RoomSocket.sendToAll(roomId, new Message<RoomState>(RoomSocket.ROOM_STATE, RoomState.Voting));
		}
	}
	
	public void endVotes(String roomId) throws IOException {
		Room room = RoomStore.getInstance().getRoom(roomId);
		if(room.getRoomState() == RoomState.Voting) {
			room.setRoomState(RoomState.Complete);
			TimerManager.getInstance().removeTimer(roomId);
			RoomSocket.sendToAll(roomId, new Message<RoomState>(RoomSocket.ROOM_STATE, RoomState.Complete));
			removeRoom(roomId);
		}
	}
	
	public void removeRoom(String roomId) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		if(room.getRoomState() == RoomState.Complete && RoomSocket.isRoomEmpty(roomId)) {
			System.out.println("Removing room: "+roomId);
			RoomSocket.sessions.remove(roomId);
			TimerSocket.sessions.remove(roomId);
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
