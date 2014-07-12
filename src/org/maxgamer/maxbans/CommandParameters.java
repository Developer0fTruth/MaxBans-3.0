package org.maxgamer.maxbans;

public class CommandParameters{
	//Processing information
	private int index = 0;
	private String[] args;
	
	//Real data
	private boolean silent;
	private String target;
	private String reason;
	private long duration;
	
	/**
	 * returns true if this was a silent command. This argument defaults to false
	 * @return true for silent false for loud
	 */
	public boolean isSilent(){
		return silent;
	}
	/**
	 * Returns the target of the command. This is required.
	 * @return the target, not null or empty
	 */
	public String getTarget(){
		return target;
	}
	/**
	 * Returns the reason for the command. This is not null but may be empty
	 * @return the reason
	 */
	public String getReason(){
		return reason;
	}
	/**
	 * Returns the duration for the command. This is in milliseconds
	 * @return The millisecond duration from the args.
	 */
	public long getDuration(){
		return duration;
	}
	
	/**
	 * Constructs a new set of command parameters from the given args array.
	 * The result consists of a target name (required), a reason (optional), a duration (optional, zero if not given)
	 * and silent (optional, defaults to false)
	 * @param args The arguments given
	 * @throws IllegalArgumentException with a meaningful message if the input was invalid
	 */
	public CommandParameters(String[] args) throws IllegalArgumentException{
		this.args = args;
		
		int i;
		for(i = 0; i < args.length; i++){
			if(args[i].equalsIgnoreCase("-s")){
				this.silent = true;
				
				args[i] = null;
				break;
			}
		}
		
		this.target = next();
		
		if(hasNext()){
			try{
				String s = next();
				double d = Double.parseDouble(s);
				
				String arg = next();
				
				int modifier;
				if(arg.startsWith("hour")){
					modifier = 3600;
				}
				else if(arg.startsWith("min")){
					modifier = 60;
				}
				else if(arg.startsWith("sec")){
					modifier = 1;
				}
				else if(arg.startsWith("week")){
					modifier = 604800;
				}
				else if(arg.startsWith("day")){
					modifier = 86400;
				}
				else if(arg.startsWith("year")){
					modifier = 31449600;
				}
				else if(arg.startsWith("month")){
					modifier = 2620800;
				}
				else{
					index -= 2; //Rewind by two
					modifier = 0;
				}
				
				if(modifier > 0){
					this.duration = (int) (modifier * d * 1000);
				}
			}
			catch(NumberFormatException e){
				this.index--; //Rewind
			}
		}
		
		StringBuilder sb = new StringBuilder();
		while(hasNext()){
			sb.append(next());
			
			if(hasNext()) sb.append(' ');
		}
		this.reason = sb.toString();
	}
	
	private boolean hasNext(){
		if(index >= args.length) return false;
		
		while(args[index] == null || args[index].isEmpty()){
			//Nothing
			index++;
			if(index >= args.length) return false;
		}
		return true;
	}
	
	private String next(){
		try{
			while(args[index] == null || args[index].isEmpty()){
				//Nothing
				index++;
			}
			return args[index++];
		}
		catch(IndexOutOfBoundsException e){
			throw new IllegalArgumentException("Not enough args given.");
		}
	}
}