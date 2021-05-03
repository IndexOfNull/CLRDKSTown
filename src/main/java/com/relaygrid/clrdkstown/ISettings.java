package com.relaygrid.clrdkstown;

public interface ISettings {

	public void reloadConfig();
	
	public boolean pickaxeCraftingAllowed();
	
	public boolean dropToEnderchestsAllowed();
	
	public boolean dropToChestsAllowed();
	
	public boolean dropToOtherInventoriesAllowed();
	
	public boolean preventItemMovement();
	
	public boolean preventAnvilRepair();
	
	public boolean shouldIgnoreOwners();
	
	public boolean dropToGroundAllowed();
	
	public boolean allowDroppingIfNotOwned();
	
}
