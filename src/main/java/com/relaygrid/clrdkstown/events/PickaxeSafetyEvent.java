package com.relaygrid.clrdkstown.events;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.relaygrid.clrdkstown.CLRDKSTown;
import com.relaygrid.clrdkstown.ISettings;
import com.relaygrid.clrdkstown.Keys;

import net.md_5.bungee.api.ChatColor;

public class PickaxeSafetyEvent implements Listener {
		
	private CLRDKSTown pluginInstance;
	
	public PickaxeSafetyEvent(CLRDKSTown instance) {
		this.pluginInstance = instance;
	}
	
	private boolean hasNoDropTag(ItemStack itemStack) {
		try {
			NamespacedKey key = new NamespacedKey(pluginInstance, Keys.CAN_BE_DROPPED);
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
	
	private UUID getItemOwnerUUID(ItemStack itemStack) {
		try {
			NamespacedKey key = new NamespacedKey(pluginInstance, Keys.ITEM_OWNER);
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
	
	private boolean playerCanDropItem(Player player, ItemStack itemStack) {
		return player.hasPermission("clrdkstown.dropbypass") || !hasNoDropTag(itemStack);
	}
	
	private boolean playerOwnsItem(Player player, ItemStack itemStack) {
		if (player.hasPermission("clrdkstown.ownerbypass") || pluginInstance.getSettings().shouldIgnoreOwners()) {
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
		if (!playerOwnsItem(e.getPlayer(), e.getItemDrop().getItemStack()) && pluginInstance.getSettings().allowDroppingIfNotOwned()) {
			return;
		}
		if (!playerCanDropItem(e.getPlayer(), e.getItemDrop().getItemStack()) && !pluginInstance.getSettings().dropToGroundAllowed()) {
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
		Inventory topInv = e.getView().getTopInventory();
		
		ISettings settings = pluginInstance.getSettings();
		

		Boolean ownsItem;
		Boolean canDropItem;
		if (Arrays.asList(InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE).contains(e.getAction())) {
			
			ownsItem = playerOwnsItem(player, cursorItem);
			canDropItem = playerCanDropItem(player, cursorItem);
		} else {
			ownsItem = playerOwnsItem(player, slotItem);
			canDropItem = playerCanDropItem(player, slotItem);
		}
		
		if (clickedInv == null) {
			e.setCancelled(true);
			return;
		}
		
		boolean leavingPlayer = false;
		if ((clickedInv.equals(playerInv) && e.isShiftClick()) || (!clickedInv.equals(playerInv) && !cursorItem.getType().equals(Material.AIR))) { //If the user is placing something in the other inv or their shiftclicking theirs
			leavingPlayer = true;
		}
		
		if (settings.dropToEnderchestsAllowed() && topInv.getType().equals(InventoryType.ENDER_CHEST)) { //If we allow enderchest moving
			if (leavingPlayer && !ownsItem) { //Make sure the user isn't depositing someone else's stuff
				e.setCancelled(true);
			}
			return;
		}
		
		if (settings.dropToChestsAllowed() && topInv.getType().equals(InventoryType.CHEST)) { //If we can't drop to chests or if we're not working with a chest
			if (clickedInv.equals(topInv) && !ownsItem) {
				e.setCancelled(true);
			}
			if (!ownsItem && !leavingPlayer) { //Prevent people from swiping other people's stuff
				e.setCancelled(true);
			}
			return;
		}
		
		if (!settings.preventAnvilRepair() && topInv.getType().equals(InventoryType.ANVIL)) {
			return;
		}

		if (settings.dropToOtherInventoriesAllowed()) { //If we can't drop to chests or if we're not working with a chest
			if (clickedInv.equals(topInv) && !ownsItem) {
				e.setCancelled(true);
			}
			if (!ownsItem && !leavingPlayer) { //Prevent people from swiping other people's stuff
				e.setCancelled(true);
			}
			return;
		}
		
		if ((leavingPlayer && !canDropItem) || (!leavingPlayer && !ownsItem) || ((clickedInv.equals(topInv) && !ownsItem))) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) e.getEntity();
		if (!playerOwnsItem(player, e.getItem().getItemStack())) {
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
					if (playerCanDropItem(player, item)) {
						player.getWorld().dropItemNaturally(player.getLocation(), item);
						inventory.setItem(slot, cursor);
						e.getView().setCursor(null);
						break;
					}
				}
			}
		}
	}
	
	@EventHandler()
	public void onAnvilPrepare(PrepareAnvilEvent e) {
		if (pluginInstance.getSettings().preventAnvilRepair()) {
			Inventory inv = e.getView().getTopInventory();
			for (ItemStack item : inv.getContents()) {
				if (!playerCanDropItem((Player) e.getView().getPlayer(), item)) {
					e.setResult(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@EventHandler()
	public void onCraftItem(PrepareItemCraftEvent e) {
		if (e.isRepair() || !pluginInstance.getSettings().pickaxeCraftingAllowed()) {
			return;
		}
		Recipe recipe = e.getRecipe();
		if (recipe != null) {
			Material itemType = recipe.getResult().getType();
			if (itemType == Material.WOODEN_PICKAXE || itemType == Material.IRON_PICKAXE || itemType == Material.GOLDEN_PICKAXE || itemType == Material.DIAMOND_PICKAXE || itemType == Material.NETHERITE_PICKAXE) {
				e.getInventory().setResult(new ItemStack(Material.AIR));
				for (HumanEntity he : e.getViewers()) {
					if (he instanceof Player) {
						((Player) he).sendMessage(ChatColor.RED + "You cannot craft that item.");
					}
				}
			}
		}
	}
	
	@EventHandler()
	public void onInventoryMove(InventoryMoveItemEvent e) { //Contrary to what you may believe, this does not fire for users
		if (hasNoDropTag(e.getItem()) && pluginInstance.getSettings().preventItemMovement()) {
			e.setCancelled(true);
		}
	}
	
}
	
