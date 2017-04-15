package org.king.apps.lunchvote.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.king.apps.lunchvote.models.Message;
import org.king.apps.lunchvote.models.Room;
import org.king.apps.lunchvote.models.RoomState;
import org.king.apps.lunchvote.models.User;
import org.king.apps.lunchvote.models.Votable;
import org.king.apps.lunchvote.singletons.RoomStore;
import org.king.apps.lunchvote.singletons.TimerManager;
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
	
	public Room getRoom(String id) {
		System.out.println(id);
		Room room = RoomStore.getInstance().getRoom(id);
		
		if(room == null) {
			System.out.println("Could not find room");
		} else {
			System.out.println(room.getName());
		}
		
		return room;
	}
	
	public Room addUserToRoom(String roomId, String userId) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		User user = new User(userId, "Anonymous", room.getMaxVotes(), room.getMaxVetos(), room.getMaxNominations());
		room.addUser(user);
		
		return room;
	}
	
	public void removeUserFromRoom(String roomId, String userId) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		if(room != null) {
			room.removeUser(userId);
			if(room.getRoomState().equals(RoomState.Complete) && room.getUsers().isEmpty()) {
				removeRoom(roomId);
			}
		}
	}
	
	public Votable addNomination(String roomId, String nomName, String nomDesc) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		Votable nomination = new Votable(UUID.randomUUID().toString(), nomName, nomDesc);
		room.addVotable(nomination);
		return nomination;
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
			if(room.getUsers().isEmpty()) {
				removeRoom(roomId);
			}
		}
	}
	
	private void removeRoom(String roomId) {
		System.out.println("Removing room: "+roomId);
		RoomSocket.sessions.remove(roomId);
		TimerSocket.sessions.remove(roomId);
		RoomStore.getInstance().removeRoom(roomId);
	}
	
	public boolean vote(String roomId, String userId, String votableId) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		User u = room.findUser(userId);
		
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

	public boolean veto(String roomId, String userId, String votableId) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		User u = room.findUser(userId);
		
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
