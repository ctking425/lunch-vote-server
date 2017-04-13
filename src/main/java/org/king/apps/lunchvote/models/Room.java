package org.king.apps.lunchvote.models;

import java.util.ArrayList;
import java.util.List;

public class Room {
	
	private String id;
	private String name;
	private int maxVotes;
	private int maxVetos;
	private int maxNominations;
	private RoomState roomState;
	private List<User> users;
	private List<Votable> votables;
	
	public Room() {
		super();
		this.name = "Default Room";
		this.maxVotes = 2;
		this.maxVetos = 1;
		this.maxNominations = 2;
		this.roomState = RoomState.Ready;
		this.users = new ArrayList<>();
		this.votables = new ArrayList<>();
	}
	
	public Room(String id, String name, int maxVotes, int maxVetos, int maxNominations) {
		super();
		this.id = id;
		this.name = name;
		this.maxVotes = maxVotes;
		this.maxVetos = maxVetos;
		this.maxNominations = maxNominations;
		this.roomState = RoomState.Ready;
		this.users = new ArrayList<>();
		this.votables = new ArrayList<>();
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
	
	public User findUser(String userId) {
		for(User u : this.users) {
			if(u.getId().equals(userId)) {
				return u;
			}
		}
		return null;
	}

	public List<User> getUsers() {
		return users;
	}

	public void addUser(User user) {
		this.users.add(user);
	}
	
	public void removeUser(String userId) {
		boolean found = false;
		int i;
		for(i=0; i<this.users.size(); i++) {
			if(this.users.get(i).getId().equals(userId)) {
				found = true;
				break;
			}
		}
		if(found) this.users.remove(i);
	}
	
	public Votable findVotable(String votableId) {
		for(Votable v : this.votables) {
			if(v.getId().equals(votableId)){
				return v;
			}
		}
		return null;
	}

	public List<Votable> getVotables() {
		return votables;
	}

	public void addVotable(Votable votable) {
		this.votables.add(votable);
	}
	
	public void removeVotable(Votable votable) {
		this.votables.remove(votable);
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", name=" + name + ", maxVotes=" + maxVotes + ", maxVetos=" + maxVetos
				+ ", maxNominations=" + maxNominations + ", roomState=" + roomState + ", users=" + users + ", votables="
				+ votables + "]";
	}

}
