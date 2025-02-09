
package com.dynast.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import com.dynast.civcraft.components.ProjectileLightningComponent;
import com.dynast.civcraft.config.CivSettings;
import com.dynast.civcraft.exception.CivException;
import com.dynast.civcraft.exception.InvalidConfiguration;
import com.dynast.civcraft.object.Buff;
import com.dynast.civcraft.object.Town;
import com.dynast.civcraft.util.BlockCoord;

public class TeslaTower extends Structure {

	ProjectileLightningComponent teslaComponent;
	
	protected TeslaTower(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
		this.hitpoints = this.getMaxHitPoints();
	}
	
	protected TeslaTower(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public void loadSettings() {
		super.loadSettings();
		teslaComponent = new ProjectileLightningComponent(this, this.getCenterLocation().getLocation()); 
		teslaComponent.createComponent(this);
	}
	
	public String getMarkerIconName() {
		return "tesla";
	}
	
	public int getDamage() {
		double rate = 1;
//		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.FIRE_BOMB);
		if (this.getTown().getBuffManager().hasBuff("buff_trtesla")) {
			rate += this.getTown().getBuffManager().getEffectiveDouble("buff_trtesla");
		}
		return (int)(teslaComponent.getDamage()*rate);
	}
	
	@Override
	public int getMaxHitPoints() {
		double rate = 1;
		rate += this.getTown().getBuffManager().getEffectiveDouble("buff_chichen_itza_tower_hp");
		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.BARRICADE);
		return (int) (info.max_hitpoints * rate);
	}
	
//	public void setDamage(int damage) {
//		cannonComponent.setDamage(damage);
//	}


	public void setTurretLocation(BlockCoord absCoord) {
		teslaComponent.setTurretLocation(absCoord);
	}
	
	
//	@Override
//	public void fire(Location turretLoc, Location playerLoc) {
//		turretLoc = adjustTurretLocation(turretLoc, playerLoc);
//		Vector dir = getVectorBetween(playerLoc, turretLoc);
//		
//		Fireball fb = turretLoc.getWorld().spawn(turretLoc, Fireball.class);
//		fb.setDirection(dir);
//		// NOTE cannon does not like it when the dir is normalized or when velocity is set.
//		fb.setYield((float)yield);
//		CivCache.cannonBallsFired.put(fb.getUniqueId(), new CannonFiredCache(this, playerLoc, fb));
//	}
	
	@Override
	public void onCheck() throws CivException {
		try {
			double build_distance = CivSettings.getDouble(CivSettings.warConfig, "tesla_tower.build_distance");
			
			for (Town town : this.getTown().getCiv().getTowns()) {
				for (Structure struct : town.getStructures()) {
					if (struct instanceof TeslaTower) {
						BlockCoord center = struct.getCenterLocation();
						double distance = center.distance(this.getCenterLocation());
						if (distance <= build_distance) {
							throw new CivException(CivSettings.localize.localizedString("var_buildable_tooCloseToTeslaTower",(center.getX()+","+center.getY()+","+center.getZ())));
						}
					}
//					if (struct instanceof CannonTower) {
//						BlockCoord center = struct.getCenterLocation();
//						double distance = center.distance(this.getCenterLocation());
//						if (distance <= build_distance) {
//							throw new CivException(CivSettings.localize.localizedString("var_buildable_tooCloseToCannonShip",(center.getX()+","+center.getY()+","+center.getZ())));
//						}
//					}
				}
			}
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			throw new CivException(e.getMessage());
		}
		
	}
	
}
