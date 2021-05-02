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
	
	public boolean getDisablePickaxeRecipes() {
		return config.getBoolean("disable-pickaxe-recipes");
	}
	
	public void reloadConfig() {
		config.load();
	}
	
}
