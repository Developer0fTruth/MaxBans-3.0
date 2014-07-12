package org.maxgamer.maxbans.language;

import java.util.HashMap;


public class Lang{
	private static HashMap<String, String> messages;
	
	public static void init(HashMap<String, String> msgConfig){
		messages = msgConfig;
	}
	
	public static String get(String key, String... args){
		if(args.length % 2 > 0){
			throw new IllegalArgumentException("Language arguments must be an even length.");
		}
		
		String msg = messages.get(key);
		if(msg == null) return "No such MaxBans language message defined for " + key + ", please inform an administrator.";
		
		for(int i = 0; i < args.length; i++){
			msg = msg.replaceAll("\\{" + args[i] + "\\}", args[++i]);
		}
		return msg;
	}
}