package com.dynast.civcraft.loregui;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.dynast.civcraft.config.ConfigBuildableInfo;
import com.dynast.civcraft.exception.CivException;
import com.dynast.civcraft.lorestorage.LoreGuiItem;
import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.main.CivMessage;
import com.dynast.civcraft.object.Resident;
import com.dynast.civcraft.structure.Buildable;
import com.dynast.civcraft.structurevalidation.StructureValidator;
import com.dynast.civcraft.template.Template;
import com.dynast.civcraft.threading.TaskMaster;
import com.dynast.global.perks.Perk;
import com.dynast.global.perks.components.CustomPersonalTemplate;

public class BuildWithPersonalTemplate implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
		
		ConfigBuildableInfo info = resident.pendingBuildableInfo;
		try {
			/* get the template name from the perk's CustomTemplate component. */
			String perk_id = LoreGuiItem.getActionData(stack, "perk");
			Perk perk = Perk.staticPerks.get(perk_id);
			CustomPersonalTemplate customTemplate = (CustomPersonalTemplate)perk.getComponent("CustomPersonalTemplate");
			Template tpl = customTemplate.getTemplate(player, resident.pendingBuildableInfo);
			Location centerLoc = Buildable.repositionCenterStatic(player.getLocation(), info, Template.getDirection(player.getLocation()), (double)tpl.size_x, (double)tpl.size_z);	
			TaskMaster.asyncTask(new StructureValidator(player, tpl.getFilepath(), centerLoc, resident.pendingCallback), 0);
			resident.desiredTemplate = tpl;
			player.closeInventory();
		} catch (CivException e) {
			CivMessage.sendError(player, e.getMessage());
		}
	}

}
