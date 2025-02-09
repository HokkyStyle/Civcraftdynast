package com.dynast.civcraft.object;

import org.bukkit.entity.Player;

import com.dynast.civcraft.structure.Buildable;
import com.dynast.civcraft.util.BlockCoord;

public interface BuildableDamageBlock {
	Buildable getOwner();
	void setOwner(Buildable owner);
	Town getTown();
	Civilization getCiv();
	BlockCoord getCoord();
	void setCoord(BlockCoord coord);
	int getX();
	int getY();
	int getZ();
	String getWorldname();
	boolean isDamageable();
	void setDamageable(boolean damageable);
	boolean canDestroyOnlyDuringWar();
	boolean allowDamageNow(Player player);
}
