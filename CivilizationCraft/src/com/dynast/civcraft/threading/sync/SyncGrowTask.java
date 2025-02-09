
package com.dynast.civcraft.threading.sync;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import com.dynast.civcraft.main.CivData;
import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.main.CivLog;
import com.dynast.civcraft.structure.Farm;
import com.dynast.civcraft.structure.farm.FarmChunk;
import com.dynast.civcraft.structure.farm.GrowBlock;
import com.dynast.civcraft.threading.sync.request.GrowRequest;
import com.dynast.civcraft.util.ItemManager;

public class SyncGrowTask implements Runnable {
	
	public static Queue<GrowRequest> requestQueue = new LinkedList<>();
	public static ReentrantLock lock;
	
	public static final int UPDATE_LIMIT = 200;
	
	public SyncGrowTask() {
		lock = new ReentrantLock();
	}
	
	@Override
	public void run() {
		if (!CivGlobal.growthEnabled) {
			return;
		}
		
		HashSet<FarmChunk> unloadedFarms = new HashSet<>();
		
		if (lock.tryLock()) {
			try {
				for (int i = 0; i < UPDATE_LIMIT; i++) {
					GrowRequest request = requestQueue.poll();
					if (request == null) {
						return;
					}
					
					if (request.farmChunk == null) {
						request.result = false;
					} else if (!request.farmChunk.getChunk().isLoaded()) {
						// This farm's chunk isn't loaded so we can't update 
						// the crops. Add the missed growths to the farms to
						// process later.
						unloadedFarms.add(request.farmChunk);
						request.result = false;

					} else {
						
						for (GrowBlock growBlock : request.growBlocks) {
							switch (growBlock.typeId) {
							case CivData.CARROTS:
							case CivData.WHEAT:
							case CivData.POTATOES:
								if ((growBlock.data-1) != ItemManager.getData(growBlock.bcoord.getBlock())) {
									// replanted??
									continue;
								}
								break;
							}
							
							if (!growBlock.spawn && ItemManager.getId(growBlock.bcoord.getBlock()) != growBlock.typeId) {
								continue;
							} else {
								if (growBlock.spawn) {
									// Only allow block to change its type if its marked as spawnable.
									ItemManager.setTypeId(growBlock.bcoord.getBlock(), growBlock.typeId);
								}
								ItemManager.setData(growBlock.bcoord.getBlock(), growBlock.data);
								request.result = true;
							}
							
						}
					}
					
					request.finished = true;
					request.condition.signalAll();
				}
				
				// increment any farms that were not loaded.
				for (FarmChunk fc : unloadedFarms) {
					fc.incrementMissedGrowthTicks();
					Farm farm = (Farm)fc.getStruct();
					farm.saveMissedGrowths();
				}
				
				
			} finally {
				lock.unlock();
			}
		} else {
			CivLog.warning("SyncGrowTask: lock busy, retrying next tick.");
		}
	}
}

