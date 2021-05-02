package com.relaygrid.clrdkstown.events;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.relaygrid.clrdkstown.CLRDKSTown;

public class PickaxeSafetyEvent implements Listener {
	
	private static boolean hasNoDropTag(Item item) {
		return playerCanDropItem(item.getItemStack());
	}
	
	private static boolean playerCanDropItem(ItemStack itemStack) {
		try {
			NamespacedKey key = new NamespacedKey(CLRDKSTown.getInstance(), "canBeDropped");
			ItemMeta itemMeta = itemStack.getItemMeta();
			PersistentDataContainer container = itemMeta.getPersistentDataContainer();

			if (container.has(key, PersistentDataType.INTEGER)) {
				if (container.get(key, PersistentDataType.INTEGER) != 1) {
					return true;
				}
			}
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	private static boolean playerCanDropItem(Player player, ItemStack itemStack) {
		return player.hasPermission("clrdkstown.dropbypass") || !playerCanDropItem(itemStack);
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		if (!playerCanDropItem(e.getPlayer(), e.getItemDrop().getItemStack())) {
			e.setCancelled(true);
		}
	}
	
	// Ensure that the player cannot move or shift-click a non-droppable item
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPickaxeClick(InventoryClickEvent e) {
		ItemStack cursorItem = e.getCursor();
		ItemStack slotItem = e.getCurrentItem();
		Inventory clickedInv = e.getClickedInventory();
		Player player = (Player) e.getWhoClicked();
		Inventory playerInv = player.getInventory();
		
		if (!playerCanDropItem(player, cursorItem)) { //Manually moving the item
			if (!clickedInv.equals(playerInv)) {
				e.setCancelled(true);
			}
		}
		
		if (!playerCanDropItem(player, slotItem) && e.isShiftClick()) { //Shift clicking
			e.setCancelled(true);
		}
		
	}
	
	// Ensures drag events are properly trapped
	@EventHandler()
	public void onPickaxeDrag(InventoryDragEvent e) {
		if (!playerCanDropItem((Player) e.getWhoClicked(), e.getOldCursor())) {
			e.setCancelled(true);
		}
	}
	
	// Ensures that the player cannot dispose of non-droppable items by keeping them in their cursor.
	@EventHandler()
	public void onInventoryClose(InventoryCloseEvent e) {
		ItemStack cursor = e.getView().getCursor();
		Player player = (Player) e.getPlayer();
		Inventory inventory = player.getInventory();
		if (!cursor.getType().equals(Material.AIR)) {
			if (!playerCanDropItem(player, cursor) && inventory.firstEmpty() == -1) {
				for (int slot = 0; slot < 36; slot++) { //0->35 are main inventory slots
					ItemStack item = inventory.getItem(slot);
					if (!playerCanDropItem(item)) {
						player.getWorld().dropItemNaturally(player.getLocation(), item);
						inventory.setItem(slot, cursor);
						e.getView().setCursor(null);
						break;
					}
				}
			}
		}
	}
	
}
	
