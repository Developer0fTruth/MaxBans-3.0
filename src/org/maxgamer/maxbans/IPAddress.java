package org.maxgamer.maxbans;

public class IPAddress implements Comparable<IPAddress>{
	private byte[] address = new byte[4];
	
	public IPAddress(int address){
		for(int i = 3; i >= 0; i--){
			this.address[i] = (byte) address;
			address = address >> 8;
		}
	}
	
	/**
	 * 
	 * @param ip
	 * @throws IllegalArgumentException if the IP address is invalid.
	 */
	public IPAddress(String ip){
		String[] parts = ip.split("\\.");
		if(parts.length != 4) throw new IllegalArgumentException("Bad IP address given, given " + ip);
		
		try{
			for(int i = 0; i < 4; i++){
				String p = parts[i];
				
				byte b = (byte) Integer.parseInt(p); 
				this.address[i] = b;
			}
		}
		catch(NumberFormatException e){
			throw new IllegalArgumentException("Bad IP address given, given " + ip);
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder(15);
		
		for(int i = 0; i < 4; i++){
			sb.append(this.address[i] & 0xFF);
			if(i < 3) sb.append('.');
		}
		
		return sb.toString();
	}
	
	public int toInt(){
		int addr = 0;
		for(int i = 0; i < 4; i++){
			addr = addr << 8;
			addr |= address[i];
		}
		return addr;
	}
	
	public boolean isGreaterThan(IPAddress o){
		for(int i = 0; i < address.length; i++){
			if((address[i] & 0xFF) > (o.address[i] & 0xFF)){
				return true;
			}
			else if((address[i] & 0xFF) < (o.address[i] & 0xFF)){
				return false;
			}
		}
		return false; 
	}
	
	public boolean isLessThan(IPAddress o){
		for(int i = 0; i < address.length; i++){
			if((address[i] & 0xFF) < (o.address[i] & 0xFF)){
				return true;
			}
			else if((address[i] & 0xFF) > (o.address[i] & 0xFF)){
				return false;
			}
		}
		return false; 
	}

	@Override
	public int compareTo(IPAddress o) {
		if(isGreaterThan(o)) return 1;
		if(isLessThan(o)) return -1;
		return 0; //Same
	}
	
	@Override
	public int hashCode(){
		return this.toInt();
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof IPAddress){
			return ((IPAddress) o).address == this.address;
		}
		return false;
	}
}