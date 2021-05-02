package com.relaygrid.clrdkstown;

import java.util.Collections;
import java.util.List;
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
	private static final Logger LOGGER = Logger.getLogger("CLRDKSTown");
	FileConfiguration config = getConfig();
	
	@Override
	public void onEnable() {
		//Register config
		config.addDefault("test", true);
		config.options().copyDefaults(true);
		saveConfig();
		
		//Register commands
		//this.getCommand("town").setExecutor(new MainCommand());
		//this.getCommand("debugpick").setExecutor(new DebugGivePick());
		
		//Register listeners
		this.getServer().getPluginManager().registerEvents(new PickaxeSafetyEvent(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoined(this), this);
		LOGGER.log(Level.INFO, "Started CLRDKSTown!");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return onTownTabComplete(sender.getServer(), sender, command, label, args, CLRDKSTown.class.getClassLoader(), "com.relaygrid.clrdkstown.commands.Command");
	}
	
	public List<String> onTownTabComplete(Server server, CommandSender sender, Command command, String label, String[] args, ClassLoader classLoader, String commandPath) {

		final TownCommand cmd; //thanks EssentialsX for this brilliantly simple way of auto-registering commands
		try {
			cmd = (TownCommand) classLoader.loadClass(commandPath + command.getName()).newInstance();
			cmd.setPluginInstance(this);
		} catch (Exception ex) {
			sender.sendMessage("Command class not loaded or does not exist!");
			LOGGER.log(Level.SEVERE, "Command class not loaded or does not exist!");
			return Collections.emptyList();
		}
		
		try {
			return cmd.tabComplete(server, sender, command, label, args);
		} catch (Exception e) {
			return Collections.emptyList();
		}
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return onTownCommand(sender.getServer(), sender, command, label, args, CLRDKSTown.class.getClassLoader(), "com.relaygrid.clrdkstown.commands.Command");
	}
	
	public boolean onTownCommand(Server server, CommandSender sender, Command command, String label, String[] args, ClassLoader classLoader, String commandPath) {
		
		final TownCommand cmd; //thanks EssentialsX for this brilliantly simple way of auto-registering commands
		try {
			cmd = (TownCommand) classLoader.loadClass(commandPath + command.getName()).newInstance();
		} catch (Exception ex) {
			sender.sendMessage("Command class not loaded or does not exist!");
			LOGGER.log(Level.SEVERE, "Command class not loaded or does not exist!");
			return true;
		}
		
		try {
			cmd.onCommand(sender, command, label, args);
		} catch (NotEnoughArgumentsException e) {
			sender.sendMessage(command.getUsage());
		} catch (PlayerNotFoundException e) {
			sender.sendMessage("Player not found");
		} catch (BadArgumentException e) {
			sender.sendMessage("Bad argument");
		} catch (Exception e) {
			sender.sendMessage("Unknown error");
			return true;
		}

		return true;
	}
	
}
