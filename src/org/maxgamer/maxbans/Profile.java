package org.maxgamer.maxbans;

import java.util.UUID;

import org.maxgamer.maxbans.punish.Ban;
import org.maxgamer.maxbans.punish.Mute;

public class Profile extends Eloquent{
	private String user;
	private String lastIp;
	private long lastSeen;
	
	public Profile(UUID uuid) {
		super(Tables.PROFILE_TABLE, "uuid", uuid.toString());
	}
	
	public Profile(UUID uuid, String user, String lastIp, long lastSeen){
		this(uuid);
		this.user = user;
		this.lastIp = lastIp;
	}
	
	@Override
	public String getId(){
		return (String) super.getId();
	}
	
	public String getUser(){
		return user;
	}
	
	public String getLastIP(){
		return lastIp;
	}
	
	public void setLastIP(String ip){
		this.lastIp = ip;
	}
	
	public void setLastSeen(long epochms){
		this.lastSeen = epochms;
	}
	
	public void setUser(String name){
		this.user = name;
	}
	
	@Override
	public String toString(){
		return getId() + " [" + user + "] @" + lastIp;
	}
	
	public long getLastSeen(){
		return lastSeen;
	}
	
	public Ban getBan(){
		return BanManager.getBan(this);
	}
	
	public Mute getMute(){
		return BanManager.getMute(this);
	}
}