package com.relaygrid.clrdkstown.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetDroppableCommand extends TownCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		ItemStack currentItem = player.getInventory().getItemInMainHand();
		if (currentItem.getType().equals(Material.AIR)) {
			player.sendMessage("You must be holding something in your hand to use that command.");
			return true;
		}
		return true;
	}
	
}
