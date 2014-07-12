package org.maxgamer.maxbans.test;

import java.io.File;
import java.sql.Connection;
import java.util.UUID;

import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.database.Database;
import org.maxgamer.maxbans.database.DatabaseCore;
import org.maxgamer.maxbans.database.SQLiteCore;
import org.maxgamer.maxbans.punish.Ban;
import org.maxgamer.maxbans.punish.Mute;

public class BanManagerTest{
	public static void main(String[] args){
		try{
			DatabaseCore core = new SQLiteCore(new File("punishments.db"));
			Database db = new Database(core);
			
			BanManager.init(db);
			Connection con = db.getConnection();
			Profile p = new Profile(UUID.fromString("04978f72-2c79-4c70-98e6-a23f3ea9f9a1"), "maxbans", "127.0.0.1", System.currentTimeMillis());
			
			if(p.exists(con) == false){
				System.out.println("Creating new dummy record");
				p.insert(con);
			}
			else{
				p.load(con);
				System.out.println("Profile: " + p.getId() + ", " + p.getUser() + ", " + p.getLastIP());
			}
			
			Ban ban = BanManager.getBan(p);
			if(ban != null){
				System.out.println("Ban: " + ban.getBanner() + ", " + ban.getReason() + ", " + ban.getCreated() + ", " + ban.getExpires());
			}
			else{
				System.out.println("No ban.");
			}
			
			Mute mute = BanManager.getMute(p);
			if(mute != null){
				System.out.println("Mute: " + mute.getBanner() + ", " + mute.getReason() + ", " + mute.getCreated() + ", " + mute.getExpires());
			}
			else{
				System.out.println("No mute.");
			}
			
			System.out.println("Autocomplete: " + BanManager.getProfile("m", true));
			System.out.println("No Autocomplete: " + BanManager.getProfile("maxbans", false));
			System.out.println("No Autocomplete/Invalid: " + BanManager.getProfile("maxb", false));
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}