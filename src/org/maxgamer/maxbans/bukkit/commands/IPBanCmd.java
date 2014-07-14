package org.maxgamer.maxbans.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.CommandParameters;
import org.maxgamer.maxbans.IPAddress;
import org.maxgamer.maxbans.IPRange;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Time;
import org.maxgamer.maxbans.Util;
import org.maxgamer.maxbans.bukkit.CommandSkeleton;
import org.maxgamer.maxbans.language.Lang;
import org.maxgamer.maxbans.punish.Ban;
import org.maxgamer.maxbans.punish.IPBan;

public class IPBanCmd extends CommandSkeleton{
	public IPBanCmd() {
		super("ipban");
	}

	@Override
	public void run(Profile banner, CommandSender s, String[] args) {
		CommandParameters params = new CommandParameters(args);
		
		IPRange range;
		try{
			range = IPRange.parse(params.getTarget());
		}
		catch(IllegalArgumentException e){
			Profile victim = BanManager.getProfile(params.getTarget(), true);
			if(victim == null){
				s.sendMessage(ChatColor.GREEN + params.getTarget() + " is not a valid IP, IP Range, or user.");
				return;
			}
			
			//We ban the username as well as the IP address if supplied with a username.
			Ban ban = new Ban(victim, banner, params.getReason(), System.currentTimeMillis(), params.getDuration() > 0 ? params.getDuration() + System.currentTimeMillis() : 0);
			Ban old = BanManager.getBan(victim);
			
			if(old != null){
				if((old.isTemporary() && ban.isTemporary() && ban.getExpires() > old.getExpires()) || (ban.isTemporary() == false && old.isTemporary() == false)){
					BanManager.ban(victim, ban);
				}
			}
			
			if(victim.getLastIP() != null){
				range = IPRange.parse(params.getTarget());
			}
			else{
				s.sendMessage(ChatColor.GREEN + "There is no IP record for that user. They have been banned normally instead.");
				return;
			}
		}
		
		IPBan ban = new IPBan(range.toString(), banner, params.getReason(), System.currentTimeMillis(), params.getDuration() > 0 ? params.getDuration() + System.currentTimeMillis() : 0);
		
		try{
			BanManager.ipban(ban);
		}
		catch(IllegalArgumentException e){
			s.sendMessage(ChatColor.GREEN + "That ban overlaps with an existing IP ban.");
			return;
		}
		
		Time expirey = new Time(ban.getExpires());
		String msg = Lang.get("ipban.disconnect", "banner", banner.getUser(), "reason", ChatColor.translateAlternateColorCodes('&', params.getReason()), "remaining", expirey.toDuration(System.currentTimeMillis()), "expires", expirey.toDate(), "range", range.toString(), "ip", range.toString());
		msg = Util.trim(msg, 255);
		for(Player p : Bukkit.getOnlinePlayers()){
			IPAddress addr = new IPAddress(p.getAddress().getAddress().getHostAddress());
			if(range.contains(addr)){
				p.kickPlayer(msg);
			}
		}
		
		msg = Lang.get("ipban.disconnect", "banner", banner.getUser(), "reason", ChatColor.translateAlternateColorCodes('&', params.getReason()), "remaining", expirey.toDuration(System.currentTimeMillis()), "expires", expirey.toDate(), "range", range.toString(), "ip", range.toString());
		if(params.isSilent()){
			Bukkit.broadcast("[Silent] " + msg, "maxbans.see.silent");
		}
		else{
			Bukkit.broadcast(msg, "maxbans.see.broadcast");
		}
	}
}