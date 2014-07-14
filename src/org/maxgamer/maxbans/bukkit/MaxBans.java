package org.maxgamer.maxbans.bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.maxbans.BanManager;
import org.maxgamer.maxbans.Profile;
import org.maxgamer.maxbans.bukkit.commands.BanCmd;
import org.maxgamer.maxbans.bukkit.commands.IPBanCmd;
import org.maxgamer.maxbans.bukkit.commands.LockdownCmd;
import org.maxgamer.maxbans.bukkit.commands.MuteCmd;
import org.maxgamer.maxbans.bukkit.commands.UnbanCmd;
import org.maxgamer.maxbans.bukkit.commands.UnmuteCmd;
import org.maxgamer.maxbans.database.Database;
import org.maxgamer.maxbans.database.DatabaseCore;
import org.maxgamer.maxbans.database.MySQLCore;
import org.maxgamer.maxbans.database.SQLiteCore;
import org.maxgamer.maxbans.language.Lang;

public class MaxBans extends JavaPlugin{
	private static MaxBans instance;
	private static Profile consoleProfile;
	
	public static MaxBans getInstance(){
		return instance;
	}
	
	public static Profile getConsole(){
		return consoleProfile;
	}
	
	private Database db;
	private PlayerListener listener;
	
	@Override
	public void onEnable(){
		instance = this;
		saveDefaultConfig(); //Also ensures our folder is created
		
		File messages = new File(getDataFolder(), "locale.yml");
		if(messages.exists() == false){
			try{
				messages.createNewFile();
				FileOutputStream out = new FileOutputStream(messages);
				InputStream in = getResource("locale.yml");
				int n;
				byte[] data = new byte[1024];
				while((n = in.read(data)) > 0){
					out.write(data, 0, n);
				}
				in.close();
				out.close();
			}
			catch(IOException e){
				e.printStackTrace();
				System.out.println("There was an error creating the locale.yml file.");
				System.out.println("This could imply Bukkit hasn't got permissions for the folder, or that the file system is full.");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		
		YamlConfiguration locale = new YamlConfiguration();
		try{
			locale.load(messages);
			
			ConfigurationSection cfg = locale.getConfigurationSection("messages");
			if(cfg == null){
				throw new Exception("Messages section not defined in locale.yml!");
			}
			
			Map<String, Object> map = cfg.getValues(true);
			HashMap<String, String> strings = new HashMap<String, String>();
			for(Entry<String, Object> entry : map.entrySet()){
				if(entry.getValue() instanceof String){
					String s = (String) entry.getValue();
					s = ChatColor.translateAlternateColorCodes('&', s);
					strings.put(entry.getKey(), s);
				}
			}
			
			Lang.init(strings);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("There was an error reading the locale.yml file.");
			System.out.println("This suggests that the file is most likely configured badly. Check you haven't used any tabs inside the YML file, and that the format is correct.");
			System.out.println("Alternatively, the file could be locked. Consider the above stacktrace.");
		}
		
		
		ConfigurationSection cfg = getConfig().getConfigurationSection("database");
		
		try{
			DatabaseCore core;
			if(cfg.getString("type").equalsIgnoreCase("mysql")){
				core = new MySQLCore(cfg.getString("host"), cfg.getString("user"), cfg.getString("pass"), cfg.getString("database"), cfg.getString("port"));
			} 
			else if(cfg.getString("type").equalsIgnoreCase("sqlite")){
				File file = new File(getDataFolder(), "punishments.db");
				if(file.exists() == false){
					FileOutputStream out = new FileOutputStream(file);
					InputStream in = getResource("punishments.db");
					int n;
					byte[] data = new byte[1024];
					while((n = in.read(data)) > 0){
						out.write(data, 0, n);
					}
					in.close();
					out.close();
				}
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
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		try {
			BanManager.init(db);
			
			consoleProfile = new Profile(new UUID(0L, 0L), "Console", "127.0.0.1", System.currentTimeMillis());
			if(consoleProfile.exists(db.getConnection()) == false){
				consoleProfile.insert(db.getConnection());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There was an error loading the Ban Manager during startup.");
			System.out.println("Please check that your database has not been manually modified to an incorrect state.");
			System.out.println("If you continue to have this issue, you may fix it by deleting the punishments.db file in the plugin folder.");
			System.out.println("Above is a stacktrace that caused the issue.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		this.listener = new PlayerListener();
		getServer().getPluginManager().registerEvents(this.listener, this);
		
		//When we're enabling our plugin, we are not guaranteed that the plugin has been enabled while nobody is online.
		//This could break the guarantee that BanManager.getProfile() will never return null for an online player.
		//This appends the new profile for any players which are currently online.
		for(Player player : Bukkit.getOnlinePlayers()){
			if(BanManager.getProfile(player.getUniqueId()) == null){
				Profile profile = new Profile(player.getUniqueId(), player.getName(), player.getAddress().getAddress().getHostAddress(), System.currentTimeMillis());
				BanManager.register(profile);
			}
		}
		
		new BanCmd();
		new UnbanCmd();
		new MuteCmd();
		new UnmuteCmd();
		new IPBanCmd();
		new LockdownCmd();
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