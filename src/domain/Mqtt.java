package domain;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.*;


public class Mqtt extends Thread {
	
	private static final String ADDRESS = "tcp://api.servioticy.com:1883";
	private static final String USERNAME = "compose";
	private static final String PASSWORD = "shines";
	private static final String APIKEY = "Y2VlMTBkYjEtNDVjNi00N2E5LTk0YjEtMzBmOTI0NTUzMzg1YjU2YmE5NzUtOTEyZC00M2FmLWE0MGEtMzJiZjcwNWFkMDg0";
	static final String CLIENTID = "knowledgeManager";
	private MqttConnectOptions connOpts;
	private MqttClient client;
	private MqttCb callback;
	private ArrayList<String> ids;
	private String topic;
	private Utils uts;
	private Database awsdb;

	public Mqtt(Database awsdb) {
		ids = new ArrayList<String>();
		uts = new Utils();
		this.awsdb = awsdb;
		connect();
		subscribe();
	}
	
	/**
	 * Connects to the ServIoTicy MQTT endpoint
	 * 
	 */
	private void connect() {
		connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		connOpts.setUserName(USERNAME);
		connOpts.setPassword(PASSWORD.toCharArray());
		try {
			client = new MqttClient(ADDRESS, CLIENTID);
			callback = new MqttCb(uts, awsdb);
			client.setCallback(callback);
			client.connect(connOpts);
			System.out.println("Connected");
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Subscribes to all the sensor ID availables
	 * 
	 * TODO Read sensor IDs from Cloud Database
	 * TODO change topic string depending on data model
	 */
	private void subscribe() {
		
		//ids = uts.readSOIdsFromFile();
		ids = awsdb.queryIds();
		String soID = null;
		for (int i = 0; i < ids.size(); ++i) {
			topic = APIKEY + "/" + ids.get(i) + "/streams/weather/updates";
			try {
				client.subscribe(topic, 0);
				
				soID = uts.extractIdFromTopic(topic);
				
				System.out.println("Subscribed to SO " + uts.extractIdFromTopic(topic));
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Data retrieval example
		 */
		
		String data = awsdb.getDatafile(soID);
		System.out.println(data);
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
