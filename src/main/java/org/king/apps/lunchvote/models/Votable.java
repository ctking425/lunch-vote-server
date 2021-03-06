package org.king.apps.lunchvote.models;

public class Votable {
	
	private String id;
	private String name;
	private String description;
	private int votes;
	private int vetos;
	
	public Votable(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.votes = 0;
		this.vetos = 0;
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getVotes() {
		return votes;
	}
	
	public void addVote() {
		this.votes++;
	}
	
	public int getVetos() {
		return vetos;
	}
	
	public void addVeto() {
		this.vetos++;
	}
}
