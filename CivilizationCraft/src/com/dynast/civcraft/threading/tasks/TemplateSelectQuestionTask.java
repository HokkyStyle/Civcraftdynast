
package com.dynast.civcraft.threading.tasks;


public class TemplateSelectQuestionTask extends PlayerQuestionTask {
	
//	String playerName; /* player who is being asked a question. */
////	Town town;
////	Structure struct;
//	ArrayList<Perk> templatePerkList;
//	long timeout; /* Timeout after question expires. */
//	QuestionResponseInterface finishedFunction;
//		
//	public TemplateSelectQuestionTask(Player player, ArrayList<Perk> list, long timeout, 
//			QuestionResponseInterface finishedFunction) {
//		this.playerName = player.getName();
//		//this.town = town;
//		//this.struct = struct;
//		this.templatePerkList = list;
//		this.timeout = timeout;
//		this.finishedFunction = finishedFunction;
//	}
//	
//	
//	@Override
//	public void run() {
//		
//		Player askedPlayer;
//		try {
//			askedPlayer = CivGlobal.getPlayer(playerName);
//		} catch (CivException e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		CivMessage.sendHeading(askedPlayer, "Select Template To Use");
//		CivMessage.send(askedPlayer, "0) "+CivColor.Gold+"Default");
//		int i = 1;
//		for (Perk perk : templatePerkList) {
//			CivMessage.send(askedPlayer, i+") "+CivColor.Gold+perk.getName());
//			i = 1;
//		}
//		
//		CivMessage.send(askedPlayer, CivColor.LightGray+"Respond by typing "+CivColor.LightBlue+"/select #");
//		
//		try {
//			synchronized(this) {
//				this.wait(timeout);
//			}
//		} catch (InterruptedException e) {
//			cleanup();
//			return;
//		}
//		
//		if (responded) {
//			cleanup();
//			finishedFunction.processResponse(response);
//			return;
//		}
//		
//		CivMessage.send(askedPlayer, CivColor.LightGray+"You failed to make a selection. Default selected.");
//		cleanup();
//		finishedFunction.processResponse("0");
//		
//	}
//	
//	/* When this task finishes, remove itself from the hashtable. */
//	private void cleanup() {
//		CivGlobal.removeQuestion(playerName);
//	}
//	
}
