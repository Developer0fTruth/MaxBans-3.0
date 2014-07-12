package org.maxgamer.maxbans.bukkit;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.database.Database;
import org.maxgamer.maxbans.database.DatabaseWatcher.DatabaseTask;

public class PlayerListener implements Listener{
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent e){
		UUID uuid = e.getPlayer().getUniqueId();
		
		//TODO: IP checks.
		
		final Profile p = BanManager.getProfile(uuid);
		if(p != null){ 
			//This guy has a history. 
			if(p.getBan() != null){
				e.disallow(Result.KICK_OTHER, p.getBan().getReason());
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
}