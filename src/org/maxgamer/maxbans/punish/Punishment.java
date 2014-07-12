package org.maxgamer.maxbans.punish;

import org.maxgamer.maxbans.Eloquent;
import org.maxgamer.maxbans.Profile;

public class Punishment extends Eloquent{
	private String banner;
	private String reason;
	private long created;
	private long expires;
	
	public Punishment(String table, String idField, Object id){
		super(table, idField, id);
	}
	
	public Punishment(String table, String idField, Object id, Profile banner, String reason, long created, long expires){
		super(table, idField, id);
		this.banner = banner.getId();
		this.reason = reason;
		this.created = created;
		this.expires = expires;
	}
	
	public String getBanner(){
		return banner;
	}
	
	public String getReason(){
		return reason;
	}
	
	public long getCreated(){
		return created;
	}
	
	public long getExpires(){
		return expires;
	}
	
	public boolean isTemporary(){
		return expires > 0;
	}
	
	public boolean hasExpired(){
		return isTemporary() && expires < System.currentTimeMillis();
	}
}