package com.relaygrid.clrdkstown;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.relaygrid.clrdkstown.commands.*;
import com.relaygrid.clrdkstown.events.*;

public class CLRDKSTown extends JavaPlugin {
	
	public static CLRDKSTown pluginInstance;
	
	private static final Logger LOGGER = Logger.getLogger("CLRDKSTown");
	FileConfiguration config = getConfig();
	private static DebugGivePick MC = new DebugGivePick();
	private static SetDroppableCommand MC2 = new SetDroppableCommand();
	
	public static CLRDKSTown getInstance() {
		return pluginInstance;
	}
	
	@Override
	public void onEnable() {
		pluginInstance = this;
		//Register config
		config.addDefault("test", true);
		config.options().copyDefaults(true);
		saveConfig();
		
		//Register commands
		//this.getCommand("town").setExecutor(new MainCommand());
		//this.getCommand("debugpick").setExecutor(new DebugGivePick());
		
		
		//Register listeners
		this.getServer().getPluginManager().registerEvents(new PlayerJoined(), this);
		this.getServer().getPluginManager().registerEvents(new PickaxeSafetyEvent(), this);
		LOGGER.log(Level.INFO, "Started CLRDKSTown!");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return onTownCommand(sender.getServer(), sender, command, label, args);
	}
	
	public boolean onTownCommand(Server server, CommandSender sender, Command command, String label, String[] args) {
		try { //Fix this
			if (command.getName().equals("debugpick")) {
				return MC.onCommand(sender, command, label, args);
			} else {
				return MC2.onCommand(sender, command, label, args);
			}
		} catch (NotEnoughArgumentsException e) {
			sender.sendMessage(command.getUsage());
		} catch (PlayerNotFoundException e) {
			sender.sendMessage("Player not found");
		} catch (BadArgumentException e) {
			sender.sendMessage("Bad argument");
		}
		return true;
	}
	
}
