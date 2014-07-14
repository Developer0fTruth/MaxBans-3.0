package org.maxgamer.maxbans.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.CommandParameters;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.bukkit.CommandSkeleton;
import org.maxgamer.maxbans.language.Lang;
import org.maxgamer.maxbans.punish.Mute;

public class UnmuteCmd extends CommandSkeleton{
	public UnmuteCmd() {
		super("unmute");
	}

	@Override
	public void run(Profile banner, CommandSender s, String[] args) {
		CommandParameters params = new CommandParameters(args);
		
		Profile victim = BanManager.getProfile(params.getTarget(), true);
		if(victim == null){
			s.sendMessage(ChatColor.GREEN + "No user " + params.getTarget() + " found.");
			return;
		}
		
		Mute mute = BanManager.getMute(victim);
		if(mute != null){
			BanManager.unmute(victim);
			
			String msg = Lang.get("mute.unmute", "banner", banner.getUser(), "muter", banner.getUser(), "name", victim.getUser());
			if(params.isSilent()){
				Bukkit.broadcast("[Silent] " + msg, "maxbans.see.silent");
			}
			else{
				Bukkit.broadcast(msg, "maxbans.see.broadcast");
			}
			return;
		}
		
		s.sendMessage(ChatColor.GREEN + "Could not locate a mute for " + victim.getUser());
	}
}