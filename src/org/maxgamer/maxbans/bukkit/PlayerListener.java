package org.maxgamer.maxbans.bukkit;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.IPAddress;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.Util;
import org.maxgamer.maxbans.database.Database;
import org.maxgamer.maxbans.database.DatabaseWatcher.DatabaseTask;
import org.maxgamer.maxbans.language.Lang;
import org.maxgamer.maxbans.punish.IPBan;

public class PlayerListener implements Listener{
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent e){
		if(BanManager.isLockdown()){
			e.disallow(Result.KICK_OTHER, Util.trim(Lang.get("lockdown.disconnect", "reason", BanManager.getLockdownReason()), 255));
			return;
		}
		
		IPAddress address = new IPAddress(e.getAddress().getHostAddress());
		IPBan ipban = BanManager.getIPBan(address);
		if(ipban != null){
			e.disallow(Result.KICK_OTHER, Util.trim(ipban.getDisconnectMessage(), 255));
			return;
		}
		
		
		UUID uuid = e.getPlayer().getUniqueId();
		
		final Profile p = BanManager.getProfile(uuid);
		if(p != null){ 
			//This guy has a history. 
			if(p.getBan() != null){
				e.disallow(Result.KICK_OTHER, Util.trim(p.getBan().getDisconnectMessage(), 255));
				return;
			} 
			
			if(e.getAddress().getHostAddress().equals(p.getLastIP()) == false){
				p.setLastIP(e.getAddress().getHostAddress());
			}
			if(e.getPlayer().getName().equals(p.getUser()) == false){
				p.setUser(e.getPlayer().getName());
			}
			p.setLastSeen(System.currentTimeMillis());
			
			BanManager.getDatabase().getWatcher().queue(new DatabaseTask(){
				@Override
				public void execute(Database db) throws SQLException { 
					p.update(db.getConnection());
				} 
			});
		}
		else{
			//Register the new profile.
			Profile profile = new Profile(uuid, e.getPlayer().getName(), e.getAddress().getHostAddress(), System.currentTimeMillis());
			BanManager.register(profile);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e){
		UUID uuid = e.getPlayer().getUniqueId();
		Profile p = BanManager.getProfile(uuid);
		
		if(p.getMute() != null){
			e.setCancelled(true);
			e.getPlayer().sendMessage(p.getMute().getMuteMessage());
		}
	}
}