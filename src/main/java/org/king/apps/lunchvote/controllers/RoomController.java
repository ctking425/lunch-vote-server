package org.king.apps.lunchvote.controllers;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.king.apps.lunchvote.models.Room;
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

}
