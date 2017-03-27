package data;

import org.postgresql.ds.PGPoolingDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class IdentifierDB {
	
	private static IdentifierDB instance = new IdentifierDB();
	
	private IdentifierDB() {
		initComponents();
	}
	
	public static IdentifierDB getInstance() {
		return instance;
	}
	
	private String DB_USERNAME;
	private String DB_PASSWORD;
	private String DB;
	private String DB_NAME;
	private String DB_TABLE;
	
	private Statement st;
	private PreparedStatement pst;
	private PGPoolingDataSource poolSource;
	
	
	private void initComponents() {
		loadProperties();
		loadPoolSource();
		//createIdTable();
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
        Properties prop = new Properties();
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
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
        return res;
    }

}
