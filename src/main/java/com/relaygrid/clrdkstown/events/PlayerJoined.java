package com.relaygrid.clrdkstown.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoined implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(ChatColor.BLUE + "Welcome " + event.getPlayer().getDisplayName());
		//event.getPlayer().openInventory(event.getPlayer().getEnderChest());
	}
	
}
