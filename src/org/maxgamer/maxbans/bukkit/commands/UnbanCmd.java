package org.maxgamer.maxbans.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.CommandParameters;
import org.maxgamer.maxbans.IPAddress;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.bukkit.CommandSkeleton;
import org.maxgamer.maxbans.punish.Ban;
import org.maxgamer.maxbans.punish.IPBan;

public class UnbanCmd extends CommandSkeleton{
	public UnbanCmd() {
		super("unban");
	}

	@Override
	public void run(Profile banner, CommandSender s, String[] args) {
		CommandParameters params = new CommandParameters(args);
		
		Profile victim = BanManager.getProfile(params.getTarget(), true);
		if(victim == null){
			s.sendMessage("No user " + params.getTarget() + " found.");
			return;
		}
		
		Ban ban = BanManager.getBan(victim);
		boolean success = false;
		if(ban != null){
			BanManager.unban(victim);
			success = true;
		}
		
		if(victim.getLastIP() != null){
			IPAddress ip = new IPAddress(victim.getLastIP());
			IPBan ipban = BanManager.getIPBan(ip);
			if(ipban != null){
				BanManager.unipban(ip);
				success = true;
			}
		}
		
		if(success){
			s.sendMessage("Unbanned " + victim.getUser());
		}
		else{
			s.sendMessage("Could not locate a ban for " + victim.getUser());
		}
	}
}