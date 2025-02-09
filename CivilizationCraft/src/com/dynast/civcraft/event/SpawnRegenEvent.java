
package com.dynast.civcraft.event;

import java.util.Calendar;

import org.bukkit.Bukkit;

import com.dynast.civcraft.config.CivSettings;
import com.dynast.civcraft.exception.InvalidConfiguration;
import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.main.CivLog;
import com.dynast.civcraft.object.Civilization;
import com.dynast.civcraft.object.CultureChunk;
import com.dynast.civcraft.object.Town;
import com.dynast.civcraft.object.TownChunk;
import com.dynast.civcraft.threading.TaskMaster;

public class SpawnRegenEvent implements EventInterface {

	@Override
	public void process() {
		CivLog.info("TimerEvent: SpawnRegenEvent -------------------------------------");
		
		class RegenSyncTask implements Runnable {
			
			CultureChunk cc;
			
			public RegenSyncTask(CultureChunk cc) {
				this.cc = cc;
			}
			
			@Override
			public void run() {
				Bukkit.getWorld("world").regenerateChunk(cc.getChunkCoord().getX(), cc.getChunkCoord().getZ());
			}
		}
		
		int tickDelay = 0;
		for (Civilization civ : CivGlobal.getAdminCivs()) {
			if (civ.isAdminCiv()) {
				for (Town town : civ.getTowns()) {				
					for (CultureChunk cc : town.getCultureChunks()) {
						TownChunk tc = CivGlobal.getTownChunk(cc.getChunkCoord());
						if (tc == null) {
							TaskMaster.syncTask(new RegenSyncTask(cc), tickDelay);
							tickDelay += 1;
						}
					}
				}
			}
		}
				
		
	}

	@Override
	public Calendar getNextDate() throws InvalidConfiguration {
		Calendar cal = EventTimer.getCalendarInServerTimeZone();
		int regen_hour = CivSettings.getInteger(CivSettings.civConfig, "global.regen_spawn_hour");
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.add(Calendar.HOUR_OF_DAY, regen_hour);
		
		Calendar now = Calendar.getInstance();
		if (now.after(cal)) {
			cal.add(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, regen_hour);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		}
		
		return cal;
	}

}
