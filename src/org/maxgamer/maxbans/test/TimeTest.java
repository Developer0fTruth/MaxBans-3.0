package org.maxgamer.maxbans.test;

import org.maxgamer.maxbans.Time;

public class TimeTest{
	public static void main(String[] args){
		Time t = new Time(3723000);
		System.out.println("3723000: " + t.toString());
		System.out.println(t.toDate());
		
		t = new Time(System.currentTimeMillis());
		System.out.println(System.currentTimeMillis() + ": " + t.toString());
	}
}