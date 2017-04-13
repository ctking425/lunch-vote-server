package org.king.apps.lunchvote.singletons;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.king.apps.lunchvote.models.RoomState;
import org.king.apps.lunchvote.utils.Timer;

public class TimerManager {
	
	private static TimerManager instance;
	
	public static TimerManager getInstance() {
		if(instance == null) {
			instance = new TimerManager();
		}
		return instance;
	}
	
	private Map<String, Thread> timers;
	
	private TimerManager() {
		timers = Collections.synchronizedMap(new HashMap<>());
	}
	
	public void startTimer(String roomId, int time, RoomState callback) {
		if(timers.containsKey(roomId)) {
			System.out.println("Timer already started");
		}
		
		Timer timer = new Timer(roomId, time, callback);
		Thread thread = new Thread(timer);
		thread.start();
		timers.put(roomId, thread);
		System.out.println("Started timer for: "+roomId);
	}
	
	public void removeTimer(String roomId) {
		if(timers.containsKey(roomId)) {
			System.out.println("Removing timer for: "+roomId);
			timers.remove(roomId);
		}
	}

}
