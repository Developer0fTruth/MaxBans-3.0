package org.maxgamer.maxbans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.maxgamer.maxbans.database.Database;
import org.maxgamer.maxbans.database.DatabaseWatcher.DatabaseTask;
import org.maxgamer.maxbans.punish.Ban;
import org.maxgamer.maxbans.punish.IPBan;
import org.maxgamer.maxbans.punish.Mute;

public class BanManager{
	private static Database database;
	
	private static TrieSet names;
	private static HashMap<UUID, Profile> profiles;
	private static HashMap<String, UUID> uuidCache;
	
	private static HashMap<Profile, Ban> bans;
	private static HashMap<Profile, Mute> mutes;
	private static TreeMap<IPAddress, IPBan> ipbans;
	
	private static boolean lockdown = false;
	private static String  lockdownReason = "Maintenance";
	
	public static boolean isLockdown(){
		return lockdown;
	}
	
	public static String getLockdownReason(){
		return lockdownReason;
	}
	
	public static void setLockdown(boolean lock, String reason){
		lockdown = lock;
		lockdownReason = reason;
	}
	
	public static void init(Database db) throws SQLException{
		database = db;
		Connection con = database.getConnection();
		
		names = new TrieSet();
		profiles = new HashMap<UUID, Profile>();
		uuidCache = new HashMap<String, UUID>();
		bans = new HashMap<Profile, Ban>();
		mutes = new HashMap<Profile, Mute>();
		ipbans = new TreeMap<IPAddress, IPBan>();
		
		
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
		
		ps = con.prepareStatement("SELECT * FROM " + Tables.MUTE_TABLE);
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
		
		ps = con.prepareStatement("SELECT * FROM " + Tables.IPBAN_TABLE);
		rs = ps.executeQuery();
		while(rs.next()){
			IPBan ban = new IPBan(rs.getString("range"));
			ban.load(rs);
			
			IPRange range = ban.getRange();
			
			ipbans.put(range.getStart(), ban);
			
			System.out.println("Loaded IPBan range: " + range + ", from string " + rs.getString("range")); 
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
	
	public static IPBan getIPBan(IPAddress exact){
		Entry<IPAddress, IPBan> entry = ipbans.floorEntry(exact);
		if(entry != null){
			if(exact.isGreaterThan(entry.getValue().getRange().getFinish()) == false){
				return entry.getValue();
			}
			//Else, the IP address they're searching for is not contained in that ban.
		}
		
		return null;
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
	
	public static void unban(Profile profile){
		final Ban ban = getBan(profile);
		
		bans.remove(profile);
		if(ban != null){
			database.getWatcher().queue(new DatabaseTask(){
				@Override
				public void execute(Database db) throws SQLException {
					ban.delete(db.getConnection());
				}
			});
		}
	}
	
	public static void unipban(IPAddress ip){
		final IPBan ipban = getIPBan(ip);
		
		ipbans.remove(ipban.getRange().getStart());
		if(ipban != null){
			database.getWatcher().queue(new DatabaseTask(){
				@Override
				public void execute(Database db) throws SQLException {
					ipban.delete(db.getConnection());
				}
			});
		}
	}
	
	public static void unmute(Profile profile){
		final Mute mute = getMute(profile);
		
		mutes.remove(profile);
		if(mute != null){
			database.getWatcher().queue(new DatabaseTask(){
				@Override
				public void execute(Database db) throws SQLException {
					mute.delete(db.getConnection());
				}
			});
		}
	}
	
	public static void ban(Profile profile, final Ban ban){
		final Ban old = getBan(profile);
		if(old != null){
			if((old.isTemporary() && ban.isTemporary() && old.getExpires() >= ban.getExpires()) || (old.isTemporary() == false)){
				throw new IllegalArgumentException("The user " + profile.getUser() + " already has a ban which lasts longer than the given one.");
			}
			
			database.getWatcher().queue(new DatabaseTask(){
				@Override
				public void execute(Database db) throws SQLException {
					old.delete(db.getConnection());
				}
			});
		}
		
		bans.put(profile, ban);
		database.getWatcher().queue(new DatabaseTask(){
			@Override
			public void execute(Database db) throws SQLException {
				ban.insert(db.getConnection());
			}
		});
	}
	
	public static void mute(Profile profile, final Mute mute){
		final Mute old = getMute(profile);
		if(old != null){
			if((old.isTemporary() && mute.isTemporary() && old.getExpires() >= mute.getExpires()) || (old.isTemporary() == false)){
				throw new IllegalArgumentException("The user " + profile.getUser() + " already has a mute which lasts longer than the given one.");
			}
			
			database.getWatcher().queue(new DatabaseTask(){
				@Override
				public void execute(Database db) throws SQLException {
					old.delete(db.getConnection());
				}
			});
		}
		
		mutes.put(profile, mute);
		database.getWatcher().queue(new DatabaseTask(){
			@Override
			public void execute(Database db) throws SQLException {
				mute.insert(db.getConnection());
			}
		});
	}
	
	public static void ipban(final IPBan ban){
		IPRange range = ban.getRange();
		IPBan old = ipbans.floorEntry(range.getFinish()).getValue();
		
		if(old != null){
			if(old.getRange().getFinish().isGreaterThan(range.getStart()) == false || old.getRange().getFinish().isGreaterThan(range.getFinish())){
				//Overlap.
				throw new IllegalArgumentException("IPBan overlap!");
			}
		}
		
		ipbans.put(range.getStart(), ban);
		database.getWatcher().queue(new DatabaseTask(){
			@Override
			public void execute(Database db) throws SQLException {
				ban.insert(db.getConnection());
			}
		});
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