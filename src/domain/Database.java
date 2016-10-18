package domain;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.postgresql.ds.PGPoolingDataSource;

public class Database {
	
	private static Database instance = new Database();
	
	private Database() {
		initComponents();
	}
	
	public static Database getInstance() {
		return instance;
	}
	
	private String DB_USERNAME;
	private String DB_PASSWORD;
	private String DB;
	private String DB_NAME;
	private String DB_TABLE;
	
	private Statement st;
	private PreparedStatement pst;
	private Utils uts;
	
	private Properties prop;
	private PGPoolingDataSource poolSource;
	private Map<String, Integer> ruleAssociations;
	
	
	private void initComponents() {
		this.uts = Utils.getInstance();
		ruleAssociations = new HashMap<String, Integer>();
		loadProperties();
		loadPoolSource();
		//createIdTable();
		createAssociationsTable();
		initialiseCounters();
	}
	
	public Connection getConnectionListener() {
		Connection c = null;
		try {
			c = poolSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}
	
	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("database.properties");
			prop.load(is);
			DB_USERNAME = prop.getProperty("db_username");
			DB_PASSWORD = prop.getProperty("db_password");
			DB = prop.getProperty("db");
			DB_NAME = prop.getProperty("db_name");
			DB_TABLE = prop.getProperty("db_table");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadPoolSource() {
		poolSource = new PGPoolingDataSource();
		poolSource.setDataSourceName("DB Data Source");
		poolSource.setServerName(DB);
		poolSource.setDatabaseName(DB_NAME);
		poolSource.setUser(DB_USERNAME);
		poolSource.setPassword(DB_PASSWORD);
		poolSource.setMaxConnections(50);
	}
	
	private void closeConnection(Connection c) {
		if (c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void createIdTable() {
		Connection c = null;
		try {
			c = poolSource.getConnection();
			st = c.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS " + DB_TABLE + " ("
					  	  + "servioticy_id varchar(50) primary key,"
					  	  + "model varchar(30),"
					  	  + "location varchar(100),"
					  	  + "associations varchar(100),"
					  	  + "created timestamp default current_timestamp(2));";
			st.executeUpdate(create);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
	}
	
	private void createAssociationsTable() {
		Connection c = null;
		try {
			c = poolSource.getConnection();
			st = c.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS associations ("
					  	  + "location varchar(100),"
					  	  + "actuator varchar(20),"
					  	  + "sensors varchar(200),"
					  	  + "rule varchar(50),"
					  	  + "registrations_left integer,"
					  	  + "state varchar(20),"
					  	  + "PRIMARY KEY(location, actuator));";
			st.executeUpdate(create);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
	}

	
	public ArrayList<String> queryIds(int n) {
		ArrayList<String> ret = new ArrayList<String>();
		Connection c = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT servioticy_id FROM " + DB_TABLE + " ORDER BY created DESC LIMIT ?");
			pst.setInt(1, n);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				ret.add(rs.getString("servioticy_id"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return ret;
	}
	
	public String getModel(String soID) {
		Connection c = null;
		String res = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT model FROM " + DB_TABLE + " WHERE servioticy_id = ?");
			pst.setString(1, soID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) res = rs.getString("model").toLowerCase();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return res;
	}
	
	public String getLocation(String soID) {
		Connection c = null;
		String res = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT location FROM " + DB_TABLE + " WHERE servioticy_id = ?");
			pst.setString(1, soID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) res = rs.getString("location");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return res;
	}
	
	public void updateAssociations(String soID, String type, String location) {
		Connection c = null;
		String[] associations = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT associations FROM " + DB_TABLE + " WHERE servioticy_id = ?");
			pst.setString(1, soID);
			ResultSet rs = pst.executeQuery();
			if (rs.next() && !rs.getString("associations").isEmpty()) {
				associations = rs.getString("associations").split(",");
				for (String s : associations) {
					String t = s.split("/")[0];
					String actuator = s.split("/")[1];
					if (t.equals(type)) updateActuator(location, actuator, type, soID);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
	}
	
	private void updateActuator(String location, String actuator, String type, String soID) {
		Connection c = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT sensors, registrations_left FROM associations WHERE location = ? AND actuator = ?");
			pst.setString(1, location);
			pst.setString(2, actuator);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getInt("registrations_left") > 0) {
					String res = uts.addToJSON(soID, type, rs.getString("sensors"));
					pst = c.prepareStatement("UPDATE associations SET sensors = ?, registrations_left = ? WHERE location = ? AND actuator = ?");
					pst.setString(1, res);
					pst.setInt(2, rs.getInt("registrations_left") - 1);
					pst.setString(3, location);
					pst.setString(4, actuator);
					pst.executeUpdate();
				}
			}
			else {
				System.out.println("Rule not found, adding it...");
				insertNewAssociationRule(location, actuator, soID, type);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
	}
	
	private void insertNewAssociationRule(String location, String actuator, String soID, String type) {
		Connection c = null;
		String rule = uts.getRuleByActuator(ruleAssociations, actuator);
		/**
		 * Unable to find the rule
		 */
		if (rule == null) return;
		/**
		 * Rule found, insert can be done
		 */
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("INSERT INTO associations VALUES(?,?,?,?,?,?)");
			pst.setString(1, location);
			pst.setString(2, actuator);
			
			String json = uts.addToJSON(soID, type, "{}");
			pst.setString(3, json);
			pst.setString(4, rule);
			
			int regs = uts.getRegsByActuator(ruleAssociations, actuator);
			pst.setInt(5, regs - 1);
			
			/**
			 * Set initial actuator state
			 */
			pst.setString(6, "undefined");
			pst.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
	}
	
	
	
	private void initialiseCounters() {
		Connection c = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("UPDATE associations SET sensors = ?, registrations_left = ?, state = ? WHERE rule = ?");
			
			for (Map.Entry<String, Integer> entry : ruleAssociations.entrySet()) {
				pst.setString(1, "{}");
				pst.setInt(2, entry.getValue());
				pst.setString(3, "undefined");
				String rule = entry.getKey().split("/")[1];
				pst.setString(4, rule);
				pst.executeUpdate();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c);
		}
	}

}
