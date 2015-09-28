package domain;

import iot.Manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.*;

/**
 * 
 * @author David
 *
 *TODO utils might not be necessary
 *
 */


public class Mqtt {
	
	/**
	 * High enough value to query all the ID database
	 */
	
	private static final int QUERY_ALL = 99999999;
	
	private String ADDRESS;
	private String USERNAME;
	private String PASSWORD;
	private String APIKEY;
	private String CLIENTID;
	private MqttConnectOptions connOpts;
	private MqttClient client;
	private MqttCb callback;
	private ArrayList<String> ids;
	private String topic;
	private Utils uts;
	private Properties prop;
	private Manager manager;

	public Mqtt(Manager m, Database awsdb) {
		ids = new ArrayList<String>();
		uts = Utils.getInstance();
		this.manager = m;
		loadProperties();
		connect();
		subscribe(awsdb.queryIds(QUERY_ALL));
	}
	
	/**
	 * Reads all the needed properties for the MQTT connection
	 */
	
	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("manager.properties");
			prop.load(is);
			ADDRESS = prop.getProperty("so_address");
			USERNAME = prop.getProperty("so_username");
			PASSWORD = prop.getProperty("so_password");
			APIKEY = prop.getProperty("so_apikey");
			CLIENTID = prop.getProperty("so_clientid");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Connects to the ServIoTicy MQTT endpoint
	 * 
	 */
	private void connect() {
		connOpts = new MqttConnectOptions();
		
		/**
		 * Session has to be set to TRUE
		 */
		
		connOpts.setCleanSession(true);
		connOpts.setUserName(USERNAME);
		connOpts.setPassword(PASSWORD.toCharArray());
		try {
			client = new MqttClient(ADDRESS, CLIENTID);
			callback = new MqttCb(manager);
			client.setCallback(callback);
			client.connect(connOpts);
			if (client.isConnected()) System.out.println("Connected to ServIoTicy");
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Subscribes to the last n IDs inserted in the database
	 * 
	 */
	/*public void subscribe(int n) {
		ids = awsdb.queryIds(n);
		for (String id : ids) {
			topic = APIKEY + "/" + id + "/streams/data/updates";
			try {
				client.subscribe(topic);
				
				System.out.println("Subscribed to SO " + uts.extractIdFromTopic(topic));
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	public void subscribe(ArrayList<String> ids) {
		for (String id : ids) {
			topic = APIKEY + "/" + id + "/streams/data/updates";
			try {
				client.subscribe(topic);
				
				System.out.println("Subscribed to SO " + uts.extractIdFromTopic(topic));
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public ArrayList<String> getIds() {
		return ids;
	}
	
	/**
	 * Disconnects the client from the ServIoTicy endpoint
	 * 
	 */
	public void disconnect() {
		try {
			client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
}
