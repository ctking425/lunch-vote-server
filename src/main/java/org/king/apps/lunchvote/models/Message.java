package org.king.apps.lunchvote.models;

public class Message<T> {
	private String type;
	private T data;
	
	public Message() {
		super();
	}
	
	public Message(String type, T data) {
		super();
		this.type = type;
		this.data = data;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
}
