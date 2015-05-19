package domain;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Database {
	
	private String AWS_USERNAME;
	private String AWS_PASSWORD;
	private String AWS_DB;
	
	private Connection connect, connectListener;
	private Statement st;
	
	private Properties prop;
	
	
	public Database() {
		loadProperties();
		connect();
		createTable();
	}
	
	public Connection getConnectionListener() {
		return connectListener;
	}
	
	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("manager.properties");
			prop.load(is);
			AWS_USERNAME = prop.getProperty("aws_username");
			AWS_PASSWORD = prop.getProperty("aws_password");
			AWS_DB = prop.getProperty("aws_db");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void connect() {
		try {
			Class.forName("org.postgresql.Driver");
			Properties props = new Properties();
			props.setProperty("user", AWS_USERNAME);
			props.setProperty("password", AWS_PASSWORD);
			connect = DriverManager.getConnection("jdbc:postgresql://" + AWS_DB, props);
			connectListener = DriverManager.getConnection("jdbc:postgresql://" + AWS_DB, props);
			System.out.println("Connected to AWS RDS");
		} catch (ClassNotFoundException|SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createTable() {
		try {
			st = connect.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS ids ("
					  	  + "servioticy_id varchar(50) primary key,"
					  	  + "model varchar(30),"
					  	  //+ "datafile json,"
					  	  + "location varchar(100),"
					  	  + "created timestamp default current_timestamp(2));";
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
