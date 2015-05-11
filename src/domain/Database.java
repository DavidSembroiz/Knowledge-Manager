package domain;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class Database {
	
	private static final String AWS_USERNAME = "root";
	private static final String AWS_PASSWORD = "rootpass";
	private static final String AWS_DB = "sensors.cguux3hin2bh.eu-west-1.rds.amazonaws.com:5432/data";
	
	private Connection connect;
	private Statement st;

	public Database() {
		connect();
		createTable();
	}
	
	private void connect() {
		try {
			Class.forName("org.postgresql.Driver");
			Properties props = new Properties();
			props.setProperty("user", AWS_USERNAME);
			props.setProperty("password", AWS_PASSWORD);
			connect = DriverManager.getConnection("jdbc:postgresql://" + AWS_DB, props);
		} catch (ClassNotFoundException|SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createTable() {
		try {
			st = connect.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS ids ("
					  	  + "servioticy_id varchar(50) primary key,"
					  	  + "model json,"
					  	  + "datafile json,"
					  	  + "location varchar(100));";
			st.executeUpdate(create);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> queryIds() {
		ArrayList<String> ret = new ArrayList<String>();
		try {
			
			st = connect.createStatement();
			String readIds = "SELECT servioticy_id FROM ids";
			ResultSet rs = st.executeQuery(readIds);
			while(rs.next()) {
				ret.add(rs.getString("servioticy_id"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String getDatafile(String soID) {
		try {
			
			st = connect.createStatement();
			String get = "SELECT datafile->'channels' as jsondata FROM ids WHERE servioticy_id = '" + soID + "'";
			ResultSet rs = st.executeQuery(get);
			while(rs.next()) return rs.getString("jsondata");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
