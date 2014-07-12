package org.maxgamer.maxbans.test;

import org.maxgamer.maxbans.IPAddress;

public class IPTest{
	public static void main(String[] args){
		String[] ips = new String[]{"1.2.3.4", "127.0.0.1", "126.0.0.1", "255.255.255.255"};
		
		
		for(String s : ips){
			try{
				IPAddress addr = new IPAddress(s);
				if(addr.toString().equals(s) == false){
					System.out.println("Incorrect result for " + s + ", got " + addr.toString() + "(" + addr.toInt() + ")");
				}
				else{
					System.out.println(s + " is " + addr);
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("Error parsing IP " + s);
			}
		}
	}
}