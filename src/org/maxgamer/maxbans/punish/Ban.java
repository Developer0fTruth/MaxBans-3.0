package org.maxgamer.maxbans.punish;

import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Tables;

public class Ban extends Punishment{
	public Ban(Object id){
		super(Tables.BAN_TABLE, "profile", id);
	}
	
	public Ban(Profile victim, Profile banner, String reason, long created, long expires) {
		super(Tables.BAN_TABLE, "profile", victim.getId(), banner, reason, created, expires);
	}
}