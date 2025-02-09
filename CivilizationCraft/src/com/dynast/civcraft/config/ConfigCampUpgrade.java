
package com.dynast.civcraft.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.dynast.civcraft.camp.Camp;
import com.dynast.civcraft.main.CivLog;
import com.dynast.civcraft.main.CivMessage;

public class ConfigCampUpgrade {

	public String id;
	public String name;
	public double cost;
	public String action;
	public String require_upgrade = null;
	
	public static HashMap<String, Integer> categories = new HashMap<>();
	
	public static void loadConfig(FileConfiguration cfg, Map<String, ConfigCampUpgrade> upgrades) {
		upgrades.clear();
		List<Map<?, ?>> culture_levels = cfg.getMapList("upgrades");
		for (Map<?, ?> level : culture_levels) {
			ConfigCampUpgrade upgrade = new ConfigCampUpgrade();
			
			upgrade.id = (String)level.get("id");
			upgrade.name = (String)level.get("name");
			upgrade.cost = (Double)level.get("cost");
			upgrade.action = (String)level.get("action");
			upgrade.require_upgrade = (String)level.get("require_upgrade");	
			upgrades.put(upgrade.id, upgrade);
		}
		CivLog.info("Loaded "+upgrades.size()+" camp upgrades.");		
	}

	public boolean isAvailable(Camp camp) {
		if (camp.hasUpgrade(this.id)) {
			return false;
		}
		
		if (this.require_upgrade == null || this.require_upgrade.equals("")) {
			return true;
		}
		
		if (camp.hasUpgrade(this.require_upgrade)) {
			return true;
		}
		return false;
	}

	public void processAction(Camp camp) {
		
		if (this.action == null) {
			CivLog.warning("No action found for upgrade:"+this.id);
			return;
		}
		
		switch(this.action.toLowerCase()) {
		case "enable_sifter":
			camp.setSifterEnabled(true);
			CivMessage.sendCamp(camp, CivSettings.localize.localizedString("camp_upgrade_Sifter"));
			break;
		case "enable_longhouse":
			camp.setLonghouseEnabled(true);
			CivMessage.sendCamp(camp, CivSettings.localize.localizedString("camp_upgrade_longhouse"));
			break;
		case "enable_garden":
			camp.setGardenEnabled(true);
			CivMessage.sendCamp(camp, CivSettings.localize.localizedString("camp_upgrade_garden"));
			break;
		default:
			CivLog.warning(CivSettings.localize.localizedString("var_camp_upgrade_unknown",this.action,this.id));
			break;
		}
	}
	
}
