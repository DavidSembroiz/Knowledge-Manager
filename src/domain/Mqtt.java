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


public class Mqtt extends Thread {
	
	private static final int QUERY_ALL = -1;
	
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
	private Database awsdb;
	private Properties prop;
	private Manager manager;

	public Mqtt(Manager m, Utils u, Database awsdb) {
		ids = new ArrayList<String>();
		uts = u;
		this.awsdb = awsdb;
		this.manager = m;
		loadProperties();
		connect();
		subscribe(QUERY_ALL);
	}
	
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
		connOpts.setCleanSession(false);
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
	 * Subscribes to all the sensor ID availables
	 * 
	 * 
	 * TODO change topic string depending on data model
	 * @param n 
	 */
	public void subscribe(int n) {
		
		ids = awsdb.queryIds(n);
		for (String id : ids) {
			topic = APIKEY + "/" + id + "/streams/weather/updates";
			try {
				client.subscribe(topic, 0);
				
				System.out.println("Subscribed to SO " + uts.extractIdFromTopic(topic));
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Main loop to maintain the listener running for receiving updates
	 * 
	 */
	public void run() {
		while(true) {
			// Maintain class open for receive updated messages
		}
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
