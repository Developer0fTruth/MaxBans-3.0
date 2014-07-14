package org.maxgamer.maxbans.bukkit.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.CommandParameters;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Time;
import org.maxgamer.maxbans.bukkit.CommandSkeleton;
import org.maxgamer.maxbans.language.Lang;
import org.maxgamer.maxbans.punish.Mute;

public class MuteCmd extends CommandSkeleton{
	public MuteCmd() {
		super("mute");
	}

	@Override
	public void run(Profile banner, CommandSender s, String[] args) {
		CommandParameters params = new CommandParameters(args);
		
		Profile victim = BanManager.getProfile(params.getTarget(), true);
		if(victim == null){
			s.sendMessage(ChatColor.GREEN + "No user " + params.getTarget() + " found.");
			return;
		}
		
		Mute mute = new Mute(victim, banner, params.getReason(), System.currentTimeMillis(), params.getDuration() > 0 ? params.getDuration() + System.currentTimeMillis() : 0);
		Mute old = BanManager.getMute(victim);
		
		if(old != null){
			if((old.isTemporary() && mute.isTemporary() && old.getExpires() >= mute.getExpires()) || (old.isTemporary() == false)){
				s.sendMessage(ChatColor.GREEN + "The user " + victim.getUser() + " has an existing mute which lasts longer than the given one.");
				return;
			}
		}
		
		BanManager.mute(victim, mute);
		
		Player p = Bukkit.getPlayer(UUID.fromString(victim.getId()));
		
		Time expirey = new Time(mute.getExpires());
		String msg = Lang.get("mute.broadcast", "reason", ChatColor.translateAlternateColorCodes('&', params.getReason()), "banner", banner.getUser(), "muter", banner.getUser(), "remaining", expirey.toDuration(System.currentTimeMillis()), "expires", expirey.toDate(), "name", victim.getUser());
		
		if(params.isSilent()){
			Bukkit.broadcast("[Silent] " + msg, "maxbans.see.silent");
			if(p != null) p.sendMessage(msg);
		}
		else{
			Bukkit.broadcast(msg, "maxbans.see.broadcast");
		}
	}
}