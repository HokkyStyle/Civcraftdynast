package com.dynast.civcraft.items.components;

import gpl.AttributeUtil;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.dynast.civcraft.util.BlockCoord;
import com.dynast.civcraft.util.ItemManager;

public class NoCauldronWash extends ItemComponent {

	@Override
	public void onPrepareCreate(AttributeUtil attrUtil) {
	}

	
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (!event.hasBlock()) {
				return;
			}
						
			BlockCoord bcoord = new BlockCoord(event.getClickedBlock());
						
			if (ItemManager.getId(bcoord.getBlock()) == ItemManager.getId(Material.CAULDRON)) {			
				event.getPlayer().updateInventory();
				event.setCancelled(true);
				return;
			}
		}
	}
}