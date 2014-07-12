package org.maxgamer.maxbans.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.CommandParameters;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.bukkit.CommandSkeleton;
import org.maxgamer.maxbans.punish.Ban;

public class BanCmd extends CommandSkeleton{
	public BanCmd() {
		super("ban");
	}

	@Override
	public void run(Profile banner, CommandSender s, String[] args) {
		CommandParameters params = new CommandParameters(args);
		
		Profile victim = BanManager.getProfile(params.getTarget(), true);
		if(victim == null){
			s.sendMessage("No user " + params.getTarget() + " found.");
			return;
		}
		
		Ban ban = new Ban(victim, banner, params.getReason(), System.currentTimeMillis(), params.getDuration() > 0 ? params.getDuration() + System.currentTimeMillis() : 0);
		Ban old = BanManager.getBan(victim);
		
		if(old != null){
			if((old.isTemporary() && ban.isTemporary() && old.getExpires() >= ban.getExpires()) || (old.isTemporary() == false)){
				s.sendMessage("The user " + victim.getUser() + " has an existing ban which lasts longer than the given one.");
				return;
			}
		}
		
		BanManager.ban(victim, ban);
	}
}