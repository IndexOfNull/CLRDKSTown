package com.relaygrid.clrdkstown.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Commandtownreload extends TownCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		pluginInstance.getSettings().reloadConfig();
		sender.sendMessage(ChatColor.GREEN + "CLRDKSTown config reloaded!");
		return true;
	}
	
}
