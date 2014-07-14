package org.maxgamer.maxbans.bukkit.commands;

import java.text.ParseException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Util;
import org.maxgamer.maxbans.bukkit.CommandSkeleton;
import org.maxgamer.maxbans.bukkit.MaxBans;
import org.maxgamer.maxbans.language.Lang;

public class LockdownCmd extends CommandSkeleton{

	public LockdownCmd() {
		super("lockdown");
	}

	@Override
	public void run(Profile sender, CommandSender s, String[] args) {
		boolean lock;
		
		if(args.length > 0){
			int index;
			try{
				lock = Util.parseBoolean(args[0]);
				index = 1;
			}
			catch(ParseException e){
				lock = !BanManager.isLockdown();
				index = 0;
			}
			
			StringBuilder sb = new StringBuilder();
			if(index < args.length){
				sb.append(args[index++]);
			}
			
			while(index < args.length){
				sb.append(' ');
				sb.append(args[index++]);
			}
			
			String reason = sb.toString();
			BanManager.setLockdown(lock, reason);
			String msg;
			
			if(lock) msg = Lang.get("lockdown.broadcast", "name", sender.getUser(), "reason", reason);
			else msg = Lang.get("lockdown.lockoff", "name", sender.getUser());
			
			Bukkit.broadcast(msg, "maxbans.see.broadcast");
		}
		else{
			//No args at all!
			String reason = MaxBans.getInstance().getConfig().getString("default.lockdown-reason", "Maintenance");
			lock = !BanManager.isLockdown();
			
			BanManager.setLockdown(lock, reason);
			
			String msg;
			if(lock) msg = Lang.get("lockdown.broadcast", "name", sender.getUser(), "reason", reason);
			else msg = Lang.get("lockdown.lockoff", "name", sender.getUser());
			
			Bukkit.broadcast(msg, "maxbans.see.broadcast");
		}
	}
}