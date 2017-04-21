package org.king.apps.lunchvote.singletons;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import org.king.apps.lunchvote.models.Message;
import org.king.apps.lunchvote.utils.Serializer;

public class SessionManager {
	
	private static SessionManager instance;
	
	public static SessionManager getInstance() {
		if(instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}
	
	private Map<String, Set<Session>> compositeSessionMap;
	
	private SessionManager() {
		this.compositeSessionMap = new HashMap<>();
	}
	
	public void addSession(String socketName, String roomId, Session session) {
		String key = buildCompositeKey(socketName, roomId);
		
		if(!this.compositeSessionMap.containsKey(key)) {
			this.compositeSessionMap.put(key, Collections.synchronizedSet(new HashSet<>()));
		}
		
		this.compositeSessionMap.get(key).add(session);
	}
	
	public boolean removeSession(String socketName, String roomId, Session session) {
		String key = buildCompositeKey(socketName, roomId);
		
		if(this.compositeSessionMap.containsKey(key)) {
			return this.compositeSessionMap.get(key).remove(session);
		}
		
		return false;
	}
	
	public void removeSessions(String socketName, String roomId) {
		String key = buildCompositeKey(socketName, roomId);
		
		this.compositeSessionMap.remove(key);
	}
	
	public Set<Session> getSessions(String socketName, String roomId) {
		String key = buildCompositeKey(socketName, roomId);
		
		return this.compositeSessionMap.get(key);
	}
	
	public void sendMessage(String socketName, String roomId, Message<?> message) throws IOException {
		String key = buildCompositeKey(socketName, roomId);
		
		if(this.compositeSessionMap.containsKey(key)) {
			for(Session session : this.compositeSessionMap.get(key)) {
				if(session.isOpen()) {
					session.getBasicRemote().sendText(Serializer.toJson(message));
				}
			}
		}
	}
	
	public boolean isEmpty(String socketName, String roomId) {
		String key = buildCompositeKey(socketName, roomId);
		
		if(this.compositeSessionMap.containsKey(key)) {
			return this.compositeSessionMap.get(key).isEmpty();
		}
		
		return true;
	}
	
	private String buildCompositeKey(String socketName, String roomId) {
		return socketName+roomId;
	}

}
