package com.relaygrid.clrdkstown.commands;


import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.relaygrid.clrdkstown.Keys;

public class Commanddebugpick extends TownCommand {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws NotEnoughArgumentsException, PlayerNotFoundException {
		
		Player player;
		
		if (args.length < 1) {
			player = (Player) sender;
		} else {
			player = getPlayer(sender.getServer(), args[0]);
		}

		sender.sendMessage(player.getDisplayName() + " " + player.getHealth());
		
		NamespacedKey canBeDroppedKey = new NamespacedKey(pluginInstance, Keys.CAN_BE_DROPPED);
		NamespacedKey itemOwnerKey = new NamespacedKey(pluginInstance, Keys.ITEM_OWNER);
		ItemStack stack = new ItemStack(Material.DIAMOND_PICKAXE);
		
		ItemMeta itemMeta = stack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.ITALIC + "" + ChatColor.BLUE + "Starter Pickaxe");
		itemMeta.setLore(Arrays.asList("This item may not be discarded."));
		
		PersistentDataContainer metaContainer = itemMeta.getPersistentDataContainer();
		metaContainer.set(canBeDroppedKey, PersistentDataType.INTEGER, 0);
		metaContainer.set(itemOwnerKey, PersistentDataType.STRING, player.getUniqueId().toString());
		stack.setItemMeta(itemMeta);
		
		player.getInventory().addItem(stack);
		return true;
	}
	
}
