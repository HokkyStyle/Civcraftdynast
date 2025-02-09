package com.dynast.civcraft.endgame;

import java.util.ArrayList;

import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.main.CivMessage;
import com.dynast.civcraft.object.Civilization;
import com.dynast.civcraft.sessiondb.SessionEntry;


public class EndGameCheckTask implements Runnable {

	@Override
	public void run() {
		
		/* 
		 * Run through every Civilization in the game, if one the checks pass,
		 * then declare that Civilization the winner.
		 */
		
		/* automate as much of this as possible. */
		
		/* Record winner. 
		 *  - Record Scores for all civs.
		 *  - Mark game as over and disallow score changes.
		 *  - Add top5 civs to global 'hall of fame' table.
		 *  - check that game is noArrayList<E>fore doing end game checks.
		 */
		if (CivGlobal.isCasualMode()) {
			return;
		}
		
		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup("endgame:winningCiv");
		if (entries.size() != 0) {
			CivMessage.global(entries.get(0).value);
			return;
		}
		
		for (Civilization civ : CivGlobal.getCivs()) {
			if (civ.isAdminCiv()) {
				continue;
			}
								
			/* Check for every condition. */
			for (EndGameCondition cond : EndGameCondition.endConditions) {
				if (cond.check(civ)) {
					cond.onSuccess(civ);
				} else {
					cond.onFailure(civ);
				}
			}
		}

	}

}
