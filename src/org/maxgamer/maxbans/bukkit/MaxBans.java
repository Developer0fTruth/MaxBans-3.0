package org.maxgamer.maxbans.bukkit;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.database.Database;
import org.maxgamer.maxbans.database.DatabaseCore;
import org.maxgamer.maxbans.database.MySQLCore;
import org.maxgamer.maxbans.database.SQLiteCore;

public class MaxBans extends JavaPlugin{
	private Database db;
	private PlayerListener listener;
	
	@Override
	public void onEnable(){
		saveDefaultConfig(); //Also ensures our folder is created
		
		ConfigurationSection cfg = getConfig().getConfigurationSection("database");
		
		try{
			DatabaseCore core;
			if(cfg.getString("type").equalsIgnoreCase("mysql")){
				core = new MySQLCore(cfg.getString("host"), cfg.getString("user"), cfg.getString("pass"), cfg.getString("database"), cfg.getString("port"));
			}
			else if(cfg.getString("type").equalsIgnoreCase("sqlite")){
				core = new SQLiteCore(new File(getDataFolder(), "punishments.db"));
			}
			else{
				throw new Exception("Invalid database type specified in config: " + cfg.getString("type"));
			}
			
			db = new Database(core);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("There was a database error whilst loading the plugin.");
			System.out.println("Please check your config settings are correct, and that your database is running.");
			System.out.println("Above is the stacktrace that caused the issue.");
		}
		
		try {
			BanManager.init(db);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There was an error loading the Ban Manager during startup.");
			System.out.println("Please check that your database has not been manually modified to an incorrect state.");
			System.out.println("If you continue to have this issue, you may fix it by deleting the punishments.db file in the plugin folder.");
			System.out.println("Above is a stacktrace that caused the issue.");
		}
		
		this.listener = new PlayerListener();
		getServer().getPluginManager().registerEvents(this.listener, this);
	}
	
	@Override
	public void onDisable(){
		BanManager.destroy();
		this.listener = null;
	}
	
	@Override
	public void reloadConfig(){
		super.reloadConfig();
	}
}