package com.dynast.civcraft.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.dynast.civcraft.arena.ArenaTeam;
import com.dynast.civcraft.camp.Camp;
import com.dynast.civcraft.config.CivSettings;
import com.dynast.civcraft.config.ConfigMarketItem;
import com.dynast.civcraft.event.EventTimer;
import com.dynast.civcraft.exception.CivException;
import com.dynast.civcraft.exception.InvalidConfiguration;
import com.dynast.civcraft.items.BonusGoodie;
import com.dynast.civcraft.main.CivGlobal;
import com.dynast.civcraft.main.CivLog;
import com.dynast.civcraft.object.*;
import com.dynast.civcraft.permission.PermissionGroup;
import com.dynast.civcraft.randomevents.RandomEvent;
import com.dynast.civcraft.road.RoadBlock;
import com.dynast.civcraft.sessiondb.SessionDatabase;
import com.dynast.civcraft.structure.Structure;
import com.dynast.civcraft.structure.wonders.Wonder;
import com.dynast.civcraft.threading.TaskMaster;
import com.dynast.civcraft.util.BiomeCache;
import com.dynast.global.perks.PerkManager;
import com.dynast.global.perks.PerkManagerSimple;
import com.dynast.global.reports.ReportManager;
import com.dynast.global.scores.ScoreManager;
import com.jolbox.bonecp.Statistics;

public class SQL {
	
	private static String hostname = "";
	private static String port = "";
	private static String db_name = "";
	private static String username = "";
	private static String password = "";
	public static String tb_prefix = "";
	
	private static String dsn = "";

	private static Integer min_conns;
	private static Integer max_conns;
	private static Integer parts;
//	public static Connection context = null;
	
//	public static Connection global_context = null;
	private static String global_dsn = "";
	private static String global_hostname = "";
	private static String global_port = "";
	private static String global_username = "";
	private static String global_password = "";
	private static String global_db = "";
	private static Integer global_min_conns;
	private static Integer global_max_conns;
	private static Integer global_parts;
	
	public static ConnectionPool gameDatabase;
	private static ConnectionPool globalDatabase;
	private static ConnectionPool perkDatabase;

	public static void initialize() throws InvalidConfiguration, SQLException, ClassNotFoundException {
		CivLog.heading("Initializing SQL");
		
		SQL.hostname = CivSettings.getStringBase("mysql.hostname");
		SQL.port = CivSettings.getStringBase("mysql.port");
		SQL.db_name = CivSettings.getStringBase("mysql.database");
		SQL.username = CivSettings.getStringBase("mysql.username");
		SQL.password = CivSettings.getStringBase("mysql.password");
		SQL.tb_prefix = CivSettings.getStringBase("mysql.table_prefix");
		SQL.dsn = "jdbc:mysql://" + hostname + ":" + port + "/" + tb_prefix+db_name+"?useSSL=false";
		SQL.min_conns = Integer.valueOf(CivSettings.getStringBase("mysql.min_conns"));
		SQL.max_conns = Integer.valueOf(CivSettings.getStringBase("mysql.max_conns"));
		SQL.parts = Integer.valueOf(CivSettings.getStringBase("mysql.parts"));

				CivLog.info("\t Using "+SQL.hostname+":"+SQL.port+" user:"+SQL.username+" DB:"+SQL.db_name);

		CivLog.info("\t Building Connection Pool for GAME database.");
		gameDatabase = new ConnectionPool(SQL.dsn, SQL.username, SQL.password, SQL.min_conns, SQL.max_conns, SQL.parts);
		CivLog.info("\t Connected to GAME database");
		
		CivLog.heading("Initializing Global SQL Database");
		SQL.global_hostname = CivSettings.getStringBase("global_database.hostname");
		SQL.global_port = CivSettings.getStringBase("global_database.port");
		SQL.global_username = CivSettings.getStringBase("global_database.username");
		SQL.global_password = CivSettings.getStringBase("global_database.password");
		SQL.global_db = CivSettings.getStringBase("global_database.database");
		SQL.global_min_conns = Integer.valueOf(CivSettings.getStringBase("global_database.min_conns"));
		SQL.global_max_conns = Integer.valueOf(CivSettings.getStringBase("global_database.max_conns"));
		SQL.global_parts = Integer.valueOf(CivSettings.getStringBase("global_database.parts"));

		SQL.global_dsn = "jdbc:mysql://"+ SQL.global_hostname + ":" + SQL.global_port + "/" + SQL.global_db+"?useSSL=false";
		CivLog.info("\t Using GLOBAL db at:"+SQL.global_hostname+":"+SQL.global_port+" user:"+SQL.global_username+" DB:"+SQL.global_db);
		CivLog.info("\t Building Connection Pool for GLOBAL database.");
		globalDatabase = new ConnectionPool(SQL.global_dsn, SQL.global_username, SQL.global_password, SQL.global_min_conns, SQL.global_max_conns, SQL.global_parts);
		CivLog.info("\t Connected to GLOBAL database");
		
		CivGlobal.perkManager = new PerkManager();
//		if (PlatinumManager.isLegacyEnabled()) {
//			CivLog.heading("Initializing Perk/Web Database");	
//			PerkManager.hostname = CivSettings.getStringBase("perk_database.hostname");
//			PerkManager.port = CivSettings.getStringBase("perk_database.port");
//			PerkManager.db_name = CivSettings.getStringBase("perk_database.database");
//			PerkManager.username = CivSettings.getStringBase("perk_database.username");
//			PerkManager.password = CivSettings.getStringBase("perk_database.password");
//			PerkManager.dsn = "jdbc:mysql://" + PerkManager.hostname + ":" + PerkManager.port + "/" + PerkManager.db_name;
//			CivLog.info("\t Using "+PerkManager.dsn+" as PERK database.");
//			perkDatabase = new ConnectionPool(PerkManager.dsn, PerkManager.username, PerkManager.password, SQL.global_min_conns, SQL.global_max_conns, SQL.global_parts);
//			CivLog.info("\t Connected to PERK database.");
//		} else if (PlatinumManager.isEnabled()) {
			CivGlobal.perkManager = new PerkManagerSimple();
			CivGlobal.perkManager.init();
			CivLog.info("Enabled SIMPLE PerkManager");
//		}

		
		CivLog.heading("Initializing SQL Finished");
	}


	public static void initCivObjectTables() throws SQLException {	
		CivLog.heading("Building Civ Object Tables.");

		SessionDatabase.init();
		BiomeCache.init();
		Civilization.init();
		Town.init();
		Resident.init();
		Relation.init();
		TownChunk.init();
		Structure.init();
		Wonder.init();
		WallBlock.init();
		RoadBlock.init();
		PermissionGroup.init();
		TradeGood.init();
		Ruin.init();
		MobSpawner.init();
		ProtectedBlock.init();
		BonusGoodie.init();
		MissionLogger.init();
		EventTimer.init();
		Camp.init();
		ConfigMarketItem.init();
		RandomEvent.init();
		ArenaTeam.init();
		StructureSign.init();
					
		CivLog.heading("Building Global Tables!!");
		ReportManager.init();
		ScoreManager.init();
		
		CivLog.info("----- Done Building Tables ----");
		
	}
	
//	public static void globalConnect() throws SQLException {
//		if (global_context == null || global_context.isClosed()) {
//			if (SQL.global_username.equalsIgnoreCase("") && SQL.global_password.equalsIgnoreCase("")) {
//				global_context = DriverManager.getConnection(SQL.global_dsn);
//			} else {
//				global_context = DriverManager.getConnection(SQL.global_dsn, SQL.global_username, SQL.global_password);
//			}
//		}
//
//		if (global_context == null || global_context.isClosed()) {
//			throw new SQLException("Lost context to GLOBAL MYSQL server!");
//		}
//		
//		return;
//	}
	
//	public static void connect() throws SQLException {
//		if (context == null || context.isClosed()) {
//			if (SQL.username.equalsIgnoreCase("") && SQL.password.equalsIgnoreCase("")) {
//				context = DriverManager.getConnection(SQL.dsn);
//			} else {
//				context = DriverManager.getConnection(SQL.dsn, SQL.username, SQL.password);
//			}
//		}
//
//		if (context == null || context.isClosed()) {
//			throw new SQLException("Lost context to MYSQL server!");
//		}
//		
//		return;
//	}
	
	public static Connection getGameConnection() throws SQLException {
		//CivLog.debug("get connection ----> free conns:"+SQL.getGameDatabaseStats().getTotalFree()+" leased:"+SQL.getGameDatabaseStats().getTotalLeased());
//		if (SQL.getGameDatabaseStats().getTotalFree() == 0) {
//			try {
//				throw new CivException("No more free connections! Possible connection leak!");
//			} catch (CivException e) {
//				e.printStackTrace();
//			}			
//		}
		
		return gameDatabase.getConnection();
	}

	public static Statistics getGameDatabaseStats() {
		return gameDatabase.getStats();
	}
	
	public static Connection getGlobalConnection() throws SQLException {
		//CivLog.debug("get connection ----> free conns:"+SQL.getGameDatabaseStats().getTotalFree()+" leased:"+SQL.getGameDatabaseStats().getTotalLeased());
//		if (SQL.getGlobalDatabaseStats().getTotalFree() == 0) {
//			try {
//				throw new CivException("No more free connections! Possible connection leak!");
//			} catch (CivException e) {
//				e.printStackTrace();
//			}
//		}
		
		return globalDatabase.getConnection();
	}
	
	public static Statistics getGlobalDatabaseStats() {
		return globalDatabase.getStats();
	}
	
	public static Connection getPerkConnection() throws SQLException {
		//CivLog.debug("get connection ----> free conns:"+SQL.getGameDatabaseStats().getTotalFree()+" leased:"+SQL.getGameDatabaseStats().getTotalLeased());
		if (SQL.getPerkDatabaseStats().getTotalFree() == 0) {
			try {
				throw new CivException("No more free connections! Possible connection leak!");
			} catch (CivException e) {
				e.printStackTrace();
			}
		}
		
		return perkDatabase.getConnection();
	}	
	private static Statistics getPerkDatabaseStats() {
		return perkDatabase.getStats();
	}
	
	public static boolean hasTable(String name) throws SQLException {
		Connection context = null;
		ResultSet result = null;
		try {
			context = getGameConnection();
			DatabaseMetaData dbm = context.getMetaData();
			String[] types = { "TABLE" };
			
			result = dbm.getTables(null, null, SQL.tb_prefix + name, types);
			return result.next();
		} finally {
			SQL.close(result, null, context);
		}
	}
	
	public static boolean hasGlobalTable(String name) throws SQLException {
		Connection global_context = null;
		ResultSet rs = null;
		
		try {
			global_context = getGlobalConnection();
			DatabaseMetaData dbm = global_context.getMetaData();
			String[] types = { "TABLE" };
			rs = dbm.getTables(null, null, name, types);
			return rs.next();

		} finally {
			SQL.close(rs, null, global_context);
		}
	}

	public static boolean hasColumn(String tablename, String columnName) throws SQLException {
		Connection context = null;
		ResultSet result = null;
		
		try {
			context = getGameConnection();		
			DatabaseMetaData dbm = context.getMetaData();
			result = dbm.getColumns(null, null, SQL.tb_prefix + tablename, columnName);
			return result.next();
		} finally {
			SQL.close(result, null, context);
		}
	}
	
	public static void addColumn(String tablename, String columnDef) throws SQLException {		
		Connection context = null;
		PreparedStatement ps = null;
		
		try {
			String table_alter = "ALTER TABLE "+ SQL.tb_prefix + tablename +" ADD " +columnDef;
			context = getGameConnection();		
			ps = context.prepareStatement(table_alter);
			ps.execute();
			CivLog.info("\tADDED:"+columnDef);
		} finally {
			SQL.close(null, ps, context);
		}
		
	}
	
	public static boolean hasGlobalColumn(String tablename, String columnName) throws SQLException {
		Connection global_context = null;
		ResultSet rs = null;
		
		try {
			global_context = getGlobalConnection();
			DatabaseMetaData dbm = global_context.getMetaData();
			rs = dbm.getColumns(null, null, tablename, columnName);
			
		    try {
				return rs.next();
		    } finally {
		    	rs.close();
		    }
		    
		} finally {
			SQL.close(rs, null, global_context);
		}
	}
	public static void addGlobalColumn(String tablename, String columnDef) throws SQLException {
		Connection global_context = null;
		PreparedStatement ps = null;
		
		try {
			global_context = SQL.getGlobalConnection();	
			String table_alter = "ALTER TABLE "+ tablename +" ADD " +
					columnDef;
			
			ps = global_context.prepareStatement(table_alter);
			ps.execute();
			CivLog.info("\tADDED GLOBAL:"+columnDef);
		} finally {
			SQL.close(null, ps, global_context);
		}
	}
	
	public static void updateNamedObjectAsync(NamedObject obj, HashMap<String, Object> hashmap, String tablename) throws SQLException {
		TaskMaster.asyncTask("", new SQLUpdateNamedObjectTask(obj, hashmap, tablename), 0);
	}
	
	public static void updateNamedObject(SQLObject obj, HashMap<String, Object> hashmap, String tablename) throws SQLException {
		if (obj.isDeleted()) {
			return;
		}
		
		if (obj.getId() == 0) {
			obj.setId(SQL.insertNow(hashmap, tablename));
		} else {
			SQL.update(obj.getId(), hashmap, tablename);
		}	
	}
	
	public static void update(int id, HashMap<String, Object> hashmap, String tablename) throws SQLException {
		hashmap.put("id", id);
		update(hashmap, "id", tablename);
	}
	
	
	public static void update(HashMap<String,Object> hashmap, String keyname, String tablename) throws SQLException {
		Connection context = null;
		PreparedStatement ps = null;
		
		try {
			String sql = "UPDATE `" + SQL.tb_prefix + tablename + "` SET ";
			String where = " WHERE `"+keyname+"` = ?;";
			ArrayList<Object> values = new ArrayList<>();
	
			Object keyValue = hashmap.get(keyname);
			hashmap.remove(keyname);
			
			Iterator<String> keyIter = hashmap.keySet().iterator();
			while (keyIter.hasNext()) {
				String key = keyIter.next();
				
				sql += "`"+key+"` = ?";
				sql += "" + (keyIter.hasNext() ? ", " : " ");
				values.add(hashmap.get(key));
			}
			
			sql += where;
			
			context = SQL.getGameConnection();		
			ps = context.prepareStatement(sql);
					
			int i = 1;
			for (Object value : values) {
				if (value instanceof String) {
					ps.setString(i, (String) value);
				} else if (value instanceof Integer) {
					ps.setInt(i, (Integer)value);
				} else if (value instanceof Boolean) {
					ps.setBoolean(i, (Boolean)value);
				} else if (value instanceof Double) {
					ps.setDouble(i, (Double)value);
				} else if (value instanceof Float) {
					ps.setFloat(i, (Float)value);
				} else if (value instanceof Long) {
					ps.setLong(i, (Long)value);
				} else {
					ps.setObject(i, value);
				}
				i++;
			}
			
			ps.setObject(i, keyValue);
	
			if (ps.executeUpdate() == 0) {
				insertNow(hashmap, tablename);
			}
		} finally {
			SQL.close(null, ps, context);
		}
	}

	public static void insert(HashMap<String, Object> hashmap, String tablename) {
		TaskMaster.asyncTask(new SQLInsertTask(hashmap, tablename), 0);
	}
	
	public static int insertNow(HashMap<String, Object> hashmap, String tablename) throws SQLException {
		Connection context = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			String sql = "INSERT INTO " + SQL.tb_prefix + tablename + " ";
			String keycodes = "(";
			String valuecodes = " VALUES ( ";
			ArrayList<Object> values = new ArrayList<>();
			
			Iterator<String> keyIter = hashmap.keySet().iterator();
			while (keyIter.hasNext()) {
				String key = keyIter.next();
				
				keycodes += key;
				keycodes += "" + (keyIter.hasNext() ? "," : ")");
				
				valuecodes += "?";
				valuecodes += "" + (keyIter.hasNext() ? "," : ")");
				
				values.add(hashmap.get(key));
			}
			
			sql += keycodes;
			sql += valuecodes;
			
			context = SQL.getGameConnection();		
			ps = context.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			int i = 1;
			for (Object value : values) {
				if (value instanceof String) {
					ps.setString(i, (String) value);
				} else if (value instanceof Integer) {
					ps.setInt(i, (Integer)value);
				} else if (value instanceof Boolean) {
					ps.setBoolean(i, (Boolean)value);
				} else if (value instanceof Double) {
					ps.setDouble(i, (Double)value);
				} else if (value instanceof Float) {
					ps.setFloat(i, (Float)value);
				} else if (value instanceof Long) {
					ps.setLong(i, (Long)value);
				} else {
					ps.setObject(i, value);
				}
				i++;
			}
			
			ps.execute();
			int id = 0;
			rs = ps.getGeneratedKeys();
	
			while (rs.next()) {
				id = rs.getInt(1);
				break;
			}
				
			if (id == 0) {
				String name = (String)hashmap.get("name");
				if (name == null) {
					name = "Unknown";
				}
				
				CivLog.error("SQL ERROR: Saving an SQLObject returned a 0 ID! Name:"+name+" Table:"+tablename);
			}
			return id;

		} finally {
			SQL.close(rs, ps, context);
		}
	}


	public static void deleteNamedObject(SQLObject obj, String tablename) throws SQLException {
		Connection context = null;
		PreparedStatement ps = null;
		
		try {
			String sql = "DELETE FROM " + SQL.tb_prefix + tablename + " WHERE `id` = ?";
			context = SQL.getGameConnection();		
			ps = context.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, obj.getId());
			ps.execute();
			ps.close();
			obj.setDeleted(true);
		} finally {
			SQL.close(null, ps, context);
		}	
	}
	
	public static void deleteByName(String name, String tablename) throws SQLException {
		Connection context = null;
		PreparedStatement ps = null;
		
		try {
			String sql = "DELETE FROM " + SQL.tb_prefix + tablename + " WHERE `name` = ?";
			context = SQL.getGameConnection();		
			ps = context.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ps.execute();
			ps.close();
		} finally {
			SQL.close(null, ps, context);
		}
	}
	public static void makeCol(String colname, String type, String TABLE_NAME) throws SQLException {
		if (!SQL.hasColumn(TABLE_NAME, colname)) {
			CivLog.info("\tCouldn't find "+colname+" column for "+TABLE_NAME);
			SQL.addColumn(TABLE_NAME, "`"+colname+"` "+type);				
		}
	}
	
	public static void makeTable(String table_create) throws SQLException {
		Connection context = null;
		PreparedStatement ps = null;
		
		try {
			context = SQL.getGameConnection();
			ps = context.prepareStatement(table_create);
			ps.execute();
		} finally {
			SQL.close(null, ps, context);
		}

	}	
	
	public static void makeGlobalTable(String table_create) throws SQLException {
		Connection context = null;
		PreparedStatement ps = null;
		
		try {
			context = SQL.getGlobalConnection();
			ps = context.prepareStatement(table_create);
			ps.execute();
		} finally {
			SQL.close(null, ps, context);
		}
	}
	
	public static void close(ResultSet rs, PreparedStatement ps, Connection context) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (context != null) {
			try {
				context.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
