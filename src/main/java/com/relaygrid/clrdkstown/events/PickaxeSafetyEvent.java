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
		return hasNoDropTag(item.getItemStack());
	}
	
	private static boolean hasNoDropTag(ItemStack itemStack) {
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
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		if (hasNoDropTag(e.getItemDrop())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPickaxeClick(InventoryClickEvent e) {
		ItemStack cursorItem = e.getCursor();
		ItemStack slotItem = e.getCurrentItem();
		Inventory clickedInv = e.getClickedInventory();
		Player player = (Player) e.getWhoClicked();
		Inventory playerInv = player.getInventory();
		
		if (hasNoDropTag(cursorItem)) { //Manually moving the item
			if (!clickedInv.equals(playerInv)) {
				e.setCancelled(true);
			}
		}
		
		if (hasNoDropTag(slotItem) && e.isShiftClick()) { //Shift clicking
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler()
	public void onPickaxeDrag(InventoryDragEvent e) {
		if (hasNoDropTag(e.getOldCursor())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler()
	public void onInventoryClose(InventoryCloseEvent e) {
		ItemStack cursor = e.getView().getCursor();
		Player player = (Player) e.getPlayer();
		Inventory inventory = player.getInventory();
		if (!cursor.getType().equals(Material.AIR)) {
			if (hasNoDropTag(cursor) && inventory.firstEmpty() == -1) {
				for (int slot = 0; slot < 36; slot++) { //0->35 are main inventory slots
					ItemStack item = inventory.getItem(slot);
					if (!hasNoDropTag(item)) {
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
	
