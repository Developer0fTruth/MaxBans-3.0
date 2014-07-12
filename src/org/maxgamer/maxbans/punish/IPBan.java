package org.maxgamer.maxbans.punish;

import java.util.UUID;

import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.IPRange;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Tables;
import org.maxgamer.maxbans.Time;
import org.maxgamer.maxbans.language.Lang;

public class IPBan extends Punishment{
	public IPBan(String range){
		super(Tables.IPBAN_TABLE, "ip_range", range);
	}
	
	public IPBan(String range, Profile banner, String reason, long created, long expires) {
		super(Tables.IPBAN_TABLE, "ip_range", range, banner, reason, created, expires);
	}
	
	@Override
	public String getId(){
		return (String) super.getId();
	}
	
	public IPRange getRange(){
		return IPRange.parse(getId());
	}
	
	public String getDisconnectMessage(){
		Time t = new Time(getExpires());
		return Lang.get("ipban.disconnect", "banner", BanManager.getProfile(UUID.fromString(getBanner())).getUser(), "reason", getReason(), "remaining", t.toDuration(System.currentTimeMillis()), "expires", t.toDate(), "range", getId(), "ip", getId());
	}
}