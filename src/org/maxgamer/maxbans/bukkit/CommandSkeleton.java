package org.maxgamer.maxbans.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.Profile;

public abstract class CommandSkeleton implements CommandExecutor{
	private String name;
	
	public CommandSkeleton(String name){
		this.name = name.toLowerCase();
		MaxBans.getInstance().getCommand(name).setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if(hasPermission(s) == false){
			s.sendMessage("You don't have the required permission (maxbans." + name + ") to access that command.");
			return true;
		}
		
		Profile p;
		if(s instanceof Player){
			p = BanManager.getProfile(((Player) s).getUniqueId());
		}
		else{
			p = MaxBans.getConsole();
		}
		
		try{
			run(p, s, args);
		}
		catch(Exception e){
			if(e instanceof IllegalArgumentException){
				s.sendMessage("Command failed. Reason: " + e.getMessage());
			}
			else{
				e.printStackTrace();
				s.sendMessage("There was an unexpected error processing your command.");
				s.sendMessage("Message: '" + e.getMessage() + "', stacktrace is in the Console.");
				s.sendMessage("Please report this to the author at http://dev.bukkit.org/bukkit-plugins/MaxBans/");
				s.sendMessage("Please also attach the command used, database contents or file and config file if necessary with steps to reproduce the issue!");
				s.sendMessage("Everything little report helps when tracking down bugs!");
			}
		}
		return true;
	}
	
	public boolean hasPermission(CommandSender s){
		return s.hasPermission("maxbans." + name);
	}
	
	public abstract void run(Profile sender, CommandSender s, String[] args);
}