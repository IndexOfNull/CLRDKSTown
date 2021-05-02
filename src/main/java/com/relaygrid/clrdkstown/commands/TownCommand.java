package com.relaygrid.clrdkstown.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.Lists;
import com.relaygrid.clrdkstown.CLRDKSTown;

public class TownCommand {
	
	protected transient CLRDKSTown pluginInstance; 
	
	public void setPluginInstance(CLRDKSTown instance) {
		pluginInstance = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws Exception {
		return false;
	}

	public static Player getPlayer(final Server server, final String search) throws PlayerNotFoundException {
		Player exPlayer;
		
		try {
			exPlayer = server.getPlayer(UUID.fromString(search));
		} catch (final IllegalArgumentException ex) {
			exPlayer = server.getPlayer(search);
		}
		
		if (exPlayer == null) {
			throw new PlayerNotFoundException();
		}
		
		return exPlayer;
	}
	
	public static Boolean parseBoolean(String arg) {
		if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("yes") || arg.equalsIgnoreCase("1")) {
			return true;
		} else if (arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("no") || arg.equalsIgnoreCase("0")) {
			return false;
		}
		return null;
	}


	public List<String> tabComplete(Server server, CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return Collections.emptyList();
		}
		List<String> options = getTabCompleteOptions(server, sender, label, args);
		if (options == null) {
			return null;
		}
		return StringUtil.copyPartialMatches(args[args.length - 1], options, Lists.newArrayList());
	}
	
	protected List<String> getTabCompleteOptions(Server server, CommandSender sender, String commandLabel, String[] args) {
		return getPlayers(server);
	}
	
	protected List<String> getPlayers(Server server) { //For tab completion
		Collection<? extends Player> onlinePlayers = server.getOnlinePlayers();
		List<String> playerNames = Lists.newArrayList();
		for (Player player : onlinePlayers) {
			playerNames.add(player.getName());
		}
		return playerNames;
	}
}
