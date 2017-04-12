package org.king.apps.lunchvote.models;

import java.util.HashMap;
import java.util.Map;

public class Room {
	
	private String id;
	private String name;
	private int maxVotes;
	private int maxVetos;
	private int maxNominations;
	private RoomState roomState;
	private Map<String, User> users;
	private Map<String, Votable> votables;
	
	public Room() {
		super();
		this.name = "Default Room";
		this.maxVotes = 2;
		this.maxVetos = 1;
		this.maxNominations = 2;
		this.roomState = RoomState.Ready;
		this.users = new HashMap<>();
		this.votables = new HashMap<>();
	}
	
	public Room(String id, String name, int maxVotes, int maxVetos, int maxNominations) {
		super();
		this.id = id;
		this.name = name;
		this.maxVotes = maxVotes;
		this.maxVetos = maxVetos;
		this.maxNominations = maxNominations;
		this.roomState = RoomState.Ready;
		this.users = new HashMap<>();
		this.votables = new HashMap<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxVotes() {
		return maxVotes;
	}

	public void setMaxVotes(int maxVotes) {
		this.maxVotes = maxVotes;
	}

	public int getMaxVetos() {
		return maxVetos;
	}

	public void setMaxVetos(int maxVetos) {
		this.maxVetos = maxVetos;
	}

	public int getMaxNominations() {
		return maxNominations;
	}

	public void setMaxNominations(int maxNominations) {
		this.maxNominations = maxNominations;
	}

	public RoomState getRoomState() {
		return roomState;
	}

	public void setRoomState(RoomState roomState) {
		this.roomState = roomState;
	}

	public Map<String, User> getUsers() {
		return users;
	}

	public void addUser(String id, User user) {
		this.users.put(id, user);
	}
	
	public void removeUser(String id) {
		this.users.remove(id);
	}

	public Map<String, Votable> getVotables() {
		return votables;
	}

	public void addVotable(String id, Votable votable) {
		this.votables.put(id, votable);
	}
	
	public void removeVotable(String id) {
		this.votables.remove(id);
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", name=" + name + ", maxVotes=" + maxVotes + ", maxVetos=" + maxVetos
				+ ", maxNominations=" + maxNominations + ", roomState=" + roomState + ", users=" + users + ", votables="
				+ votables + "]";
	}

}
