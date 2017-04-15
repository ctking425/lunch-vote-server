package org.king.apps.lunchvote.singletons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.king.apps.lunchvote.exception.RoomNotFoundException;
import org.king.apps.lunchvote.models.User;

public class UserStore {

	private static UserStore instance;
	
	public static UserStore getInstance() {
		if(instance == null) {
			instance = new UserStore();
		}
		return instance;
	}
	
	private Map<String, Set<User>> userMap;
	
	private UserStore() {
		userMap = new HashMap<>();
	}
	
	public void addUser(String roomId, User user) {
		if(!userMap.containsKey(roomId)) {
			userMap.put(roomId, new HashSet<User>());
		}
		
		userMap.get(roomId).add(user);
	}
	
	public User findUser(String roomId, String userId) throws RoomNotFoundException {
		if(!userMap.containsKey(roomId)) {
			return null;
		}
		
		String decrypedUserId;
		
		try {
			decrypedUserId = CryptService.getInstance().decrypt(userId);
		} catch(Exception e) {
			decrypedUserId = userId;
		}
		
		for(User u : userMap.get(roomId)) {
			if(u.getId().equals(decrypedUserId)) {
				return u;
			}
		}
		
		return null;
	}
	
	public void removeRoom(String roomId) {
		userMap.remove(roomId);
	}

}
