package org.maxgamer.maxbans.punish;

import java.util.UUID;

import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Tables;
import org.maxgamer.maxbans.Time;
import org.maxgamer.maxbans.language.Lang;

public class Mute extends Punishment{
	public Mute(Object id){
		super(Tables.MUTE_TABLE, "profile", id);
	}
	
	public Mute(Profile victim, Profile banner, String reason, long created, long expires) {
		super(Tables.MUTE_TABLE, "profile", victim.getId(), banner, reason, created, expires);
	}
	
	public String getMuteMessage(){
		Time t = new Time(getExpires());
		return Lang.get("mute.chat", "banner", BanManager.getProfile(UUID.fromString(getBanner())).getUser(), "reason", getReason(), "expires", t.toDate(), "remaining", t.toDuration(System.currentTimeMillis()));
	}
}