package domain;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.postgresql.ds.PGPoolingDataSource;

public class Database {
	
	private String AWS_USERNAME;
	private String AWS_PASSWORD;
	private String AWS_DB;
	private String AWS_DB_NAME;
	
	private Connection c;
	private Statement st;
	
	private Properties prop;
	private PGPoolingDataSource poolSource;
	
	
	public Database() {
		loadProperties();
		loadPoolSource();
		createTable();
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
			} catch (SQLException e) {e.printStackTrace();}
		}
	}
	
	private void createTable() {
		c = null;
		try {
			c = poolSource.getConnection();
			st = c.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS ids ("
					  	  + "servioticy_id varchar(50) primary key,"
					  	  + "model varchar(30),"
					  	  + "location varchar(100),"
					  	  + "created timestamp default current_timestamp(2));";
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
			st = c.createStatement();
			String get = "SELECT location FROM ids WHERE servioticy_id = '" + soID + "'";
			ResultSet rs = st.executeQuery(get);
			if (rs.next()) res = rs.getString("location");
		} catch(SQLException e) {
				e.printStackTrace();
		} finally {
			closeConnection(c);
		}
		return res;
	}
}
