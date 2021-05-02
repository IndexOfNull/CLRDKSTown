package com.relaygrid.clrdkstown.commands;

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

import com.relaygrid.clrdkstown.CLRDKSTown;
import com.relaygrid.clrdkstown.Keys;

public class Commandsetowner extends TownCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws NotEnoughArgumentsException, PlayerNotFoundException {
		
		Player player = (Player) sender;
		
		ItemStack currentItem = player.getInventory().getItemInMainHand();
		if (currentItem.getType().equals(Material.AIR)) {
			player.sendMessage(ChatColor.RED + "You must be holding something in your hand to use that command.");
			return true;
		}
		
		if (args.length < 1) {
			throw new NotEnoughArgumentsException();
		}
		
		Player targetPlayer;
		targetPlayer = getPlayer(sender.getServer(), args[0]);
		
		NamespacedKey key = new NamespacedKey(CLRDKSTown.getInstance(), Keys.ITEM_OWNER);
		ItemMeta itemMeta = currentItem.getItemMeta();
		PersistentDataContainer metaContainer = itemMeta.getPersistentDataContainer();
		if (metaContainer.has(key, PersistentDataType.STRING)) {
			metaContainer.set(key, PersistentDataType.STRING, targetPlayer.getUniqueId().toString());
			currentItem.setItemMeta(itemMeta);
			player.sendMessage(ChatColor.GREEN + "Item's owner was updated successfully.");
			return true;
		}
		player.sendMessage(ChatColor.RED + "Item does not have a droppable tag.");
		return true;
	}
	
}
