package org.maxgamer.maxbans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.maxgamer.maxbans.database.Database;
import org.maxgamer.maxbans.database.DatabaseWatcher.DatabaseTask;
import org.maxgamer.maxbans.punish.Ban;
import org.maxgamer.maxbans.punish.Mute;

public class BanManager{
	private static Database database;
	
	private static TrieSet names;
	private static HashMap<UUID, Profile> profiles;
	private static HashMap<String, UUID> uuidCache;
	
	private static HashMap<Profile, Ban> bans;
	private static HashMap<Profile, Mute> mutes;
	
	public static void init(Database db) throws SQLException{
		database = db;
		Connection con = database.getConnection();
		
		names = new TrieSet();
		profiles = new HashMap<UUID, Profile>();
		uuidCache = new HashMap<String, UUID>();
		bans = new HashMap<Profile, Ban>();
		mutes = new HashMap<Profile, Mute>();
		
		PreparedStatement ps = con.prepareStatement("SELECT * FROM " + Tables.PROFILE_TABLE);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			Profile p = new Profile(uuid);
			p.load(rs);
			
			String user = p.getUser().toLowerCase();
			
			System.out.println("Registering profile for " + user + " as " + uuid);
			
			profiles.put(uuid, p);
			names.add(user);
			uuidCache.put(user, uuid);
		}
		rs.close();
		ps.close();
		
		ps = con.prepareStatement("SELECT * FROM " + Tables.BAN_TABLE);
		rs = ps.executeQuery();
		while(rs.next()){
			Ban ban = new Ban(rs.getString("profile"));
			ban.load(rs);
			
			UUID uuid = UUID.fromString(rs.getString("profile"));
			Profile p = getProfile(uuid);
			
			bans.put(p, ban);
		}
		rs.close();
		ps.close();
		
		ps = con.prepareStatement("SELECT * FROM mutes " + Tables.MUTE_TABLE);
		rs = ps.executeQuery();
		while(rs.next()){
			Mute mute = new Mute(rs.getString("profile"));
			mute.load(rs);
			
			UUID uuid = UUID.fromString(rs.getString("profile"));
			Profile p = getProfile(uuid);
			
			mutes.put(p, mute);
		}
		rs.close();
		ps.close();
	}
	
	public static void destroy(){
		names = null;
		profiles = null;
		uuidCache = null;
		bans = null;
		mutes = null;
		database = null; 
	}
	
	public static Mute getMute(Profile profile){
		final Mute mute = mutes.get(profile);
		if(mute != null){
			if(mute.hasExpired()){
				database.getWatcher().queue(new DatabaseTask(){
					@Override
					public void execute(Database db) throws SQLException {
						mute.delete(db.getConnection());
					}
				});
				return null;
			}
		}
		return mute;
	}
	
	public static Ban getBan(Profile profile){
		final Ban ban = bans.get(profile);
		if(ban != null){
			if(ban.hasExpired()){
				database.getWatcher().queue(new DatabaseTask(){
					@Override
					public void execute(Database db) throws SQLException {
						ban.delete(db.getConnection());
					}
				});
				return null;
			}
		}
		return ban;
	}
	
	public static Profile getProfile(String name, boolean autocomplete){
		if(name == null) throw new NullPointerException("No name given.");
		name = name.toLowerCase();
		
		if(autocomplete){
			String full = names.nearestKey(name);
			if(full != null) name = full;
		}
		
		UUID uuid = uuidCache.get(name);
		
		return getProfile(uuid);
	}
	
	public static Profile getProfile(UUID uuid){
		return profiles.get(uuid);
	}
	
	public static void register(final Profile profile){
		UUID id = UUID.fromString(profile.getId());
		String user = profile.getUser().toLowerCase();
		
		profiles.put(id, profile);
		uuidCache.put(user, id);
		names.add(user);
		
		BanManager.getDatabase().getWatcher().queue(new DatabaseTask(){
			@Override
			public void execute(Database db) throws SQLException {
				profile.insert(db.getConnection());
			}
		});
	}
	
	public static Database getDatabase(){
		return database;
	}
}