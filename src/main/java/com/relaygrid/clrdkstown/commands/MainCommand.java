package com.relaygrid.clrdkstown.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainCommand extends TownCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack diamond = new ItemStack(Material.DIAMOND, 4);
			player.getInventory().addItem(diamond);
		}
		return true;
	}
	
}
