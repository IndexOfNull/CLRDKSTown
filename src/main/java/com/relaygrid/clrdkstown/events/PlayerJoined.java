package com.relaygrid.clrdkstown.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.relaygrid.clrdkstown.CLRDKSTown;

public class PlayerJoined implements Listener {

	private CLRDKSTown pluginInstance;
	
	public PlayerJoined(CLRDKSTown instance) {
		this.pluginInstance = instance;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(ChatColor.BLUE + "Welcome " + event.getPlayer().getDisplayName());
		//event.getPlayer().openInventory(event.getPlayer().getEnderChest());
	}
	
}
