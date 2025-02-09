
package com.dynast.civcraft.threading.tasks;

import java.util.ArrayList;

import org.bukkit.ChunkSnapshot;

import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.structure.Windmill;
import com.dynast.civcraft.structure.farm.FarmChunk;
import com.dynast.civcraft.threading.TaskMaster;
import com.dynast.civcraft.util.ChunkCoord;

public class WindmillStartSyncTask implements Runnable {

	Windmill windmill;
	
	public WindmillStartSyncTask(Windmill windmill) {
		this.windmill = windmill;
	}
	
	@Override
	public void run() {
		/* Find adjacent farms, get their chunk snapshots and continue processing in our thread. */
		ChunkCoord cc = new ChunkCoord(windmill.getCorner());
		ArrayList<ChunkSnapshot> snapshots = new ArrayList<>();
				
		int[][] offset = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 1 }, {-1,-1 }, {-1, 1}, {1, -1} };
		for (int i = 0; i < 8; i++) {
			cc.setX(cc.getX() + offset[i][0]);
			cc.setZ(cc.getZ() + offset[i][1]);
			
			FarmChunk farmChunk = CivGlobal.getFarmChunk(cc);
			if (farmChunk != null) {
				snapshots.add(farmChunk.getChunk().getChunkSnapshot());
			}
			
			cc.setFromLocation(windmill.getCorner().getLocation());
		}
		
		
		if (snapshots.size() == 0) {
			return;
		}
		
		/* Fire off an async task to do some post processing. */
		TaskMaster.asyncTask("", new WindmillPreProcessTask(windmill, snapshots), 0);
				
	}

}
