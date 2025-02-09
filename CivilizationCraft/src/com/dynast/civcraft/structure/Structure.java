package com.dynast.civcraft.structure;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dynast.civcraft.object.TownChunk;
import com.dynast.civcraft.util.ChunkCoord;
import com.dynast.civcraft.war.War;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.dynast.civcraft.components.Component;
import com.dynast.civcraft.config.CivSettings;
import com.dynast.civcraft.database.SQL;
import com.dynast.civcraft.database.SQLUpdate;
import com.dynast.civcraft.exception.CivException;
import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.main.CivLog;
import com.dynast.civcraft.main.CivMessage;
import com.dynast.civcraft.object.Resident;
import com.dynast.civcraft.object.StructureSign;
import com.dynast.civcraft.object.Town;
import com.dynast.civcraft.road.Road;
import com.dynast.civcraft.template.Template;
import com.dynast.civcraft.util.BlockCoord;
import com.dynast.civcraft.util.CivColor;

public class Structure extends Buildable {
		
	public static String TABLE_NAME = "STRUCTURES";
	public Structure(Location center, String id, Town town) throws CivException {
		
		this.info = CivSettings.structures.get(id);
		this.setTown(town);
		this.setCorner(new BlockCoord(center));
		this.hitpoints = info.max_hitpoints;

		// Disallow duplicate structures with the same hash.
		Structure struct = CivGlobal.getStructure(this.getCorner());
		if (struct != null) {
			throw new CivException(CivSettings.localize.localizedString("structure_alreadyExistsHere"));
		}
	}
	
	public Structure(ResultSet rs) throws SQLException, CivException {
		this.load(rs);
	}

	@Override
	public void onCheck() throws CivException {
		/* Override in children */
	}
	
	/*
	 * I'm being a bit lazy here, I don't want to switch on the type id in more than one place
	 * so I've overloaded this function to handle both new structures and loaded ones. 
	 * Either the center,id, and town are set (new structure being created now)
	 * or result set is not null (structure being loaded)
	 */
	private static Structure _newStructure(Location center, String id, Town town, ResultSet rs) throws CivException, SQLException {
		Structure struct;
		
		switch (id) {
		case "s_bank":
			if (rs == null) {
				struct = new Bank(center, id, town);
			} else {
				struct = new Bank(rs);
			}
			break;
		
		case "s_trommel":
			if (rs == null) {
				struct = new Trommel(center, id, town);
			} else {
				struct = new Trommel(rs);
			}
			break;	

		case "ti_fish_hatchery":
			if (rs == null) {
				struct = new FishHatchery(center, id, town);
			} else {
				struct = new FishHatchery(rs);
			}
			break;	

		case "ti_trade_ship":
			if (rs == null) {
				struct = new TradeShip(center, id, town);
			} else {
				struct = new TradeShip(rs);
			}
			break;	

		case "ti_quarry":
			if (rs == null) {
				struct = new Quarry(center, id, town);
			} else {
				struct = new Quarry(rs);
			}
			break;	
			
		case "s_mob_grinder":
			if (rs == null) {
				struct = new MobGrinder(center, id, town);
			} else {
				struct = new MobGrinder(rs);
			}
			break;	
			
		case "s_store":
			if (rs == null) {
				struct = new Store(center, id, town);
			} else {
				struct = new Store(rs);
			}
			break;
			
		case "s_stadium":
			if (rs == null) {
				struct = new Stadium(center, id, town);
			} else {
				struct = new Stadium(rs);
			}
			break;
			
		case "ti_hospital":
			if (rs == null) {
				struct = new Hospital(center, id, town);
			} else {
				struct = new Hospital(rs);
			}
			break;
		
		case "s_grocer":
			if (rs == null) {
				struct = new Grocer(center, id, town);
			} else {
				struct = new Grocer(rs);
			}
			break;

		case "s_broadcast_tower":
			if (rs == null) {
				struct = new BroadcastTower(center, id, town);
			} else {
				struct = new BroadcastTower(rs);
			}
			break;
		case "s_library":
			if (rs == null) {
				struct = new Library(center, id, town);
			} else {
				struct = new Library(rs);
			}
			break;	
			
		case "s_university":
			if (rs == null) {
				struct = new University(center, id, town);
			} else {
				struct = new University(rs);
			}
			break;	
			
		case "s_school":
			if (rs == null) {
				struct = new School(center, id, town);
			} else {
				struct = new School(rs);
			}
			break;
			
		case "s_research_lab":
			if (rs == null) {
				struct = new ResearchLab(center, id, town);
			} else {
				struct = new ResearchLab(rs);
			}
			break;	
		
		case "s_blacksmith":
			if (rs == null) {
				struct = new Blacksmith(center, id, town);
			} else {
				struct = new Blacksmith(rs);
			}
			break;	
			
		case "s_granary":
			if (rs == null) {
				struct = new Granary(center, id, town);
			} else {
				struct = new Granary(rs);
			}
			break;
			
		case "ti_cottage":
			if (rs == null) {
				struct = new Cottage(center, id, town);
			} else {
				struct = new Cottage(rs);
			}
			break;
		case "s_monument":
			if (rs == null) {
				struct = new Monument(center, id, town);
			} else {
				struct = new Monument(rs);
			}
			break;
		case "s_temple":
			if (rs == null) {
				struct = new Temple(center, id, town);
			} else {
				struct = new Temple(rs);
			}
			break;
		case "ti_mine":
			if (rs == null) {
				struct = new Mine(center, id, town);
			} else {
				struct = new Mine(rs);
			}
			break;
		case "ti_sawmill":
			if (rs == null) {
				struct = new Sawmill(center, id, town);
			} else {
				struct = new Sawmill(rs);
			}
			break;
		case "ti_factory":
			if (rs == null) {
				struct = new Factory(center, id, town);
			} else {
				struct = new Factory(rs);
			}
			break;
		case "ti_farm":
			if (rs == null) {
				struct = new Farm(center, id, town);
			} else {
				struct = new Farm(rs);
			}
			break;
		case "ti_trade_outpost":
			if (rs == null) {
				struct = new TradeOutpost(center, id, town);
			} else {
				struct = new TradeOutpost(rs);
			}
			break;
		case "ti_fishing_boat":
			if (rs == null) {
				struct = new FishingBoat(center, id, town);
			} else {
				struct = new FishingBoat(rs);
			}
			break;
		case "s_townhall":
			if (rs == null) {
				struct = new TownHall(center, id, town);
			} else {
				struct = new TownHall(rs);
			}
			break;
		// Just for backwards compatibility with old typos on existing servers:
		case "s_capital":
			if (rs == null) {
				struct = new Capitol(center, id, town);
			} else {
				struct = new Capitol(rs);
			}
			break;
		case "s_capitol":
			if (rs == null) {
				struct = new Capitol(center, id, town);
			} else {
				struct = new Capitol(rs);
			}
			break;
		case "s_arrowship":
			if (rs == null) {
				struct = new ArrowShip(center, id, town);
			} else {
				struct = new ArrowShip(rs);
			}
			break;
		case "s_arrowtower":
			if (rs == null) {
				struct = new ArrowTower(center, id, town);
			} else {
				struct = new ArrowTower(rs);
			}
			break;
		case "s_cannonship":
			if (rs == null) {
				struct = new CannonShip(center, id, town);
			} else {
				struct = new CannonShip(rs);
			}
			break;
		case "s_cannontower":
			if (rs == null) {
				struct = new CannonTower(center, id, town);
			} else {
				struct = new CannonTower(rs);
			}
			break;
		case "s_scoutship":
			if (rs == null) {
				struct = new ScoutShip(center, id, town);
			} else {
				struct = new ScoutShip(rs);
			}
			break;
		case "s_scouttower":
			if (rs == null) {
				struct = new ScoutTower(center, id, town);
			} else {
				struct = new ScoutTower(rs);
			}
			break;
		case "s_shipyard":
			if (rs == null) {
				struct = new Shipyard(center, id, town);
			} else {
				struct = new Shipyard(rs);
			}
			break;
		case "ti_wall":
			if (rs == null) {
				struct = new Wall(center, id, town);
			} else {
				struct = new Wall(rs);
			}
			break;
		case "ti_fortifiedwall":
			if (rs == null) {
				struct = new FortifiedWall(center, id, town);
			} else {
				struct = new FortifiedWall(rs);
			}
			break;
		case "ti_road":
			if (rs == null) {
				struct = new Road(center, id, town);
			} else {
				struct = new Road(rs);
			}
			break;
		case "s_barracks":
			if (rs == null) {
				struct = new Barracks(center, id, town);
			} else {
				struct = new Barracks(rs);
			}
			break;
		case "ti_windmill":
			if (rs == null) {
				struct = new Windmill(center, id, town);
			} else {
				struct = new Windmill(rs);
			}
			break;
		case "s_museum":
			if (rs == null) {
				struct = new Museum(center, id, town);
			} else {
				struct = new Museum(rs);
			}
			break;
		case "s_market":
			if (rs == null) {
				struct = new Market(center, id, town);
			} else {
				struct = new Market(rs);
			}
			break;
		case "s_stable":
			if (rs == null) {
				struct = new Stable(center, id, town);
			} else {
				struct = new Stable(rs);
			}
			break;
		case "ti_pasture":
			if (rs == null) {
				struct = new Pasture(center, id, town);
			} else {
				struct = new Pasture(rs);
			}
			break;
		case "s_lighthouse":
			if (rs == null) {
				struct = new Lighthouse(center, id, town);
			} else {
				struct = new Lighthouse(rs);
			}
			break;
		case "s_teslatower":
			if (rs == null) {
				struct = new TeslaTower(center, id, town);
			} else {
				struct = new TeslaTower(rs);
			}
			break;
		case "s_altar":
			if (rs == null) {
				struct = new Altar(center, id, town);
			} else {
				struct = new Altar(rs);
			}
			break;
		case "s_emporium":
			if (rs == null) {
				struct = new Emporium(center, id, town);
			} else {
				struct = new Emporium(rs);
			}
			break;
		case "s_mint":
			if (rs == null) {
				struct = new Mint(center, id, town);
			} else {
				struct = new Mint(rs);
			}
			break;
		case "s_hotel":
			if (rs == null) {
				struct = new Hotel(center, id, town);
			} else {
				struct = new Hotel(rs);
			}
			break;
		case "s_observatory":
			if (rs == null) {
				struct = new Observatory(center, id, town);
			} else {
				struct = new Observatory(rs);
			}
			break;
		case "s_theater":
			if (rs == null) {
				struct = new Theater(center, id, town);
			} else {
				struct = new Theater(rs);
			}
			break;
		case "s_transtesla":
			if (rs == null) {
				struct = new TransformerTesla(center, id, town);
			} else {
				struct = new TransformerTesla(rs);
			}
			break;
		case "s_millitarbase":
			if (rs == null) {
				struct = new MilitaryBase(center, id, town);
			} else {
				struct = new MilitaryBase(rs);
			}
			break;
		case "s_tradeport":
			if (rs == null) {
				struct = new TradePort(center, id, town);
			} else {
				struct = new TradePort(rs);
			}
			break;
		default:
			// This structure is generic, just create a structure type. 
			// TODO should ANY structure be generic?
			if (rs == null) {
				struct = new Structure(center, id, town);
			} else {
				struct = new Structure(rs);
			}
			break;
		}
		
		struct.loadSettings();
		
		if (rs == null) {
			struct.saveComponents();
		} else {
			struct.loadComponents();
		}
				
		return struct;
	}
	
	private void loadComponents() {
		for (Component comp : this.attachedComponents) {
			comp.onLoad();
		}
	}

	private void saveComponents() {
		for (Component comp : this.attachedComponents) {
			comp.onSave();
		}
	}

	/*
	 * Public interfaces to _newStructure. 
	 */
	public static Structure newStructure(ResultSet rs) throws CivException, SQLException {
		return _newStructure(null, rs.getString("type_id"), null, rs);
	}
	
	public static Structure newStructure(Location center, String id, Town town) throws CivException {
		try {
			return _newStructure(center, id, town, null);
		} catch (SQLException e) {
			//This should never happen here..
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void init() throws SQLException {
		if (!SQL.hasTable(TABLE_NAME)) {
			String table_create = "CREATE TABLE " + SQL.tb_prefix + TABLE_NAME+" (" + 
					"`id` int(11) unsigned NOT NULL auto_increment," +
					"`type_id` mediumtext NOT NULL," + 
					"`town_id` int(11) DEFAULT NULL," + 
					"`complete` bool NOT NULL DEFAULT '0'," +
					"`builtBlockCount` int(11) DEFAULT NULL, " +
					"`cornerBlockHash` mediumtext DEFAULT NULL," +
					"`template_name` mediumtext DEFAULT NULL, "+
					"`template_x` int(11) DEFAULT NULL, " +
					"`template_y` int(11) DEFAULT NULL, " +
					"`template_z` int(11) DEFAULT NULL, " +
					"`hitpoints` int(11) DEFAULT '100'," +
					"PRIMARY KEY (`id`)" + ")";
			
			SQL.makeTable(table_create);
			CivLog.info("Created "+TABLE_NAME+" table");
		} else {
			CivLog.info(TABLE_NAME+" table OK!");
		}		
	}

	@Override
	public void load(ResultSet rs) throws SQLException, CivException {
		this.setId(rs.getInt("id"));
		this.info = CivSettings.structures.get(rs.getString("type_id"));
		this.setTown(CivGlobal.getTownFromId(rs.getInt("town_id")));
		
		if (this.getTown() == null) {
			//if (CivGlobal.testFileFlag("cleanupDatabase")) {
				//CivLog.info("CLEANING");
			this.delete();
			//}
	//		CivLog.warning("Coudln't find town ID:"+rs.getInt("town_id")+ " for structure "+this.getDisplayName()+" ID:"+this.getId());
			throw new CivException("Coudln't find town ID:"+rs.getInt("town_id")+ " for structure "+this.getDisplayName()+" ID:"+this.getId());
			//	SQL.deleteNamedObject(this, TABLE_NAME);
			//return;
		}
		
		this.setCorner(new BlockCoord(rs.getString("cornerBlockHash")));
		this.hitpoints = rs.getInt("hitpoints");
		this.setTemplateName(rs.getString("template_name"));
		this.setTemplateX(rs.getInt("template_x"));
		this.setTemplateY(rs.getInt("template_y"));
		this.setTemplateZ(rs.getInt("template_z"));
		this.setComplete(rs.getBoolean("complete"));
		this.setBuiltBlockCount(rs.getInt("builtBlockCount"));
		
		
		this.getTown().addStructure(this);
		bindStructureBlocks();
		
		if (this.isComplete() == false) {
			try {
				this.resumeBuildFromTemplate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void save() {
		SQLUpdate.add(this);
	}

	@Override
	public void saveNow() throws SQLException {
		HashMap<String, Object> hashmap = new HashMap<>();
		hashmap.put("type_id", this.getConfigId());
		hashmap.put("town_id", this.getTown().getId());
		hashmap.put("complete", this.isComplete());
		hashmap.put("builtBlockCount", this.getBuiltBlockCount());
		hashmap.put("cornerBlockHash", this.getCorner().toString());
		hashmap.put("hitpoints", this.getHitpoints());
		hashmap.put("template_name", this.getSavedTemplatePath());
		hashmap.put("template_x", this.getTemplateX());
		hashmap.put("template_y", this.getTemplateY());
		hashmap.put("template_z", this.getTemplateZ());
		SQL.updateNamedObject(this, hashmap, TABLE_NAME);
	}
	
	public void deleteSkipUndo() throws SQLException {
		super.delete();
		
		if (this.getTown() != null) {
			/* Release trade goods if we are a trade outpost. */
			if (this instanceof TradeOutpost) {
				//TODO move to trade outpost delete..
				TradeOutpost outpost = (TradeOutpost)this;
				
				if (outpost.getGood() != null) {
					outpost.getGood().setStruct(null);
					outpost.getGood().setTown(null);
					outpost.getGood().setCiv(null);
					outpost.getGood().save();
				}
			}
			
			if (!(this instanceof Wall || this instanceof FortifiedWall || this instanceof Road)) {
				CivLog.debug("Delete with Undo! "+this.getDisplayName());
				/* Remove StructureSigns */
				for (StructureSign sign : this.getSigns()) {
					sign.delete();
				}
				try {
					this.undoFromTemplate();	
				} catch (IOException | CivException e1) {
					e1.printStackTrace();
					this.fancyDestroyStructureBlocks();
				}
				CivGlobal.removeStructure(this);
				this.getTown().removeStructure(this);
				this.unbindStructureBlocks();
				if (this instanceof Farm) {
					Farm farm = (Farm)this;
					farm.removeFarmChunk();
				}
			} else {
				CivLog.debug("Delete skip Undo! "+this.getDisplayName());
				CivGlobal.removeStructure(this);
				this.getTown().removeStructure(this);
				this.unbindStructureBlocks();
				if (this instanceof Road) {
					Road road = (Road)this;
					road.deleteOnDisband();
				} else if (this instanceof Wall) {
					Wall wall = (Wall)this;
					wall.deleteOnDisband();
				}else if (this instanceof FortifiedWall) {
					FortifiedWall wall = (FortifiedWall)this;
					wall.deleteOnDisband();
				}
			}
						
			
		}
		SQL.deleteNamedObject(this, TABLE_NAME);
		if (this.npc != null) {
			npc.despawn();
			npc.destroy();
			CitizensAPI.getNPCRegistry().deregister(npc);
		}
	}
	
	
	@Override
	public void delete() throws SQLException {
		super.delete();
		
		if (this.getTown() != null) {
			/* Release trade goods if we are a trade outpost. */
			if (this instanceof TradeOutpost) {
				//TODO move to trade outpost delete..
				TradeOutpost outpost = (TradeOutpost)this;
				
				if (outpost.getGood() != null) {
					outpost.getGood().setStruct(null);
					outpost.getGood().setTown(null);
					outpost.getGood().setCiv(null);
					outpost.getGood().save();
				}
			}
			
			try {
				this.undoFromTemplate();	
			} catch (IOException | CivException e1) {
				e1.printStackTrace();
				this.fancyDestroyStructureBlocks();
			}
						
			CivGlobal.removeStructure(this);
			this.getTown().removeStructure(this);
			this.unbindStructureBlocks();
		}
		
		SQL.deleteNamedObject(this, TABLE_NAME);
	}

	@Override
	public void updateBuildProgess() {
		if (this.getId() != 0) {
			HashMap<String, Object> struct_hm = new HashMap<>();
			struct_hm.put("id", this.getId());
			struct_hm.put("type_id", this.getConfigId());
			struct_hm.put("complete", this.isComplete());
			struct_hm.put("builtBlockCount", this.savedBlockCount);
	
			try {
				SQL.updateNamedObjectAsync(this, struct_hm, TABLE_NAME);
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		} 
	}

	public void updateSignText() {
		
	}

	@Override
	public void build(Player player, Location centerLoc, Template tpl) throws Exception {
		//this.onPreBuild(centerLoc);

		doBuild(player, centerLoc, tpl);
//		// Start building from the structure's template.
//		Template tpl;
//		try {
//			tpl = new Template();
//			tpl.initTemplate(centerLoc, this);
//		} catch (Exception e) {
//			unbind();
//			throw e;
//		}


	}

	public void doBuild(Player player, Location centerLoc, Template tpl) throws CivException, IOException, SQLException {
		// We take the player's current position and make it the 'center' by moving the center location
		// to the 'corner' of the structure.
		Location savedLocation = centerLoc.clone();
		centerLoc = repositionCenter(centerLoc, tpl.dir(), (double)tpl.size_x, (double)tpl.size_z);
		Block centerBlock = centerLoc.getBlock();

		this.setTotalBlockCount(tpl.size_x*tpl.size_y*tpl.size_z);
		// Save the template x,y,z for later. This lets us know our own dimensions.
		// this is saved in the db so it remains valid even if the template changes.
		this.setTemplateName(tpl.getFilepath());
		this.setTemplateX(tpl.size_x);
		this.setTemplateY(tpl.size_y);
		this.setTemplateZ(tpl.size_z);
		this.setTemplateAABB(new BlockCoord(centerLoc), tpl);

		TownHall oldTownHall = this.getTown().getTownHall();

		if (this instanceof TownHall && !(this instanceof Capitol) && oldTownHall != null) {
			ChunkCoord coord = new ChunkCoord(centerLoc);

			if (War.isWarTime()) {
				throw new CivException(CivSettings.localize.localizedString("townHall_preBuild_duringWar"));
			}
		}
		//this.onPreBuild(centerLoc);
		
		checkBlockPermissionsAndRestrictions(player, centerBlock, tpl.size_x, tpl.size_y, tpl.size_z, savedLocation);
		this.onPreBuild(centerLoc);
		// Before we place the blocks, give our build function a chance to work on it
		this.runOnBuild(centerLoc, tpl);
		
		// Setup undo information
		getTown().lastBuildableBuilt = this;
		tpl.saveUndoTemplate(this.getCorner().toString(), this.getTown().getName(), centerLoc);
		tpl.buildScaffolding(centerLoc);
		
		// Player's center was converted to this building's corner, save it as such.
		Resident resident = CivGlobal.getResident(player);
		resident.undoPreview();
		this.startBuildTask(tpl, centerLoc);


		bind();
		this.getTown().addStructure(this);

	}
	
	
	protected void runOnBuild(Location centerLoc, Template tpl) throws CivException {
		if (this.getOnBuildEvent() == null || this.getOnBuildEvent().equals("")) {
			return;
		}
		
		if (this.getOnBuildEvent().equals("build_farm")) {
			if (this instanceof Farm) {
				Farm farm = (Farm)this;
				farm.build_farm(centerLoc);
			}
		}
		
		if (this.getOnBuildEvent().equals("build_trade_outpost")) {
			if (this instanceof TradeOutpost) {
				TradeOutpost tradeoutpost = (TradeOutpost)this;
				tradeoutpost.build_trade_outpost(centerLoc);
			}
		}
		
		return;
	}

	public void unbind() {
		CivGlobal.removeStructure(this);
	}
	
	public void bind() {
		CivGlobal.addStructure(this);
	}

	@Override
	public String getDynmapDescription() {
		return null;
	}

	@Override
	public String getMarkerIconName() {
		// options at https://github.com/webbukkit/dynmap/wiki/Using-markers
		return "bighouse";
	}

	@Override
	public void processUndo() throws CivException {
		
		if (isTownHall()) {
			throw new CivException(CivSettings.localize.localizedString("structure_move_notCaporHall"));
		}
	
		try {
			delete();
			getTown().removeStructure(this);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CivException(CivSettings.localize.localizedString("internalDatabaseException"));
		}		
		
		CivMessage.sendTown(getTown(), CivColor.LightGreen+CivSettings.localize.localizedString("var_structure_undo_success",getDisplayName()));
				
		double refund = this.getCost();
		this.getTown().depositDirect(refund);
		CivMessage.sendTown(getTown(), CivSettings.localize.localizedString("var_structure_undo_refund",this.getTown().getName(),refund,CivSettings.CURRENCY_NAME));
		
		this.unbindStructureBlocks();
	}

	public double getRepairCost() {
		return (int)this.getCost()/2;
	}

	public void onBonusGoodieUpdate() {
		
	}

	public void onMarkerPlacement(Player player, Location next, ArrayList<Location> locs) throws CivException {		
	}
	
	@Override
	@Deprecated
	public String getName() {
		return this.getDisplayName();
	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoad() throws CivException {		
	}

	@Override
	public void onUnload() {
		
	}
	
	public void repairStructureForFree() throws CivException {
		setHitpoints(getMaxHitPoints());
		bindStructureBlocks();
		
		try {
			repairFromTemplate();
		} catch (CivException | IOException e) {
			throw new CivException(CivSettings.localize.localizedString("internalIOException"));
		}
		save();
	}
	
	public void repairStructure() throws CivException {
		if (this instanceof TownHall) {
			throw new CivException(CivSettings.localize.localizedString("structure_repair_notCaporHall"));
		}
		
		double cost = getRepairCost();
		if (!getTown().getTreasury().hasEnough(cost)) {
			throw new CivException(CivSettings.localize.localizedString("var_structure_repair_tooPoor",getTown().getName(),cost,CivSettings.CURRENCY_NAME,getDisplayName()));
		}
		
		repairStructureForFree();
		
		getTown().getTreasury().withdraw(cost);
		CivMessage.sendTown(getTown(), CivColor.Yellow+CivSettings.localize.localizedString("var_structure_repair_success",getTown().getName(),getDisplayName(),getCorner()));
	}

	@Override
	public void loadSettings() {
		
		/* Build and register all of the components. */
		List<HashMap<String,String>> compInfoList = this.getComponentInfoList();
		if (compInfoList != null) {
			for (HashMap<String,String> compInfo : compInfoList) {
				String className = "com.dynast.civcraft.components."+compInfo.get("name");
				Class<?> someClass;
				try {
					someClass = Class.forName(className);
					Component compClass = (Component)someClass.newInstance();
					compClass.setName(compInfo.get("name"));
					
					for (String key : compInfo.keySet()) {
						compClass.setAttribute(key, compInfo.get(key));
					}
					
					compClass.createComponent(this, false);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		super.loadSettings();
	}

}
