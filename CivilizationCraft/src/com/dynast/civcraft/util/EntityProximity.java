package com.dynast.civcraft.util;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.v1_13_R2.AxisAlignedBB;
import net.minecraft.server.v1_13_R2.AxisAlignedBB;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class EntityProximity {

	
	/*
	 * Use a NMS method to grab an axis aligned bounding box around an area to 
	 * determine which entities are within this radius. 
	 * Optionally provide an entity that is exempt from these checks.
	 * Also optionally provide a filter so we can only capture specific types of entities.
	 */
	public static LinkedList<Entity> getNearbyEntities(Entity exempt, Location loc, double radius, Class<?> filter) {
		LinkedList<Entity> entities = new LinkedList<>();
		
		double x = loc.getX()+0.5;
		double y = loc.getY()+0.5;
		double z = loc.getZ()+0.5;
		double r = radius;
		
		CraftWorld craftWorld = (CraftWorld)loc.getWorld();
		AxisAlignedBB bb = new AxisAlignedBB(x-r, y-r, z-r, x+r, y+r, z+r);
		
		List<net.minecraft.server.v1_13_R2.Entity> eList;
		if (exempt != null) {
			 eList = craftWorld.getHandle().getEntities(((CraftEntity)exempt).getHandle(), bb);
		} else {
			 eList = craftWorld.getHandle().getEntities(null, bb);
		}
		
		for (net.minecraft.server.v1_13_R2.Entity e : eList) {
			
			
			if (filter == null || (filter.isInstance(e))) {
				entities.add(e.getBukkitEntity());
			}
		}
		
		return entities;
	}
	
}
