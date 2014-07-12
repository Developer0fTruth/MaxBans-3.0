package org.maxgamer.maxbans.punish;

import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Tables;

public class Mute extends Punishment{
	public Mute(Object id){
		super(Tables.MUTE_TABLE, "profile", id);
	}
	
	public Mute(Profile victim, Profile banner, String reason, long created, long expires) {
		super(Tables.MUTE_TABLE, "profile", victim.getId(), banner, reason, created, expires);
	}
}