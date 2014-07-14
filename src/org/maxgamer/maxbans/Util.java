package org.maxgamer.maxbans;

import java.text.ParseException;
import java.util.HashSet;

public class Util{
	
	private static HashSet<String> yes = new HashSet<String>();
	private static HashSet<String> no = new HashSet<String>();
	
	static{
		yes.add("yes"); yes.add("true"); yes.add("on"); yes.add("enable"); yes.add("1");
		no.add("no"); no.add("false"); no.add("off"); no.add("disable"); no.add("0");
	}
	
	/**
	 * Converts the given user input string into a boolean.  Such as if a player enters "yes", "enable", "disable", it converts them to true, true, false respectively.
	 * @param response The string the user has sent
	 * @return The boolean equivilant
	 * @throws ParseException if the given string is not a valid answer... Example: "bananas" is not a boolean!
	 */
	public static boolean parseBoolean(String response) throws ParseException{
		response = response.toLowerCase();
		if(yes.contains(response)) return true;
		if(no.contains(response)) return false;
		throw new ParseException("Invalid boolean: " + response, 0);
	}
	
	public static String trim(String s, int max){
		if(s.length() > max){
			return s.substring(0, max + 1);
		}
		return s;
	}
}