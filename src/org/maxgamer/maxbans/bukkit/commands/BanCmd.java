package org.maxgamer.maxbans.bukkit.commands;

import java.util.UUID;

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

public class BanCmd extends CommandSkeleton{
	public BanCmd() {
		super("ban");
	}

	@Override
	public void run(Profile banner, CommandSender s, String[] args) {
		CommandParameters params = new CommandParameters(args);
		
		try{
			IPRange range = IPRange.parse(params.getTarget());
			
			//They gave us an IPRange.
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
		catch(IllegalArgumentException e){
			Profile victim = BanManager.getProfile(params.getTarget(), true);
			if(victim == null){
				s.sendMessage(ChatColor.GREEN + "No user " + params.getTarget() + " found.");
				return;
			}
			
			Ban ban = new Ban(victim, banner, params.getReason(), System.currentTimeMillis(), params.getDuration() > 0 ? params.getDuration() + System.currentTimeMillis() : 0);
			Ban old = BanManager.getBan(victim);
			
			if(old != null){
				if((old.isTemporary() && ban.isTemporary() && old.getExpires() >= ban.getExpires()) || (old.isTemporary() == false)){
					s.sendMessage(ChatColor.GREEN + "The user " + victim.getUser() + " has an existing ban which lasts longer than the given one.");
					return;
				}
			}
			
			BanManager.ban(victim, ban);
			
			Player p = Bukkit.getPlayer(UUID.fromString(victim.getId()));
			if(p != null){
				p.kickPlayer(Util.trim(ban.getDisconnectMessage(), 255));
			}
			
			Time expirey = new Time(ban.getExpires());
			String msg = Lang.get("ban.broadcast", "reason", ChatColor.translateAlternateColorCodes('&', params.getReason()), "banner", banner.getUser(), "remaining", expirey.toDuration(System.currentTimeMillis()), "expires", expirey.toDate(), "name", victim.getUser());
			if(params.isSilent()){
				Bukkit.broadcast("[Silent] " + msg, "maxbans.see.silent");
			}
			else{
				Bukkit.broadcast(msg, "maxbans.see.broadcast");
			}
		}
	}
}