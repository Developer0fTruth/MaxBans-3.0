package org.maxgamer.maxbans;

import java.util.Calendar;

public class Time{
	private long time;
	
	public Time(long epochms){
		this.time = epochms;
	}
	
	public boolean isPast(){
		return time <= System.currentTimeMillis();
	}
	
	public boolean isFuture(){
		return time > System.currentTimeMillis();
	}
	
	public String toDate(){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
	}
	
	/**
	 * Prepares this time as a human readable duration.
	 * @return The time since epoch start, in human readable format (x years, y months, n hours, etc)
	 */
	public String toDuration(long relativeTo){
		if(time <= 0) return "Never";
		
		long time = this.time - relativeTo;
    	time =  (long) Math.ceil(time / 1000.0); //Work in seconds.
    	StringBuilder sb = new StringBuilder(40);
    	
    	if(time / 31449600 > 0){
    		//Years
    		long years = time / 31449600;
    		if(years > 100) return "Never";
    		
    		sb.append(years + (years == 1 ? " year " : " years "));
    		time -= years * 31449600;
    	}
    	if(time / 2620800 > 0){
    		//Months
    		long months = time / 2620800;
    		sb.append(months + (months == 1 ? " month " : " months "));
    		time -= months * 2620800;
    	}
    	if(time / 604800 > 0){
    		//Weeks
    		long weeks = time / 604800;
    		sb.append(weeks + (weeks == 1 ? " week " : " weeks "));
    		time -= weeks * 604800;
    	}
    	if(time / 86400 > 0){
    		//Days
    		long days = time / 86400;
    		sb.append(days + (days == 1 ? " day " : " days "));
    		time -= days * 86400;
    	}
    	
    	if(time / 3600 > 0){
    		//Hours
    		long hours = time / 3600;
    		sb.append(hours + (hours == 1 ? " hour " : " hours "));
    		time -= hours * 3600;
    	}
    	
    	if(time / 60 > 0){
    		//Minutes
    		long minutes = time / 60;
    		sb.append(minutes + (minutes == 1 ? " minute " : " minutes "));
    		time -= minutes * 60;
    	}
    	
    	if(time > 0){
    		//Seconds
    		sb.append(time + (time == 1 ? " second " : " seconds "));
    	}
    	
    	if(sb.length() > 1){
    		sb.replace(sb.length() - 1, sb.length(), "");
    	}
    	else{
    		sb = new StringBuilder("N/A");
    	}
    	return sb.toString();
    }
	
	@Override
	public String toString(){
		return toDate();
	}
}