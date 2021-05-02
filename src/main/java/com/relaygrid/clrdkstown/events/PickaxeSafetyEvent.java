package com.relaygrid.clrdkstown.events;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
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
import com.relaygrid.clrdkstown.Keys;

public class PickaxeSafetyEvent implements Listener {
		
	private static boolean hasNoDropTag(ItemStack itemStack) {
		try {
			NamespacedKey key = new NamespacedKey(CLRDKSTown.getInstance(), Keys.CAN_BE_DROPPED);
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
	
	private static UUID getItemOwnerUUID(ItemStack itemStack) {
		try {
			NamespacedKey key = new NamespacedKey(CLRDKSTown.getInstance(), Keys.ITEM_OWNER);
			ItemMeta itemMeta = itemStack.getItemMeta();
			PersistentDataContainer container = itemMeta.getPersistentDataContainer();

			if (container.has(key, PersistentDataType.STRING)) {
				return UUID.fromString(container.get(key, PersistentDataType.STRING));
			}
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	private static boolean playerCanDropItem(Player player, ItemStack itemStack) {
		return player.hasPermission("clrdkstown.dropbypass") || !hasNoDropTag(itemStack);
	}
	
	private static boolean playerCanPickupItem(Player player, ItemStack itemStack) {
		if (player.hasPermission("clrdkstown.ownerbypass")) {
			return true;
		}
		UUID ownerUUID = getItemOwnerUUID(itemStack);
		if (ownerUUID != null && !player.getUniqueId().equals(ownerUUID)) {
			return false;
		}
		return true;
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
		
		if (!playerCanDropItem(player, cursorItem) && !clickedInv.equals(playerInv)) { //Manually moving the item
				e.setCancelled(true);
		}
		
		if (!playerCanDropItem(player, slotItem) && e.isShiftClick()) { //Shift clicking
			e.setCancelled(true);
		}
		
		if (!playerCanPickupItem(player, slotItem) && !clickedInv.equals(playerInv)) { //Clicking the item if it's not in the player inv and they don't own it
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) e.getEntity();
		if (!playerCanPickupItem(player, e.getItem().getItemStack())) {
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
					if (!playerCanDropItem(player, item)) {
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
	
