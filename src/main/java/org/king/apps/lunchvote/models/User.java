package org.king.apps.lunchvote.models;

public class User {
	
	private String id;
	private String name;
	private int votes;
	private int vetos;
	private int nominations;
	
	public User(String id, String name, int votes, int vetos, int nominations) {
		super();
		this.id = id;
		this.name = name;
		this.votes = votes;
		this.vetos = vetos;
		this.nominations = nominations;
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
	
	public int getVotes() {
		return votes;
	}
	
	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	public void useVote() {
		if(this.votes > 0) this.votes--;
	}
	
	public int getVetos() {
		return vetos;
	}
	
	public void setVetos(int vetos) {
		this.vetos = vetos;
	}
	
	public void useVeto() {
		if(this.vetos > 0) this.vetos--;
	}
	
	public int getNominations() {
		return nominations;
	}
	
	public void setNominations(int nominations) {
		this.nominations = nominations;
	}
	
	public void useNomination() {
		if(this.nominations > 0) this.nominations--;
	}

}
