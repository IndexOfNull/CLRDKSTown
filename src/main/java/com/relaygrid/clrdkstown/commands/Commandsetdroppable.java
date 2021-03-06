package com.relaygrid.clrdkstown.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.relaygrid.clrdkstown.Keys;

public class Commandsetdroppable extends TownCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws NotEnoughArgumentsException, BadArgumentException {
		Player player = (Player) sender;
		ItemStack currentItem = player.getInventory().getItemInMainHand();
		if (currentItem.getType().equals(Material.AIR)) {
			player.sendMessage(ChatColor.RED + "You must be holding something in your hand to use that command.");
			return true;
		}
		
		if (args.length < 1) {
			throw new NotEnoughArgumentsException();
		}
		
		Boolean targetValue = parseBoolean(args[0]);
		if (targetValue == null) {
			throw new BadArgumentException("Must be true or false");
		}
		
		NamespacedKey key = new NamespacedKey(pluginInstance, Keys.CAN_BE_DROPPED);
		ItemMeta itemMeta = currentItem.getItemMeta();
		PersistentDataContainer metaContainer = itemMeta.getPersistentDataContainer();
		Integer value = metaContainer.get(key, PersistentDataType.INTEGER);
		
		if (value != null) {
			metaContainer.set(key, PersistentDataType.INTEGER, targetValue ? 1 : 0);
			currentItem.setItemMeta(itemMeta);
			if (targetValue == true) {
				player.sendMessage(ChatColor.GREEN + "Your held item can now be dropped.");
			} else {
				player.sendMessage(ChatColor.GREEN + "Your held item may no longer be dropped.");
			}
			return true;
		}
		player.sendMessage(ChatColor.RED + "Item does not have a droppable tag.");
		return true;
	}
	
	@Override
	public List<String> tabComplete(Server server, CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			return Arrays.asList("true", "false");
		}
		return Collections.emptyList();
	}
	
}
