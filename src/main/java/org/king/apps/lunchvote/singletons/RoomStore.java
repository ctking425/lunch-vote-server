package org.king.apps.lunchvote.singletons;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.king.apps.lunchvote.models.Room;

public class RoomStore {
	
	private static RoomStore instance;
	
	public static RoomStore getInstance() {
		if(instance == null) {
			instance = new RoomStore();
		}
		return instance;
	}
	
	private Map<String, Room> roomMap;
	
	private RoomStore() {
		roomMap = Collections.synchronizedMap(new HashMap<String, Room>());
	}
	
	public void addRoom(String id, Room room) {
		roomMap.put(id, room);
	}
	
	public Room getRoom(String id) {
		return roomMap.get(id);
	}
	
	public void removeRoom(String id) {
		roomMap.remove(id);
	}
	
	public Set<String> getRoomKeys() {
		return roomMap.keySet();
	}
	
	public Collection<Room> getAllRooms() {
		return roomMap.values();
	}

}
