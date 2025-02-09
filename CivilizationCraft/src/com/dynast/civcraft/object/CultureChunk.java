
package com.dynast.civcraft.object;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.dynast.civcraft.components.Attribute;
import com.dynast.civcraft.components.AttributeBiomeBase;
import com.dynast.civcraft.components.AttributeBiomeRadiusPerLevel;
import com.dynast.civcraft.components.Component;
import com.dynast.civcraft.config.CivSettings;
import com.dynast.civcraft.config.ConfigCultureBiomeInfo;
import com.dynast.civcraft.config.ConfigCultureLevel;
import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.main.CivMessage;
import com.dynast.civcraft.util.BiomeCache;
import com.dynast.civcraft.util.ChunkCoord;
import com.dynast.civcraft.util.CivColor;

public class CultureChunk {

	private Town town;
	private ChunkCoord chunkCoord;
	private int distance = 0;
	private Biome biome = null;
	
	public CultureChunk(Town town, ChunkCoord coord) {
		this.town = town;
		this.chunkCoord = coord;
		biome = BiomeCache.getBiome(this);
	}
	public Civilization getCiv() {
		return town.getCiv();
	}

	public Town getTown() {
		return town;
	}
	public void setTown(Town town) {
		this.town = town;
	}
	
	public ChunkCoord getChunkCoord() {
		return chunkCoord;
	}
	public void setChunkCoord(ChunkCoord chunkCoord) {
		this.chunkCoord = chunkCoord;
	}
	public int getDistanceToNearestEdge(ArrayList<TownChunk> edges) {
		int distance = Integer.MAX_VALUE;
		
		for (TownChunk tc : edges) {
			int tmp = tc.getChunkCoord().manhattanDistance(this.chunkCoord);
			if (tmp < distance) {
				distance = tmp;
			}
		}
		
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public String getOnLeaveString() {
		return CivColor.LightPurple+CivSettings.localize.localizedString("var_cultureLeaveMsg",town.getCiv().getName());
	}
	
	public String getOnEnterString() {
		return CivColor.LightPurple+CivSettings.localize.localizedString("var_cultureEnterMsg",town.getCiv().getName());
	}
	public double getPower() {
		// power = max/(distance^2).
		// if distance == 0, power = DOUBLEMAX;
		
		if (this.distance == 0) {
			return Double.MAX_VALUE;
		}
		
		ConfigCultureLevel clc = CivSettings.cultureLevels.get(getTown().getCultureLevel());
		double power = clc.amount / (Math.pow(distance, 2));
		
		return power;
	}
	
	public Biome getBiome() {
		return biome;
	}
	public void setBiome(Biome biome) {
		this.biome = biome;
	}
	
	@Override
	public String toString() {
		return this.chunkCoord.toString();
	}
	
	public ConfigCultureBiomeInfo getCultureBiomeInfo() {
		if (this.biome != null) {
			ConfigCultureBiomeInfo info = CivSettings.getCultureBiome(this.biome.name());
			return info;
		} else {
			// This can happen within 1 tick of the chunk being created, that's OK. 
			return CivSettings.getCultureBiome("UNKNOWN");
		}
	}
	
	public double getCoins() {
		return getCultureBiomeInfo().coins+getAdditionalAttributes(Attribute.TypeKeys.COINS.name());
	}
	
	public double getHappiness() {
		return getCultureBiomeInfo().happiness+getAdditionalAttributes(Attribute.TypeKeys.HAPPINESS.name());
	}
	
	public double getHammers() {
		//CivLog.debug("getting hammers...");
		return getCultureBiomeInfo().hammers+getAdditionalAttributes(Attribute.TypeKeys.HAMMERS.name());
	}
	
	public double getGrowth() {
		return getCultureBiomeInfo().growth+getAdditionalAttributes(Attribute.TypeKeys.GROWTH.name());
	}
	
	public double getBeakers() {		
		return getCultureBiomeInfo().beakers+getAdditionalAttributes(Attribute.TypeKeys.BEAKERS.name());
	}
	
	private double getAdditionalAttributes(String attrType) {
		if (getBiome() == null) {
			return 0.0;
		}
		
		Component.componentsLock.lock();
		try {
			ArrayList<Component> attrs = Component.componentsByType.get("AttributeBiomeBase");
			double total = 0;
			
			if (attrs == null) {
				return total;
			}
	
			for (Component comp : attrs) {
				if (comp instanceof AttributeBiomeRadiusPerLevel) {
				}
				
				if (comp instanceof AttributeBiomeBase) {
					AttributeBiomeBase attrComp = (AttributeBiomeBase)comp;
					if (attrComp.getAttribute().equals(attrType)) {
						total += attrComp.getGenerated(this);
					}
				}
			}
			return total;
		} finally {
			Component.componentsLock.unlock();
		}
	}
	
	public static void showInfo(Player player) {
	//	Biome biome = player.getLocation().getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
		Biome biome = getBiomeFromLocation(player.getLocation());
		
		CultureChunk cc = CivGlobal.getCultureChunk(new ChunkCoord(player.getLocation()));
		ConfigCultureBiomeInfo info = CivSettings.getCultureBiome(biome.name());
	//	CivLog.debug("showing info.");
		
		if (cc == null) {
			CivMessage.send(player, CivColor.LightPurple+biome.name()+
					CivColor.Green+" "+CivSettings.localize.localizedString("Coins")+" "+CivColor.LightGreen+info.coins+
					CivColor.Green+" "+CivSettings.localize.localizedString("Happiness")+" "+CivColor.LightGreen+info.happiness+
					CivColor.Green+" "+CivSettings.localize.localizedString("Hammers")+" "+CivColor.LightGreen+info.hammers+
					CivColor.Green+" "+CivSettings.localize.localizedString("Growth")+" "+CivColor.LightGreen+info.growth+				
					CivColor.Green+" "+CivSettings.localize.localizedString("Beakers")+" "+CivColor.LightGreen+info.beakers);
		} else {
			CivMessage.send(player, CivColor.LightPurple+biome.name()+
					CivColor.Green+" "+CivSettings.localize.localizedString("Coins")+" "+CivColor.LightGreen+cc.getCoins()+
					CivColor.Green+" "+CivSettings.localize.localizedString("Happiness")+" "+CivColor.LightGreen+info.happiness+
					CivColor.Green+" "+CivSettings.localize.localizedString("Hammers")+" "+CivColor.LightGreen+info.hammers+
					CivColor.Green+" "+CivSettings.localize.localizedString("Growth")+" "+CivColor.LightGreen+info.growth+				
					CivColor.Green+" "+CivSettings.localize.localizedString("Beakers")+" "+CivColor.LightGreen+info.beakers);
		}

	}
	
	public static Biome getBiomeFromLocation(Location loc) {
		Block block = loc.getChunk().getBlock(0, 0, 0);
		return block.getBiome();
	}
	
	
}
