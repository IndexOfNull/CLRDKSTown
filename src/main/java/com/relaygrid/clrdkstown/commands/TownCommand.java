package com.relaygrid.clrdkstown.commands;

import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TownCommand {

	protected boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws Exception {
		return false;
	}
	
	public static Player getPlayer(final Server server, final String search, final boolean getOffline) throws PlayerNotFoundException {
		Player exPlayer;
		
		try {
			exPlayer = server.getPlayer(UUID.fromString(search));
		} catch (final IllegalArgumentException ex) {
			if (getOffline) {
				exPlayer = server.getPlayerExact(search);
			} else {
				exPlayer = server.getPlayer(search);
			}
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
	
}
