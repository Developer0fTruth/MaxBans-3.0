package org.maxgamer.maxbans;

public class IPRange{
	private IPAddress start;
	private IPAddress finish;
	
	/**
	 * Parses the given IP address, which may be of the form '127.0.0.1' or '127.0.0.*' or '127.0.0.1-127.0.0.5'
	 * @param str the input
	 * @return the IP range
	 * @throws IllegalArgumentException If there was an error in the IP.
	 */
	public static IPRange parse(String str) throws IllegalArgumentException{
		if(str.length() < 7) throw new IllegalArgumentException("Invalid format '" + str + "'"); //IP's must be at least 7 characters each
		String[] parts = str.split("-");
		if(parts.length > 2) throw new IllegalArgumentException("Invalid format '" + str + "'"); 
		
		try{
			if(parts.length == 1){
				if(parts[0].contains("*")){
					//They're requesting a wildcarded IP
					IPAddress start = new IPAddress(parts[0].replaceAll("\\*", "0"));
					IPAddress finish = new IPAddress(parts[0].replaceAll("\\*", "255"));
					return new IPRange(start, finish);
				}
				else{
					IPAddress start = new IPAddress(parts[0]);
					return new IPRange(start, start);
				}
			}
			else{
				IPAddress start = new IPAddress(parts[0]);
				IPAddress finish = new IPAddress(parts[1]);
				return new IPRange(start, finish);
			}
		}
		catch(Exception e){
			throw new IllegalArgumentException("Unexpected error parsing '" + str + "'");
		}
	}
	
	public IPRange(IPAddress start, IPAddress finish){
		this.start = start;
		this.finish = finish;
	}
	
	public String toString(){
		if(start.equals(finish)){
			return start.toString();
		}
		else{
			return start.toString() + "-" + finish.toString();
		}
	}

	public IPAddress getStart() {
		return start;
	}
	public IPAddress getFinish(){
		return finish;
	}
}