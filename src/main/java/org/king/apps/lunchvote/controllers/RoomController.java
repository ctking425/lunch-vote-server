package org.king.apps.lunchvote.controllers;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.king.apps.lunchvote.models.Room;
import org.king.apps.lunchvote.models.User;
import org.king.apps.lunchvote.models.Votable;
import org.king.apps.lunchvote.singletons.RoomStore;

public class RoomController {
	
	
	public RoomController() {
		
	}
	
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
		room.addUser(userId, user);
		
		return room;
	}
	
	public void removeUserFromRoom(String roomId, String userId) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		room.removeUser(userId);
	}
	
	public Votable addNomination(String roomId, String nomName, String nomDesc) {
		Room room = RoomStore.getInstance().getRoom(roomId);
		String vId = UUID.randomUUID().toString();
		Votable nomination = new Votable(vId, nomName, nomDesc);
		room.addVotable(vId, nomination);
		return nomination;
	}

}
