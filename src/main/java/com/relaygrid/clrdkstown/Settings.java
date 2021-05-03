package com.relaygrid.clrdkstown;

import java.io.File;

public class Settings implements ISettings {
	private final transient CLRDKSTown pluginInstance;
	private final transient TownConfig config;
	
	public Settings(CLRDKSTown pluginInstance) {
		this.pluginInstance = pluginInstance;
		config = new TownConfig(new File(pluginInstance.getDataFolder(), "config.yml"));
		config.setConfigTemplate("/config.yml");
		reloadConfig();
	}
	
	public boolean pickaxeCraftingAllowed() {
		return config.getBoolean("disable-pickaxe-recipes", true);
	}
	
	public boolean dropToEnderchestsAllowed() {
		return config.getBoolean("drop-to-enderchests", true);
	}
	
	public boolean dropToChestsAllowed() {
		return config.getBoolean("drop-to-chests", false);
	}
	
	public boolean preventItemMovement() {
		return config.getBoolean("prevent-item-movement", true);
	}
	
	public boolean dropToOtherInventoriesAllowed() {
		return config.getBoolean("drop-to-other-inventories", false);
	}
	
	public boolean preventAnvilRepair() {
		return config.getBoolean("prevent-anvil-repair", true);
	}
	
	public boolean shouldIgnoreOwners() {
		return config.getBoolean("ignore-owners", false);
	}
	
	public boolean dropToGroundAllowed() {
		return config.getBoolean("drop-to-ground", false);
	}
	
	public boolean allowDroppingIfNotOwned() {
		return config.getBoolean("allow-drop-if-not-owned", true);
	}
	
	public void reloadConfig() {
		config.load();
	}
	
}
