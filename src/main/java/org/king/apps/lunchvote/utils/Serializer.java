package org.king.apps.lunchvote.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer {
	
	public static String toJson(Object o) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(o);
	}
	
	public static <T> T fromJson(String json, Class<T> type) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(json, type);
	}

}
