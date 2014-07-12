package org.maxgamer.maxbans.punish;

import java.util.UUID;

import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Tables;
import org.maxgamer.maxbans.Time;
import org.maxgamer.maxbans.language.Lang;

public class Ban extends Punishment{
	public Ban(Object id){
		super(Tables.BAN_TABLE, "profile", id);
	}
	
	public Ban(Profile victim, Profile banner, String reason, long created, long expires) {
		super(Tables.BAN_TABLE, "profile", victim.getId(), banner, reason, created, expires);
	}
	
	public String getDisconnectMessage(){
		Time t = new Time(getExpires());
		return Lang.get("ban.disconnect", "banner", BanManager.getProfile(UUID.fromString(getBanner())).getUser(), "reason", getReason(), "remaining", t.toDuration(System.currentTimeMillis()), "expires", t.toDate());
	}
}