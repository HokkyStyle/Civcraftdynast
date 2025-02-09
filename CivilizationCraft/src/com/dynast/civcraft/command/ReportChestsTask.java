
package com.dynast.civcraft.command;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.dynast.civcraft.main.CivLog;
import com.dynast.civcraft.main.CivMessage;
import com.dynast.civcraft.threading.TaskMaster;
import com.dynast.civcraft.util.BlockCoord;
import com.dynast.civcraft.util.ChunkCoord;
import com.dynast.civcraft.util.CivColor;
import com.dynast.civcraft.util.ItemManager;

public class ReportChestsTask implements Runnable {
	public Queue<ChunkCoord> coords = new LinkedList<>();
	CommandSender sender;
	
	public ReportChestsTask(CommandSender sender, Queue<ChunkCoord> coords) {
		this.coords = coords;
		this.sender = sender;
	}

	private int countItem(Inventory inv, int id) {
		int total = 0;
		for (ItemStack stack : inv.all(ItemManager.getMaterial(id)).values()) {
			total += stack.getAmount();
		}
		
		return total;
	}
	
	@Override
	public void run() {
		ChunkCoord coord = coords.poll();
		if (coord == null) {
			CivMessage.send(sender, "Done.");
			return;
		}
		Chunk chunk = coord.getChunk();
		
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 256; y++) {
				for (int z = 0; z < 16; z++) {
					Block block = chunk.getBlock(x, y, z);
					Inventory inv = null;
					if (block.getState() instanceof Chest) {
						inv = ((Chest)block.getState()).getBlockInventory();
					} else if (block.getState() instanceof Furnace) {
						inv = ((Furnace)block.getState()).getInventory();
					} else if (block.getState() instanceof Hopper) {
						inv = ((Hopper)block.getState()).getInventory();
					}
					
					if (inv != null) {
						BlockCoord bcoord = new BlockCoord(coord.getWorldname(), (coord.getX() << 4)+x, 
								y, (coord.getZ() << 4)+z);
						
						int diamondBlocks = countItem(inv, ItemManager.getId(Material.DIAMOND_BLOCK));
						int diamonds = countItem(inv, ItemManager.getId(Material.DIAMOND));
						int goldBlocks = countItem(inv, ItemManager.getId(Material.GOLD_BLOCK));
						int gold = countItem(inv, ItemManager.getId(Material.GOLD_INGOT));
						int emeraldBlocks = countItem(inv, ItemManager.getId(Material.EMERALD_BLOCK));
						int emeralds = countItem(inv, ItemManager.getId(Material.EMERALD));
						int diamondOre = countItem(inv, ItemManager.getId(Material.DIAMOND_ORE));
						int goldOre = countItem(inv, ItemManager.getId(Material.GOLD_ORE));
						int emeraldOre = countItem(inv, ItemManager.getId(Material.EMERALD_ORE));
						
						String out = block.getType().name()+": "+CivColor.LightPurple+bcoord+CivColor.White+" DB:"+diamondBlocks+" EB:"+emeraldBlocks+" GB:"+goldBlocks+" D:"+
								diamonds+" E:"+emeralds+" G:"+gold+" DO:"+diamondOre+" EO:"+emeraldOre+" GO:"+goldOre;
						if (diamondBlocks != 0 || diamonds != 0 || goldBlocks != 0 || gold != 0 || emeraldBlocks != 0 
								|| emeralds != 0 || diamondOre != 0 || goldOre != 0 || emeraldOre != 0) {	
							CivMessage.send(sender, out);
							CivLog.info("REPORT: "+out);
						}
						inv = null;
					}
				}
			}
		}
		
		for (Entity e : chunk.getEntities()) {
			Inventory inv = null;
			
			if (e.getType() == EntityType.MINECART_CHEST) {
				StorageMinecart chest = (StorageMinecart)e;
				inv = chest.getInventory();
			}
			
			if (e.getType() == EntityType.MINECART_HOPPER) {
				HopperMinecart chest = (HopperMinecart)e;
				inv = chest.getInventory();
			}
					
			if (inv != null) {
				BlockCoord bcoord = new BlockCoord(e.getLocation());
				
				int diamondBlocks = countItem(inv, ItemManager.getId(Material.DIAMOND_BLOCK));
				int diamonds = countItem(inv, ItemManager.getId(Material.DIAMOND));
				int goldBlocks = countItem(inv, ItemManager.getId(Material.GOLD_BLOCK));
				int gold = countItem(inv, ItemManager.getId(Material.GOLD_INGOT));
				int emeraldBlocks = countItem(inv, ItemManager.getId(Material.EMERALD_BLOCK));
				int emeralds = countItem(inv, ItemManager.getId(Material.EMERALD));
				int diamondOre = countItem(inv, ItemManager.getId(Material.DIAMOND_ORE));
				int goldOre = countItem(inv, ItemManager.getId(Material.GOLD_ORE));
				int emeraldOre = countItem(inv, ItemManager.getId(Material.EMERALD_ORE));
				
				String out =  e.getType().name()+": "+CivColor.LightPurple+bcoord+CivColor.White+" DB:"+diamondBlocks+" EB:"+emeraldBlocks+" GB:"+goldBlocks+" D:"+
						diamonds+" E:"+emeralds+" G:"+gold+" DO:"+diamondOre+" EO:"+emeraldOre+" GO:"+goldOre;
				if (diamondBlocks != 0 || diamonds != 0 || goldBlocks != 0 || gold != 0 || emeraldBlocks != 0 
						|| emeralds != 0 || diamondOre != 0 || goldOre != 0 || emeraldOre != 0) {
					CivMessage.send(sender, out);
					CivLog.info("REPORT: "+out);
				}
			}
		}
		
		TaskMaster.syncTask(new ReportChestsTask(sender, coords), 5);
	}
}
