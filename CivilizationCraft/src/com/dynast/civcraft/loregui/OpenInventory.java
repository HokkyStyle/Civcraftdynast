package com.dynast.civcraft.loregui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.dynast.civcraft.exception.CivException;
import com.dynast.civcraft.lorestorage.LoreGuiItem;
import com.dynast.civcraft.lorestorage.LoreGuiItemListener;
import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.main.CivLog;
import com.dynast.civcraft.threading.TaskMaster;
import com.dynast.civcraft.tutorial.CivTutorial;

public class OpenInventory implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		player.closeInventory();
		
		class SyncTaskDelayed implements Runnable {
			String playerName;
			ItemStack stack;
			
			public SyncTaskDelayed(String playerName, ItemStack stack) {
				this.playerName = playerName;
				this.stack = stack;
			}
			
			@Override
			public void run() {
				Player player;
				try {
					player = CivGlobal.getPlayer(playerName);
				} catch (CivException e) {
					e.printStackTrace();
					return;
				}
				
				switch (LoreGuiItem.getActionData(stack, "invType")) {
					case "showTutorialInventory":
						CivTutorial.showTutorialInventory(player);
						break;
					case "showCraftingHelp":
						CivTutorial.showCraftingHelp(player);
						break;
					case "showGuiInv":
						String invName = LoreGuiItem.getActionData(stack, "invName");
						Inventory inv = LoreGuiItemListener.guiInventories.get(invName);
						if (inv != null) {
							player.openInventory(inv);
						} else {
							CivLog.error("Couldn't find GUI inventory:"+invName);
						}
						break;
					case "teleportWar":


					default:
						break;
				}
			}
		}
		
		TaskMaster.syncTask(new SyncTaskDelayed(player.getName(), stack));		
	}

}
