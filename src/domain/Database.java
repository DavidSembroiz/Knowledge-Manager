package domain;

import java.io.*;
import java.sql.*;
import java.util.*;

import rules.RuleDAO;

import org.postgresql.ds.PGPoolingDataSource;

public class Database {
	
	private String AWS_USERNAME;
	private String AWS_PASSWORD;
	private String AWS_DB;
	private String AWS_DB_NAME;
	
	private Connection c;
	private Statement st;
	private PreparedStatement pst;
	private Utils uts;
	
	private Properties prop;
	private PGPoolingDataSource poolSource;
	
	
	public Database(Utils uts) {
		this.uts = uts;
		loadProperties();
		loadPoolSource();
		createIdTable();
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
			InputStream is = new FileInputStream("manager.properties");
			prop.load(is);
			AWS_USERNAME = prop.getProperty("aws_username");
			AWS_PASSWORD = prop.getProperty("aws_password");
			AWS_DB = prop.getProperty("aws_db");
			AWS_DB_NAME = prop.getProperty("aws_db_name");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadPoolSource() {
		poolSource = new PGPoolingDataSource();
		poolSource.setDataSourceName("AWS Data Source");
		poolSource.setServerName(AWS_DB);
		poolSource.setDatabaseName(AWS_DB_NAME);
		poolSource.setUser(AWS_USERNAME);
		poolSource.setPassword(AWS_PASSWORD);
		poolSource.setMaxConnections(20);
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
		c = null;
		try {
			c = poolSource.getConnection();
			st = c.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS ids ("
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
		c = null;
		try {
			c = poolSource.getConnection();
			st = c.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS associations ("
					  	  + "location varchar(100),"
					  	  + "actuator varchar(20),"
					  	  + "sensors varchar(200),"
					  	  + "rule varchar(50),"
					  	  + "registrations_left integer,"
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
		c = null;
		try {
			c = poolSource.getConnection();
			st = c.createStatement();
			String readIds = "SELECT servioticy_id FROM ids ORDER BY created DESC";
			if (n > 0) readIds += " LIMIT " + n;
			ResultSet rs = st.executeQuery(readIds);
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
	
	public String getLocation(String soID) {
		c = null;
		String res = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT location FROM ids WHERE servioticy_id = ?");
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
		c = null;
		String[] associations = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT associations FROM ids WHERE servioticy_id = ?");
			pst.setString(1, soID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
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
		c = null;
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
		} catch(SQLException e) {
				e.printStackTrace();
		} finally {
			closeConnection(c);
		}
	}
	
	public ArrayList<RuleDAO> getCompletedRules(String location) {
		c = null;
		ArrayList<RuleDAO> rules = new ArrayList<RuleDAO>();
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("SELECT actuator, sensors, rule FROM associations WHERE location = ? AND registrations_left = 0");
			pst.setString(1, location);
			ResultSet rs = pst.executeQuery();
			String actuator = null, sensors = null, rule = null;
			while (rs.next()) {
				actuator = rs.getString("actuator");
				sensors = rs.getString("sensors");
				rule = rs.getString("rule");
				rules.add(new RuleDAO(actuator, sensors, rule));
			}
			pst = c.prepareStatement("UPDATE associations SET registrations_left = -1 WHERE actuator = ? AND location = ? AND rule = ?");
			pst.setString(1, actuator);
			pst.setString(2, location);
			pst.setString(3, rule);
			pst.executeUpdate();
		} catch(SQLException e) {
				e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return rules;
	}
	
	private void initialiseCounters() {
		
		Map<String, Integer> rules = new HashMap<String, Integer>();
		
		rules.put("AirConditioning", 2);
		rules.put("CloseDoor", 2);
		rules.put("SwitchOffLight", 1);
		
		c = null;
		try {
			c = poolSource.getConnection();
			pst = c.prepareStatement("UPDATE associations SET sensors = ?, registrations_left = ? WHERE rule = ?");
			
			for (Map.Entry<String, Integer> entry : rules.entrySet()) {
				pst.setString(1, "{}");
				pst.setInt(2, entry.getValue());
				pst.setString(3, entry.getKey());
				pst.executeUpdate();
			}
		} catch(SQLException e) {
				e.printStackTrace();
		} finally {
			closeConnection(c);
		}
	}
}
