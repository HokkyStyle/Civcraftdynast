
package com.dynast.civcraft.command.town;

import org.apache.commons.lang.WordUtils;

import com.dynast.civcraft.command.CommandBase;
import com.dynast.civcraft.config.CivSettings;
import com.dynast.civcraft.config.ConfigTownUpgrade;
import com.dynast.civcraft.exception.CivException;
import com.dynast.civcraft.main.CivMessage;
import com.dynast.civcraft.object.Town;
import com.dynast.civcraft.util.CivColor;

public class TownUpgradeCommand extends CommandBase {

	@Override
	public void init() {
		command = "/town upgrade";
		displayName = CivSettings.localize.localizedString("cmd_town_upgrade_name");
		
		commands.put("list", CivSettings.localize.localizedString("cmd_town_upgrade_listDesc"));
		commands.put("purchased", CivSettings.localize.localizedString("cmd_town_upgrade_purchasedDesc"));
		commands.put("buy", CivSettings.localize.localizedString("cmd_town_upgrade_buyDesc"));
		
	}

	public void purchased_cmd() throws CivException {
		Town town = this.getSelectedTown();
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_town_upgrade_purchasedHeading"));

		String out = "";
		for (ConfigTownUpgrade upgrade : town.getUpgrades().values()) {
			out += upgrade.name+", ";
		}
		
		CivMessage.send(sender, out);
	}
	
	private void list_upgrades(String category, Town town) throws CivException {
		if (!ConfigTownUpgrade.categories.containsKey(category.toLowerCase()) && !category.equalsIgnoreCase("all")) {
			throw new CivException(CivSettings.localize.localizedString("var_cmd_town_upgrade_listnoCat",category));
		}
				
		for (ConfigTownUpgrade upgrade : CivSettings.townUpgrades.values()) {
			if (category.equalsIgnoreCase("all") || upgrade.category.equalsIgnoreCase(category)) {	
				if (upgrade.isAvailable(town)) {
					CivMessage.send(sender, upgrade.name+" "+CivColor.LightGray+CivSettings.localize.localizedString("Cost")+" "+CivColor.Yellow+upgrade.cost);
				}
			}
		}
	}
	
	public void list_cmd() throws CivException {
		Town town = this.getSelectedTown();
		
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_town_upgrade_listHeading"));
		
		if (args.length < 2) {
			CivMessage.send(sender, "- "+CivColor.Gold+CivSettings.localize.localizedString("cmd_town_upgrade_listAllHeading")+" "+
					CivColor.LightBlue+"("+ConfigTownUpgrade.getAvailableCategoryCount("all", town)+")");
			for (String category : ConfigTownUpgrade.categories.keySet()) {
				CivMessage.send(sender, "- "+CivColor.Gold+WordUtils.capitalize(category)+
						CivColor.LightBlue+" ("+ConfigTownUpgrade.getAvailableCategoryCount(category, town)+")");
			}
			return;
		}
		
		list_upgrades(args[1], town);		
	
	}
	
	public void buy_cmd() throws CivException {
		if (args.length < 2) {
			list_upgrades("all", getSelectedTown());
			CivMessage.send(sender, CivSettings.localize.localizedString("cmd_town_upgrade_buyHeading"));
			return;
		}
		
		Town town = this.getSelectedTown();
		
		String combinedArgs = "";
		args = this.stripArgs(args, 1);
		for (String arg : args) {
			combinedArgs += arg + " ";
		}
		combinedArgs = combinedArgs.trim();
		
		ConfigTownUpgrade upgrade = CivSettings.getUpgradeByNameRegex(town, combinedArgs);
		if (upgrade == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_upgrade_buyInvalid")+" "+combinedArgs);
		}
		
		if (town.hasUpgrade(upgrade.id)) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_upgrade_buyOwned"));
		}
		
		//TODO make upgrades take time by using hammers.
		town.purchaseUpgrade(upgrade);
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_upgrade_buySuccess",upgrade.name));
	}
	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {	
		this.validMayorAssistantLeader();
	}

}
